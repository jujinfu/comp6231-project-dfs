package accessServer;

import accessServer.domain.entities.FileDirInfo;
import accessServer.domain.repositories.FileDirInfoRepository;
import org.apache.commons.lang3.StringUtils;
import storageServer.StorageServer;
import storageServer.StorageServerInterface;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class AccessServer extends UnicastRemoteObject implements StorageManagementInterface, StorageServerInterface {

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
    public boolean createFile(String uri) throws Exception {
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

        if (FileDirInfoRepository.exists(uri)) {
            throw new RemoteException("File exists in db");
        }
        try{
            if(storageServer.createFile(uri)){
            }

        }
        catch(Exception e){
            throw new Exception(e.getMessage());
        }
        return false;
    }


    @Override
    public boolean fileExists(String uri) throws Exception {
        return FileDirInfoRepository.exists(uri);
    }

    @Override
    public boolean deleteFile(String uri) throws Exception {
        return false;
    }

    @Override
    public String getLastModifiedTime(String absoluteUri) throws Exception {
        return null;
    }

    @Override
    public boolean uploadWithOverride(String absoluteUri, File file) throws Exception {
        return false;
    }


    @Override
    public File download(String uri) throws Exception {
        return null;
    }

    @Override
    public String[] listFiles(String uri) throws Exception {
        return new String[0];
    }

    @Override
    public String[] listSubDirs(String uri) throws Exception {
        return new String[0];
    }

    @Override
    public boolean createDir(String uri) throws Exception {
        return false;
    }

    @Override
    public boolean deleteDir(String uri) throws Exception {
        //get parent id by path

        //

        return false;
    }

    @Override
    public boolean dirExists(String absoluteUri) throws Exception {
        return false;
    }

    @Override
    public void syncFile(String fromAbsoluteUri, String toAbsoluteUri) {

    }

    @Override
    public void updateFileMeta(String absoluteUri, String property) {

    }
}
