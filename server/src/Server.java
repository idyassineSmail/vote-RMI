import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Server extends Remote {
    String[] register(String username, String password) throws RemoteException;
    String[] login(String username, String password) throws RemoteException;
    List<String> listPolls()throws RemoteException;
    List<String> addOptions(String title) throws RemoteException;
    boolean addVote(Long id, String title, String option) throws RemoteException;
    boolean addPoll(String title, String description, String endDate, String options, Long id) throws RemoteException;
    String[] getPoll(String title) throws RemoteException;
    boolean editePoll(String poll_id, String title, String description, String endDate, Long id)throws RemoteException;
    boolean deletePoll(String title)throws RemoteException;
}
