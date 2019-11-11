package accessServer;


import org.junit.jupiter.api.Test;

public class AccessServerTest {

    private AccessServer accessServer;

    public AccessServerTest() throws Exception{
        accessServer=new AccessServer();
    }

    @Test
    void fileExistsTest() throws Exception{
        if(accessServer==null)
            accessServer=new AccessServer();
        String uri="/as_exist_file.txt";
        assert(accessServer.fileExists(uri));
    }
    @Test
    void createFileTest() throws Exception{
        if(accessServer==null)
            accessServer=new AccessServer();
        String uri="/as_create_file.txt";
        assert(accessServer.createFile(uri));
        assert(accessServer.fileExists(uri));
        assert(accessServer.deleteFile(uri));
    }
}