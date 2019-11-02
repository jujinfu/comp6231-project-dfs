import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements ServerInterface {
    Server() throws RemoteException {
        super();
    }

    public void download(String uri) throws RemoteException {
        System.out.println("Got request"+uri);
    }
    public void upload(String uri) throws RemoteException {

    }
}
