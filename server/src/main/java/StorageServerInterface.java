import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StorageServerInterface{

    // basic file operations
    public abstract File openFile(final String uri) throws RemoteException;
    public abstract boolean saveFile(final String uri,byte[] content) throws RemoteException;
    public abstract boolean closeFile(final String uri) throws RemoteException;
    public abstract boolean createFile(final String uri) throws RemoteException;
    public abstract boolean deleteFile(final String uri) throws RemoteException;
    public abstract String getFileMeta(final String uri) throws RemoteException;

    // download operations
    public abstract File download(final String uri) throws RemoteException;

    // directory operations
    public abstract String[] listFiles(final String uri)throws RemoteException;
    public abstract String[] listSubDirs(final String uri) throws RemoteException;
    public abstract boolean createDir(final String uri) throws RemoteException;
    public abstract boolean deleteDir(final String uri) throws RemoteException;
}
