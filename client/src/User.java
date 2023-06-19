import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class User extends UnicastRemoteObject implements Client{
    private Long id;
    private String username;
    private String password;
    private boolean admin;

    public User(String username, String password) throws RemoteException {
        super();
        this.username = username;
        this.password = password;
    }

    public User(Long id, String username, String password, boolean admin) throws RemoteException {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.admin = admin;
    }
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", admin=" + admin +
                '}';
    }

}
