import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote{

    //basic file operations
    public abstract File openFile(final String uri) throws RemoteException;
    public abstract boolean saveFile(final String uri,byte[] content) throws RemoteException;
    public abstract boolean closeFile(final String uri) throws RemoteException;
    public abstract boolean deleteFile(final String uri) throws RemoteException;
    public abstract String getFileMeta(final String uri) throws RemoteException;

    //upload & download operations
    public abstract void download(final String uri) throws RemoteException;
    public abstract void upload(final String uri) throws  RemoteException;

    //directory operations
    public abstract String listDirectory(final String uri)throws RemoteException;
}
