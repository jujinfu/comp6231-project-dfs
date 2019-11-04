package accessServer;

import storageServer.StorageServer;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class AccessServer extends UnicastRemoteObject implements AccessServerInterface,StorageManagementInterface {

    private StorageServer storageServer=new StorageServer();
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

    @Override
    public File openFile(String uri) throws RemoteException {
        //TODO
        // 1. check if file exits in db
        // 2. if no, throw file not found in DB, else, continue
        // 3. check if file is being locked by other user
        // 4. if yes, throw file is being used, else continue
        // 5. lock the file and call storage


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
