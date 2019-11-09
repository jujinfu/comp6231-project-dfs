package storageServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class StorageServer implements StorageServerInterface {

    // file path based on linux currently
    private final Path serverStorageRoot= Paths.get("/tmp/data");

    public StorageServer() throws RemoteException {
        super();
        if(Files.notExists(serverStorageRoot)){
            throw new RemoteException("Server root not found");
        }
    }

    @Override
    public File openFile(String uri) throws RemoteException {
        /*
        TODO
         1. check if file exits
         2. if no, throw file not found in storage, else continue
         3. Read and return file
        */
        if(!uri.startsWith(serverStorageRoot.toString()))
            uri=serverStorageRoot.toString()+uri;
        if(!Files.exists(Paths.get(uri))){
            throw new RemoteException("File not found in DB");
        }
        File f=new File(uri);
        return f;
    }

    @Override
    public boolean saveFile(String uri, int offset, byte[] content) throws RemoteException {
        /*
        TODO
         1. check if file exists
         2. if no, throw file not found, else, continue
         3. check if file is having a lock other than current user
         4. if yes, throw file is being used, else continue
         5. update content
         6. update metadata in db
        */
        if(!Files.exists(Paths.get(uri))){
            throw new RemoteException("File not found in DB");
        }


        return false;
    }

    @Override
    public boolean closeFile(String uri) throws RemoteException {
        /*
        TODO
         1. check if file exists
         2. if no, throw file not found, else, continue
         3. check if file is have a lock other than current user
         4. if yes, throw file is being used, else, continue
         5. remove lock
        */
        return false;
    }

    @Override
    public boolean createFile(String uri) throws RemoteException {
        /*
        TODO
         1. check if file exists, and check if path not exists
         2. if yes, throw FileExists or path not found
         3. if no, create file in target location with desired name
         4. register with DB
        */
        try{
            if(!uri.startsWith(serverStorageRoot.toString()))
                uri=serverStorageRoot.toString()+uri;
            String filePath=uri.substring(0,uri.lastIndexOf('/'));
            // !db.directories.any(uri);
            if(!Files.exists(Paths.get(filePath))){
                throw new IOException("path not found");
            }
            // db.files.any(uri);
            if(Files.exists(Paths.get(uri))){
                throw new IOException("file already exists");
            }
            Files.createFile(Paths.get(uri));
            // db.files.add(uri);
            return true;
        }catch(IOException e){
            throw new RemoteException(e.getMessage());
        }
    }
    @Override
    public boolean fileExists(String uri) throws RemoteException{
        if(!uri.startsWith(serverStorageRoot.toString()))
            uri=serverStorageRoot.toString()+uri;
        return Files.exists(Paths.get(uri));
    }
    @Override
    public boolean deleteFile(String uri) throws RemoteException {
        /*
        TODO
         1. check if file exists
         2. if no, throw file not found
         3. if yes, check if file is being locked
         4. if locked, throw file is currently being used
         5. if not locked, remove file and remove DB record
        */
        try {
            if(!uri.startsWith(serverStorageRoot.toString()))
                uri=serverStorageRoot.toString()+uri;
            // db.files.any(uri);
            if (!Files.exists(Paths.get(uri))) {
                throw new IOException("file not exists");
            }
            // db.file_locks.any(uri)
            if(!Files.isReadable(Paths.get(uri)) || !Files.isWritable(Paths.get(uri))){
                throw new IOException("file is currently being used");
            }
            // db.files.remove(uri)
            Files.delete(Paths.get(uri));
            return true;
        }catch(IOException e){
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public String getFileMeta(String uri) throws RemoteException {
        //TODO
        //
        return null;
    }

    @Override
    public File download(String uri) throws RemoteException {
        //TODO
        // make a copy of the file and send to client
        return null;
    }

    @Override
    public String[] listFiles(String uri) throws RemoteException {
        //TODO here we shall grab the directory content from database

        //this is just a mock up
        ArrayList<String> content=new ArrayList<String>();
        try {
            Files.list(serverStorageRoot)
                    .filter(Files::isRegularFile)
                    .forEach(x -> content.add(x.toString()));

            return (String[])content.toArray();
        }catch (IOException e){
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public String[] listSubDirs(String uri) throws RemoteException {
        //TODO we should also grab list from DB

        try {
            ArrayList<String> content=new ArrayList<String>();
            Files.list(serverStorageRoot)
                    .filter(Files::isDirectory)
                    .forEach(x->content.add(x.toString()));
            return (String[])content.toArray();
        }catch(IOException e){
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean createDir(String uri) throws RemoteException {
        //TODO
        // 1. check if parent path exists
        // 2. If no, throw path not found, else, continue
        // 3. check if dir exists
        // 4. if yes, throw dir exists, else, continue
        // 5. create dir in target location with desired name
        // 6. register with DB
        return false;
    }

    @Override
    public boolean deleteDir(String uri) throws RemoteException {
        //TODO
        // 1. check if dir exists
        // 2. if no, throw dir not found, else, continue
        // 3. check if dir is empty
        // 4. if no, throw dir not empty, else, continue
        // 6. remove dir and remove db record
        return false;
    }
}
