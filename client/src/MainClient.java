import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class MainClient {
    public static void main(String[] args) {
        try {
            Server server = (Server) Naming.lookup("rmi://localhost/vote");
            System.out.println("Client..........");
            new Login(server);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }
}