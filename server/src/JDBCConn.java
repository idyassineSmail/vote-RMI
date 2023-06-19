import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class JDBCConn {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vote?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static String hashedPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String[] register(String username, String password) {
        String response[] = null;

        try {
            String passwordHashed = hashedPassword(password);

            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO users (username, password) " +
                            "VALUES (?,?)");

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHashed);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                response = login(username, password);
            }
            stmt.close();
            conn.close();
        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public static String[] login(String username, String password) {
        String response[] = null;

        try {
            String passwordHashed = hashedPassword(password);

            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password = ?");

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHashed);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                response = new String[]{String.valueOf(resultSet.getLong("id")),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        String.valueOf(resultSet.getBoolean("admin"))};
            }
            stmt.close();
            conn.close();
        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public static List<String> listPolls() {
        List<String> polls = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM polls");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String poll = resultSet.getLong("id") + "," +
                        resultSet.getString("title") + "," +
                        resultSet.getDate("end_date");
                polls.add(poll);
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return polls;
    }

    public static List<String> addOptions(String title) {
        List<String> options = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT o._option, p.description, COUNT(o._option) " +
                            "FROM polls p " +
                            "JOIN options o ON p.id = o.poll_id " +
                            "JOIN votes v ON p.id = v.poll_id " +
                            "AND o.id = v.option_id " +
                            "WHERE p.title = ? " +
                            "GROUP BY o._option");
            preparedStatement.setString(1, title);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                options.add(resultSet.getString("_option") + ";" +
                        resultSet.getInt("COUNT(o._option)") + ";" +
                        resultSet.getString("description"));
            }
            preparedStatement = conn.prepareStatement("select o._option from options o, polls p where p.id = o.poll_id and p.title = ?;");
            preparedStatement.setString(1, title);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int i = 0;
                String _option = resultSet.getString("_option");
                for (String option : options) {
                    if (_option.equals(option.split(";")[0])) {
                        i++;
                        break;
                    }
                }
                if (i == 0) {
                    options.add(_option + ";" + "0");
                }
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return options;
    }

    public static boolean addVote(Long id, String title, String option) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT id FROM polls WHERE title=?");
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                preparedStatement = conn.prepareStatement("SELECT * FROM votes WHERE poll_id = ? AND user_id = ?");
                long pollId = resultSet.getLong("id");
                preparedStatement.setLong(1, pollId);
                preparedStatement.setLong(2, id);
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    preparedStatement = conn.prepareStatement("SELECT id FROM options WHERE poll_id = ? AND _option=?");
                    preparedStatement.setLong(1, pollId);
                    preparedStatement.setString(2, option);
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        preparedStatement = conn.prepareStatement("INSERT INTO votes(poll_id, user_id, option_id) VALUES (?,?,?)");
                        preparedStatement.setLong(1, pollId);
                        preparedStatement.setLong(2, id);
                        preparedStatement.setLong(3, resultSet.getLong("id"));
                        int addedRows = preparedStatement.executeUpdate();
                        if (addedRows > 0) {
                            return true;
                        }
                    }
                }
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean addPoll(String title, String description, String endDate, String options, Long id) {
        String[] _options = options.split(";");
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
            java.util.Date utilDate = dateFormat.parse(endDate);
            java.sql.Date end_date = new java.sql.Date(utilDate.getTime());
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO polls(title, description, end_date, creator_id) VALUES (?,?,?,?)");
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, end_date);
            preparedStatement.setLong(4, id);
            int addRows = preparedStatement.executeUpdate();
            if (addRows > 0) {
                preparedStatement = conn.prepareStatement("SELECT id FROM polls WHERE title=?");
                preparedStatement.setString(1, title);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Long poll_id = resultSet.getLong("id");
                    for (int i = 0; i < _options.length; i++) {
                        preparedStatement = conn.prepareStatement("INSERT INTO options(poll_id, _option) VALUES (?,?)");
                        preparedStatement.setLong(1, poll_id);
                        preparedStatement.setString(2, _options[i]);
                        addRows = preparedStatement.executeUpdate();
                        if (addRows <= 0) {
                            return false;
                        }
                    }
                }
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static String[] getPoll(String title) {
        String[] poll = new String[4];
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM polls WHERE title=?");
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                poll[0] = resultSet.getString("id");
                poll[1] = resultSet.getString("title");
                poll[2] = resultSet.getString("description");
                poll[3] = resultSet.getString("end_date");
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return poll;
    }

    public static boolean editePoll(String poll_id, String title, String description, String endDate, Long id) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
            java.util.Date utilDate = dateFormat.parse(endDate);
            java.sql.Date end_date = new java.sql.Date(utilDate.getTime());
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE polls SET title=?,description=?,end_date=?,creator_id=? WHERE id=?");
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, end_date);
            preparedStatement.setLong(4, id);
            preparedStatement.setLong(5, Long.parseLong(poll_id));
            int updateRows = preparedStatement.executeUpdate();
            if (updateRows > 0) {
                return true;
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean deletePoll(String title) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT id FROM polls WHERE title=?");
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Long poll_id = resultSet.getLong("id");
                preparedStatement = conn.prepareStatement("SELECT * FROM votes WHERE poll_id=?");
                preparedStatement.setLong(1, poll_id);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    preparedStatement = conn.prepareStatement("DELETE FROM votes WHERE poll_id=?");
                    preparedStatement.setLong(1, poll_id);
                    preparedStatement.executeUpdate();
                }
                preparedStatement = conn.prepareStatement("DELETE FROM options WHERE poll_id=?");
                preparedStatement.setLong(1, poll_id);
                int deleteRows = preparedStatement.executeUpdate();
                if (deleteRows > 0) {
                    preparedStatement = conn.prepareStatement("DELETE FROM polls WHERE id=?");
                    preparedStatement.setLong(1, poll_id);
                    deleteRows = preparedStatement.executeUpdate();
                    if (deleteRows > 0) {
                        return true;
                    }
                }
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
