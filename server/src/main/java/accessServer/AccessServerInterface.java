package accessServer;

import storageServer.StorageServerInterface;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AccessServerInterface extends Remote, StorageServerInterface {

}
