package storageServer;

import accessServer.domain.entities.FileDirInfo;

import java.io.File;
import java.io.FileReader;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StorageServerInterface{

    // basic file operations
    public abstract boolean createFile(final String absoluteUri) throws Exception;
    public abstract boolean fileExists(final String absoluteUri) throws Exception;
    public abstract boolean deleteFile(final String absoluteUri) throws Exception;
    public abstract String getLastModifiedTime(final String absoluteUri) throws Exception;

    // upload and download operations
    public abstract boolean uploadWithOverride(final String absoluteUri,File file) throws Exception;
    public abstract File download(final String absoluteUri) throws Exception;

    // list Dir
    public abstract String[] listFiles(final String absoluteUri)throws Exception;
    public abstract String[] listSubDirs(final String absoluteUri) throws Exception;

    // directory operations
    public abstract boolean createDir(final String absoluteUri) throws Exception;
    public abstract boolean deleteDir(final String absoluteUri) throws Exception;
    public abstract boolean dirExists(final String absoluteUri) throws Exception;
}
