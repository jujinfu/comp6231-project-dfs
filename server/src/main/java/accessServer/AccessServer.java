package accessServer;

import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;
import accessServer.domain.repositories.FileDirInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.FirebirdDialect;
import storageServer.StorageServer;
import storageServer.StorageServerInterface;

import javax.persistence.Access;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AccessServer extends UnicastRemoteObject implements AccessServerInterface, StorageManagementInterface {

    private StorageServer storageServer = new StorageServer();
    //private EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
    private FileDirInfoRepository fileDirInfoRepository;

    private ServerSocket ss;
    private ArrayList<Socket> clients;

    public AccessServer() throws RemoteException {
        super();
    }

    private String getServerConnection(String clientAddress) {
        if (clientAddress.contains("location1")) {
            return "server1";
        } else if (clientAddress.contains("location2")) {
            return "server2";
        }
        return "server1";
    }

    private String getFileName(String uri) {
        return uri.substring(uri.lastIndexOf("\\") + 1, uri.length());
    }

    private String getUpperParent(String uri) {
        String parentAbs = getFileParent(uri);
        return StringUtils.isEmpty(parentAbs) ? "\\" : uri.substring(parentAbs.lastIndexOf("\\") + 1, parentAbs.length());
    }

    private String getFileParent(String uri) {
        return uri.substring(0, uri.lastIndexOf("\\"));
    }


    @Override
    public boolean createFile(String uri) throws RemoteException {
        /*
        TODO
         1. Check if file already exists in local db
         2. if yes, check if parent are the same
         3. If parent are not the same, check if parent exists, if yes, create file, else throw path not found exception
         4. step 2, If parent are not the same, keep going to check if whole path of parent are exists
         5. If all dirs in path are exists, create
         6. Otherwise, throw path not found exception
         7. to create, run create from storage server
         8. Save DB records
         9. return result
        */

//        FileDirInfo file = em
//                .createNamedQuery("FileDirInfo.fileExists",FileDirInfo.class)
//                .setParameter("name", getFileName(uri))
//                .setParameter("parent",getFileParent(uri))
//                .getSingleResult();
        List<FileDirInfo> file = FileDirInfoRepository.getFileByNameParentName(getFileName(uri), getFileParent(uri));
        if (file.size() == 0) {
            throw new RemoteException("File not found in db");
        }

        return false;
    }


    @Override
    public boolean fileExists(String uri) throws RemoteException {
        //Algorithm needed

//        FileDirInfo file = em
//                .createNamedQuery("FileDirInfo.fileExists",FileDirInfo.class)
//                .setParameter("name", getFileName(uri))
//                .setParameter("parent",getFileParent(uri))
//                .getSingleResult();
//
//        return file==null;
        List<FileDirInfo> file = FileDirInfoRepository.getFileByNameParentName(getFileName(uri), getFileParent(uri));
        return file == null;
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
        //get parent id by path

        //

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
