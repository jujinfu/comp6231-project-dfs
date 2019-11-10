package accessServer;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AccessServerInterface extends Remote {

    /* wrap of Storage server */
    // basic file operations
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
