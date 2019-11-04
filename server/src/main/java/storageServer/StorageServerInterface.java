package storageServer;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StorageServerInterface{

    // basic file operations
    public abstract File openFile(final String absoluteUri) throws RemoteException;
    public abstract boolean saveFile(final String absoluteUri,byte[] content) throws RemoteException;
    public abstract boolean closeFile(final String absoluteUri) throws RemoteException;
    public abstract boolean createFile(final String absoluteUri) throws RemoteException;
    public abstract boolean deleteFile(final String absoluteUri) throws RemoteException;
    public abstract String getFileMeta(final String absoluteUri) throws RemoteException;

    // download operations
    public abstract File download(final String absoluteUri) throws RemoteException;

    // directory operations
    public abstract String[] listFiles(final String absoluteUri)throws RemoteException;
    public abstract String[] listSubDirs(final String absoluteUri) throws RemoteException;
    public abstract boolean createDir(final String absoluteUri) throws RemoteException;
    public abstract boolean deleteDir(final String absoluteUri) throws RemoteException;
}
