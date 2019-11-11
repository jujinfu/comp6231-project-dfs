package storageServer;

import com.mysql.cj.xdevapi.RemoveStatement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;

public class StorageServer implements StorageServerInterface {

    // file path based on linux currently
    private final Path serverStorageRoot= Paths.get("/tmp/data");

    public StorageServer() throws RemoteException {
        super();
        if(Files.notExists(serverStorageRoot)){
            //throw new RemoteException("Server root not found");
        }
    }

    @Override
    public boolean createFile(String uri) throws Exception {
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
            System.out.println(uri);
            if(Files.exists(Paths.get(uri))){
                throw new IOException("file already exists");
            }
            Files.createFile(Paths.get(uri));
            // db.files.add(uri);
            return true;
        }catch(IOException e){
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        }
    }
    @Override
    public boolean fileExists(String uri) throws Exception{
        if(!uri.startsWith(serverStorageRoot.toString()))
            uri=serverStorageRoot.toString()+uri;
        return Files.exists(Paths.get(uri));
    }
    @Override
    public boolean deleteFile(String uri) throws Exception {
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
    public String getLastModifiedTime(String uri) throws Exception {
        //TODO I dont know what we need to return here as metadata
        try {
            if (!uri.startsWith(serverStorageRoot.toString()))
                uri = serverStorageRoot.toString() + uri;
            if (!Files.exists(Paths.get(uri))) {
                throw new IOException("file not exists");
            }
            if (!Files.isReadable(Paths.get(uri))) {
                throw new IOException("file is currently being used");
            }

            return Files.getLastModifiedTime(Paths.get(uri)).toString();
        } catch (Exception e){
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean uploadWithOverride(String uri,File file) throws Exception{
        //TODO
        try {
            if (!uri.startsWith(serverStorageRoot.toString()))
                uri = serverStorageRoot.toString() + uri;

            String filePath=uri.substring(0,uri.lastIndexOf('\\'));
            // !db.directories.any(uri);
            //if(!Files.exists(Paths.get(filePath))){
             //   throw new IOException("path not found");
            //}

            Files.deleteIfExists(Paths.get(uri));
            Files.createFile(Paths.get(uri));

            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            StringBuilder contentBuilder=new StringBuilder();
            String s="";
            while ((s=fileReader.readLine())!=null ) {
                contentBuilder.append(s);
            }
            Files.write(Paths.get(uri), contentBuilder.toString().getBytes("utf-8"));
            return true;
        }catch(Exception e){
            throw new RemoteException(e.getMessage());
        }
    }
    @Override
    public File download(String uri) throws Exception {
        //TODO
        // make a copy of the file and send to client

        try{
            if (!uri.startsWith(serverStorageRoot.toString()))
                uri = serverStorageRoot.toString() + uri;
            if(!Files.exists(Paths.get(uri))){
                throw new IOException("path not found");
            }

            return new File(uri);
        }catch (Exception e){
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public String[] listFiles(String uri) throws Exception {
        //TODO here we shall grab the directory content from database

        try {
            if(!uri.startsWith(serverStorageRoot.toString()))
                uri=serverStorageRoot.toString()+uri;
            ArrayList<String> content= new ArrayList<>();
            Files.list(Paths.get(uri))
                    .filter(Files::isRegularFile)
                    .forEach(x -> content.add(x.toString()));
            String[] a=new String[content.size()];
            return content.toArray(a);
        }catch (IOException e){
            throw new RemoteException(e.getMessage());
        }
    }
    @Override
    public String[] listSubDirs(String uri) throws Exception {
        //TODO we should also grab list from DB
        try {
            if(!uri.startsWith(serverStorageRoot.toString()))
                uri=serverStorageRoot.toString()+uri;
            ArrayList<String> content=new ArrayList<String>();
            Files.list(Paths.get(uri))
                    .filter(Files::isDirectory)
                    .forEach(x->content.add(x.toString()));
            String[] a=new String[content.size()];
            return content.toArray(a);
        }catch(IOException e){
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean createDir(String uri) throws Exception {
        //TODO
        // 1. check if parent path exists
        // 2. If no, throw path not found, else, continue
        // 3. check if dir exists
        // 4. if yes, throw dir exists, else, continue
        // 5. create dir in target location with desired name
        // 6. register with DB

        try{
            if(!uri.startsWith(serverStorageRoot.toString()))
                uri=serverStorageRoot.toString()+uri;
            String dirPath=uri.substring(0,uri.lastIndexOf('\\'));
            // !db.directories.any(uri);
            //if(!Files.exists(Paths.get(dirPath))){
            //    throw new IOException("path not found");
            //}
            // db.files.any(uri);
            if(Files.exists(Paths.get(uri))){
                throw new IOException("folder already exists");
            }
            Files.createDirectory(Paths.get(uri));
            // db.files.add(uri);
            return true;
        }catch(IOException e){
            throw new RemoteException(e.getMessage());
        }
    }
    @Override
    public boolean dirExists(String uri) throws Exception{
        if(!uri.startsWith(serverStorageRoot.toString()))
            uri=serverStorageRoot.toString()+uri;
        return Files.exists(Paths.get(uri));
    }
    @Override
    public boolean deleteDir(String uri) throws RemoteException {
        //TODO
        // 1. check if dir exists
        // 2. if no, throw dir not found, else, continue
        // 3. check if dir is empty
        // 4. if no, throw dir not empty, else, continue
        // 6. remove dir and remove db record
        try {
            if(!uri.startsWith(serverStorageRoot.toString()))
                uri=serverStorageRoot.toString()+uri;

            if (!Files.exists(Paths.get(uri))) {
                throw new IOException("dir does not exist");
            }

            if(Files.list(Paths.get(uri)).count()!=0){
                throw new IOException("dir is not empty");
            }

            Files.delete(Paths.get(uri));
            return true;
        }catch(IOException e){
            throw new RemoteException(e.getMessage());
        }
    }
}
