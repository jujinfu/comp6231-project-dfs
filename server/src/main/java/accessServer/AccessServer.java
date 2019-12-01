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
import java.util.stream.Collectors;


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
        if (!Utities.isUriValid(uri)) {
            throw new IllegalArgumentException("Path is not valid or file name is empty, uri:" + uri);
        }
        FileDirInfo parent = FileDirInfoRepository.getParentByUri(uri);
        if(!FileDirInfoRepository.exists(parent)){
            throw new RemoteException("parent not exist in db, uri: " + uri);
        }
        FileDirInfo file = FileDirInfoRepository.getFile(uri);
        if(fileExists(file)){
            throw new RemoteException("File exists in db");
        }
        try{
            if(storageServer.createFile(uri)){
                FileDirInfoRepository.createNewFile(uri);
            }
        }
        catch(Exception e){
            throw new Exception(e.getMessage());
        }
        return true;
    }


    @Override
    public boolean fileExists(String uri) throws Exception {
        if(!Utities.isUriValid(uri)){
            throw new IllegalArgumentException("Path is invalid uri: "+ uri);
        }
        FileDirInfo fileDirInfo = FileDirInfoRepository.getFile(uri);
        return FileDirInfoRepository.exists(fileDirInfo);
    }

    public boolean  fileExists(FileDirInfo file){
        return FileDirInfoRepository.exists(file);
    }

    @Override
    public boolean deleteFile(String uri) throws Exception {
        if(!Utities.isUriValid(uri)){
            throw new IllegalArgumentException("Path is invalid uri: "+ uri);
        }
        FileDirInfo fileDirInfo = FileDirInfoRepository.getFile(uri);
        if(fileDirInfo.isDir()){
            throw new IllegalArgumentException("URI is a directory");
        }
        if(!fileExists(fileDirInfo)){
            throw new RemoteException("file not found");
        }
        if(storageServer.deleteFile(uri)
        ){
            FileDirInfoRepository.deleteFileById(fileDirInfo.getId());
        }
        return true;
    }

    @Override
    public String getLastModifiedTime(String absoluteUri) throws Exception {
        throw new UnsupportedOperationException("not implemented");
        //return null;
    }

    @Override
    public boolean uploadWithOverride(String absoluteUri, File file) throws Exception {
        throw new UnsupportedOperationException("not implemented");
        //return false;
    }


    @Override
    public File download(String uri) throws Exception {
        throw new UnsupportedOperationException("not implemented");
        //return null;
    }

    @Override
    public String[] listFiles(String uri) throws Exception {
        FileDirInfo dir = FileDirInfoRepository.getFile(uri);
        if (!fileExists(dir)) {
            throw new RemoteException("Directory not exist in db, uri: " + uri);
        }
        if (!dir.isDir()) {
            throw new IllegalArgumentException("URI is a file");
        }
        List<FileDirInfo> sub = FileDirInfoRepository.getChildren(dir);
        String[] files = sub.stream()
                .filter(f -> !f.isDir())
                .map(f -> f.getName())
                .toArray(String[]::new);
        return files;
    }

    @Override
    public String[] listSubDirs(String uri) throws Exception {
        FileDirInfo dir = FileDirInfoRepository.getFile(uri);
        if (!fileExists(dir)) {
            throw new RemoteException("Directory not exist in db, uri: " + uri);
        }
        if (!dir.isDir()) {
            throw new IllegalArgumentException("URI is a file");
        }
        List<FileDirInfo> sub = FileDirInfoRepository.getChildren(dir);
        String[] files = sub.stream()
                .filter(f -> f.isDir())
                .map(f -> f.getName())
                .toArray(String[]::new);
        return files;
    }

    @Override
    public boolean createDir(String uri) throws Exception {
        if (!Utities.isUriValid(uri)) {
            throw new IllegalArgumentException("Path is not valid or file name is empty, uri:" + uri);
        }
        FileDirInfo parent = FileDirInfoRepository.getParentByUri(uri);
        if(!dirExists(parent)){
            throw new RemoteException("Directory parent not exist in db, uri: " + uri);
        }
        FileDirInfo file = FileDirInfoRepository.getFile(uri);
        if(fileExists(file)){
            throw new RemoteException("Directory exists in db");
        }
        try{
            if(storageServer.createDir(uri)
            ){
                FileDirInfoRepository.createNewDir(uri);
            }
        }
        catch(Exception e){
            throw new Exception(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean deleteDir(String uri) throws Exception {
        if(!Utities.isUriValid(uri)){
            throw new IllegalArgumentException("Path is invalid uri: "+ uri);
        }
        FileDirInfo dir = FileDirInfoRepository.getFile(uri);
        if(!dirExists(dir)){
            throw new RemoteException("Directory not exists in db");
        }
        List<FileDirInfo> subFiles = FileDirInfoRepository.getChildren(dir);
        if(subFiles.size() > 0){
            throw new RemoteException("Directory is not empty");
        }
        if(storageServer.deleteDir(uri)
        ){
            FileDirInfoRepository.deleteFileById(dir.getId());
        }
        return true;
    }

    @Override
    public boolean dirExists(String absoluteUri) throws Exception {
        FileDirInfo dir = FileDirInfoRepository.getFile(absoluteUri);
        return fileExists(dir) && dir.isDir();
    }

    public boolean dirExists(FileDirInfo dir) throws Exception {
        return fileExists(dir) && dir.isDir();
    }

    @Override
    public void syncFile(String fromAbsoluteUri, String toAbsoluteUri) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void updateFileMeta(String absoluteUri, String property) {
        throw new UnsupportedOperationException("not implemented");
    }
}
