package storageServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StorageServerTest {

    private StorageServer storageServer;

    public StorageServerTest() throws Exception{
        storageServer=new StorageServer();
    }

    // caution: this will remove everything under root
    @BeforeEach
    public void cleanupServerRoot(){

    }

    @Test
    public void testCreate_exists_deleteFile() throws Exception{
        if(storageServer==null)
            storageServer=new StorageServer();
        String uri="/test_file.txt";
        assert (storageServer.createFile(uri));
        assert(storageServer.fileExists(uri));
        assert(storageServer.deleteFile(uri));
    }

    @Test
    public void testCreate_exists_deleteDir() throws Exception {
        if(storageServer==null)
            storageServer=new StorageServer();
        String uri="/test_folder";
        assert(storageServer.createDir(uri));
        assert(storageServer.dirExists(uri));
        assert(storageServer.deleteDir(uri));
    }

    @Test
    public void testListDir()throws Exception{
        if(storageServer==null)
            storageServer=new StorageServer();

        String dirUri="/test_folder";
        String subDirUri="/test_folder/sub_folder";
        String fileUri="/test_folder/test_file.txt";

        assert(storageServer.createDir(dirUri));
        assert(storageServer.dirExists(dirUri));

        assert(storageServer.createFile(fileUri));
        assert(storageServer.fileExists(fileUri));
        assert(storageServer.listFiles(dirUri).length==1);
        assert(storageServer.listFiles(dirUri)[0].endsWith(fileUri));

        assert(storageServer.createDir(subDirUri));
        assert(storageServer.dirExists(subDirUri));
        assert(storageServer.listSubDirs(dirUri).length==1);
        assert(storageServer.listSubDirs(dirUri)[0].endsWith(subDirUri));

        assert(storageServer.deleteFile(fileUri));
        assert(storageServer.deleteDir(subDirUri));
        assert(storageServer.deleteDir(dirUri));
    }

    @Test
    public void testGetLastModifiedTime() throws Exception{
        if(storageServer==null)
            storageServer=new StorageServer();
        String uri="\\test_folder";
        assert(storageServer.createDir(uri));
        assert(storageServer.dirExists(uri));
        assert(storageServer.getLastModifiedTime(uri)!=null);
        System.out.println(storageServer.getLastModifiedTime(uri));
        assert(storageServer.deleteDir(uri));
    }

    @Test
    public void testUpload() throws Exception{
        if(storageServer==null)
            storageServer=new StorageServer();
        String localFile="test_upload.txt";
        Files.deleteIfExists(Paths.get(localFile));
        Files.createFile(Paths.get(localFile));
        String content="Hello World!";
        Files.write(Paths.get(localFile),content.getBytes("utf-8"));

        // upload part
        String uri="\\test_file.txt";
        assert(storageServer.uploadWithOverride(uri,new File(localFile)));
        assert(storageServer.deleteFile(uri));
        Files.delete(Paths.get(localFile));
    }

    @Test
    public void testDownload() throws Exception{
        if(storageServer==null)
            storageServer=new StorageServer();
        String uri="\\test_download.txt";
        assert(storageServer.createFile(uri));
        File f=storageServer.download(uri);
        assert(f!=null);
        assert(storageServer.deleteFile(uri));

    }
}
