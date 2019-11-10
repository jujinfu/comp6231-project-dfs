package accessServer;

import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;
import org.hibernate.dialect.FirebirdDialect;
import storageServer.StorageServer;
import storageServer.StorageServerInterface;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class AccessServer extends UnicastRemoteObject implements StorageServerInterface,StorageManagementInterface {

    private StorageServer storageServer=new StorageServer();
    private EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();

    public AccessServer() throws RemoteException {
        super();
    }

    private String getServerConnection(String clientAddress) {
        if(clientAddress.contains("location1")){
            return "server1";
        }else if(clientAddress.contains("location2")){
            return "server2";
        }
        return "server1";
    }

    private String getFileName(String uri){
        return uri.substring(uri.lastIndexOf("\\")+1,uri.length());
    }

    private String getFileParent(String uri){
        return uri.substring(0,uri.lastIndexOf("\\"));
    }
    private FileDirInfo getRootDir(){
        FileDirInfo file=em.createNamedQuery("FileDirInfo.getRootDir", FileDirInfo.class)
                .getSingleResult();
        return file;
    }

    private List<FileDirInfo> getChildren(FileDirInfo dir){
        List<FileDirInfo> files=em.createNamedQuery("FileDirInfo.getChildren", FileDirInfo.class)
                .setParameter("parent",dir).getResultList();

        return files;
    }

    @Override
    public boolean createFile(String uri) throws RemoteException {
        /*
        TODO
         1. Check if file already exists in local db
         2. if yes, throw file already exists, else continue
         3. check if parent dir exists
         4. if no, throw path not found, else continue
         5. run create from storage server
         6. Save DB records
         7. return result
        */

        FileDirInfo file = em
                .createNamedQuery("FileDirInfo.fileExists",FileDirInfo.class)
                .setParameter("name", getFileName(uri))
                .setParameter("parent",getFileParent(uri))
                .getSingleResult();
        if(file==null)
        {
            throw new RemoteException("File not found in db");
        }

        return false;
    }

    @Override
    public boolean fileExists(String uri) throws RemoteException {
        FileDirInfo file = em
                .createNamedQuery("FileDirInfo.fileExists",FileDirInfo.class)
                .setParameter("name", getFileName(uri))
                .setParameter("parent",getFileParent(uri))
                .getSingleResult();

        return file==null;
    }

    @Override
    public boolean deleteFile(String uri) throws RemoteException {
        return false;
    }

    @Override
    public String getLastModifiedTime(String absoluteUri) throws RemoteException {
        return null;
    }

    @Override
    public boolean uploadWithOverride(String absoluteUri, File file) throws RemoteException {
        return false;
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

    @Override
    public boolean dirExists(String absoluteUri) throws RemoteException {
        return false;
    }

    @Override
    public void syncFile(String fromAbsoluteUri, String toAbsoluteUri) {

    }

    @Override
    public void updateFileMeta(String absoluteUri, String property) {

    }
}
