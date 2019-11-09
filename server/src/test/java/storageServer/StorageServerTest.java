package storageServer;

import org.junit.jupiter.api.Test;

import java.io.File;

class StorageServerTest {

    private StorageServer storageServer;

    public StorageServerTest() throws Exception{
        storageServer=new StorageServer();
    }


    @Test
    void create_exists_deleteFileTest() throws Exception{
        if(storageServer==null)
            storageServer=new StorageServer();
        String uri="/delete_file.txt";

        assert (storageServer.createFile(uri));
        assert(storageServer.fileExists(uri));
        assert(storageServer.deleteFile(uri));
    }

    @Test
    void create_exists_deleteDirTest() throws Exception
    {
        
    }

}
