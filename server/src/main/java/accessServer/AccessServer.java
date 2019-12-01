package accessServer;

import accessServer.domain.entities.FileDirInfo;
import accessServer.domain.repositories.FileDirInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import storageServer.StorageServer;
import storageServer.StorageServerInterface;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AccessServer extends UnicastRemoteObject implements StorageManagementInterface, StorageServerInterface {

    private StorageServer storageServer = new StorageServer();
    public AccessServer() throws RemoteException {
        super();
    }

    @Override
    public boolean fileExists(String uri) throws Exception {
        if(!Utities.isUriValid(uri)){
            throw new IllegalArgumentException("Path is invalid uri: "+ uri);
        }
        FileDirInfo fileDirInfo = FileDirInfoRepository.getFile(uri);
        return FileDirInfoRepository.exists(fileDirInfo);
    }

    private boolean fileExists(FileDirInfo file){
        return FileDirInfoRepository.exists(file);
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
    public boolean createFile(String uri) throws Exception {
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
    public boolean deleteFile(String uri) throws Exception {
        log.debug("deleteFile uri =  " + uri);
        if(!Utities.isUriValid(uri)){
            throw new IllegalArgumentException("Path is invalid uri: {}"+ uri);
        }
        FileDirInfo fileDirInfo = FileDirInfoRepository.getFile(uri);
        log.debug("deleteFile FileDirInfoRepository.getFile(uri) = {} ", fileDirInfo);
        if(!fileExists(fileDirInfo)){
            log.debug("RemoteException(\"file not found\")");
            throw new RemoteException("file not found");
        }
        if(fileDirInfo.isDir()){
            log.debug("IllegalArgumentException(\"URI is a directory\")");
            throw new IllegalArgumentException("URI is a directory");
        }
      if(storageServer.deleteFile(uri)){
            log.debug("deleting file form db...");
            FileDirInfoRepository.deleteFileById(fileDirInfo.getId());
      }
        log.debug("deleting file done, return true");
        return true;
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
        //FileDirInfo file = FileDirInfoRepository.getFileDir(uri);
        if(dirExists(uri)){
            throw new RemoteException("Directory exists in db");
        }
        try{
            if(storageServer.createDir(uri)){
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
        FileDirInfo dir = FileDirInfoRepository.getFileDir(uri);
        if(!dirExists(uri)){
            throw new RemoteException("Directory not exists in db");
        }
        List<FileDirInfo> subFiles = FileDirInfoRepository.getChildren(dir);
        if(subFiles.size() > 0){
            throw new RemoteException("Directory is not empty");
        }
        if(storageServer.deleteDir(uri)){
            FileDirInfoRepository.deleteFileById(dir.getId());
        }
        return true;
    }

    @Override
    public boolean dirExists(String absoluteUri) throws Exception {
        FileDirInfo dir = FileDirInfoRepository.getFile(absoluteUri);
        return fileExists(dir) && dir.isDir();
    }

    private boolean dirExists(FileDirInfo dir) throws Exception {
        return fileExists(dir) && dir.isDir();
    }

    @Override
    public String syncCommand(String command) throws IOException {
        if(command.contains(" SYNC"))
            return "";
        //this part is for demo only. will need to add server reg in database in future
        String server1_internal_addr="172.31.10.138";
        String server1_public_addr="34.202.163.106";
        String server2_internal_addr="172.31.7.242";
        String server2_public_addr="35.156.172.221";
        int serverPort=9999;

        Socket clientSocket=null;
        InetAddress localAddr=InetAddress.getLocalHost();

        if(localAddr.getHostAddress().equals(server1_internal_addr)){
            clientSocket=new Socket(server2_public_addr,serverPort);
        }else if(localAddr.getHostAddress().equals(server2_internal_addr)){
            clientSocket=new Socket(server1_public_addr,serverPort);
        }
        PrintWriter printWriter=new PrintWriter(clientSocket.getOutputStream(), true);
        printWriter.println(command+" SYNC");
        String resp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        clientSocket.close();
        return "SYNC: "+resp;


    }

    //---------below are not supported by access server and client
    @Override
    public String getLastModifiedTime(String absoluteUri) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
        //return null;
    }

    @Override
    public boolean uploadWithOverride(String absoluteUri, File file) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
        //return false;
    }

    @Override
    public File download(String uri) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
        //return null;
    }

    @Override
    public void updateFileMeta(String absoluteUri, String property) {
        throw new UnsupportedOperationException("not implemented");
    }


}
