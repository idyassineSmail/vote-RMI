import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ServerImpl extends UnicastRemoteObject implements Server {

    protected ServerImpl() throws RemoteException {
    }

    @Override
    public String[] register(String username, String password) throws RemoteException {
        return JDBCConn.register(username, password);
    }

    @Override
    public String[] login(String username, String password) throws RemoteException {
        return JDBCConn.login(username, password);
    }

    @Override
    public List<String> listPolls() throws RemoteException {
        return JDBCConn.listPolls();
    }

    @Override
    public List<String> addOptions(String title) throws RemoteException {
        return JDBCConn.addOptions(title);
    }

    @Override
    public boolean addVote(Long id, String title, String option) throws RemoteException {
        return JDBCConn.addVote(id, title, option);
    }

    @Override
    public boolean addPoll(String title, String description, String endDate, String options, Long id) throws RemoteException {
        return JDBCConn.addPoll(title, description, endDate, options, id);
    }

    @Override
    public String[] getPoll(String title) throws RemoteException {
        return JDBCConn.getPoll(title);
    }

    @Override
    public boolean editePoll(String poll_id, String title, String description, String endDate, Long id) throws RemoteException {
        return JDBCConn.editePoll(poll_id, title, description, endDate, id);
    }

    @Override
    public boolean deletePoll(String title) throws RemoteException {
        return JDBCConn.deletePoll(title);
    }
}
