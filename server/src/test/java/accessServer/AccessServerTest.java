package accessServer;


import org.junit.jupiter.api.Test;

public class AccessServerTest {

    private AccessServer accessServer;

    public AccessServerTest() throws Exception{
        accessServer=new AccessServer();
    }

    @Test
    public void fileExistsTest() throws Exception{
        if(accessServer==null)
            accessServer=new AccessServer();
        String uri="\\sub1\\sub2\\some.txt";
        assert(accessServer.fileExists(uri));
        System.out.println("testing random file");
        uri="\\random_file.txt";
        assert(!accessServer.fileExists(uri));

    }
    @Test
    public void createFileTest() throws Exception{
        if(accessServer==null)
            accessServer=new AccessServer();
        String uri="\\as_create_file.txt";
        assert(accessServer.createFile(uri));
        assert(accessServer.fileExists(uri));
        assert(accessServer.deleteFile(uri));
    }
}
