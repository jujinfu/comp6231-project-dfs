import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class AccessServer extends UnicastRemoteObject implements AccessServerInterface {
    StorageServerInterface rmiServer;
    Registry registry;

    AccessServer() throws RemoteException {
        super();

    }
    public void start() throws Exception{
        // find the (local) object registry
        registry = LocateRegistry.getRegistry(9999);
        // find the server object
        rmiServer = (StorageServerInterface) (registry.lookup("server"));
        rmiServer.download("Hello World");
    }

    @Override
    public String getServerConnection(String clientAddress) {

        return null;
    }

    @Override
    public File openFile(String uri) throws RemoteException {
        return null;
    }

    @Override
    public boolean saveFile(String uri, byte[] content) throws RemoteException {
        return false;
    }

    @Override
    public boolean closeFile(String uri) throws RemoteException {
        return false;
    }

    @Override
    public boolean createFile(String uri) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteFile(String uri) throws RemoteException {
        return false;
    }

    @Override
    public String getFileMeta(String uri) throws RemoteException {
        return null;
    }

    @Override
    public File download(String uri) throws RemoteException {
        return null;
    }

    @Override
    public String[] listFiles(String uri) throws RemoteException {
        return new String[0];
    }

    @Override
    public String[] listSubDirs(String uri) throws RemoteException {
        return new String[0];
    }

    @Override
    public boolean createDir(String uri) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteDir(String uri) throws RemoteException {
        return false;
    }
}
