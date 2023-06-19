import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class MainServer {
    public static void main(String[] args) {
        try {
            Server Server = new ServerImpl();
            LocateRegistry.createRegistry(1099);
            Naming.bind("vote", Server);
            System.out.println("Server registered...");
        } catch (RemoteException | AlreadyBoundException | MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}