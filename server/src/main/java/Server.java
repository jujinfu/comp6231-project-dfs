import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements ServerInterface {
    Server() throws RemoteException {
        super();
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
    public boolean upload(String uri) throws RemoteException {
        return false;
    }

    @Override
    public String listDirectory(String uri) throws RemoteException {
        return null;
    }
}
