package accessServer;

import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;
import org.hibernate.dialect.FirebirdDialect;
import storageServer.StorageServer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class AccessServer extends UnicastRemoteObject implements AccessServerInterface,StorageManagementInterface {

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
        return uri.substring(uri.lastIndexOf("\\"),uri.length());
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
    private FileDirInfo dbFileExistis(String uri){
        String[] paths=uri.split("\\");


        return null;
    }

    @Override
    public File openFile(String uri) throws RemoteException {
        //TODO
        // 1. check if file exits in db
        // 2. if no, throw file not found in DB, else, continue
        // 3. check if file is locked
        // 4. if yes, throw file is being used, else continue
        // 5. lock the file and call storage
        FileDirInfo file = em
                .createNamedQuery("FileDirInfo.fileExists",FileDirInfo.class)
                .setParameter("name", getFileName(uri))
                .setParameter("parent",null)
                .getSingleResult();
        if(file==null)
        {
            throw new RemoteException("File not found in db");
        }
        if(file.getStatus().equals("Locked")){
            throw new RemoteException("File is being used");
        }


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

    @Override
    public void syncFile(String fromAbsoluteUri, String toAbsoluteUri) {

    }

    @Override
    public void updateFileMeta(String absoluteUri, String property) {

    }
}
