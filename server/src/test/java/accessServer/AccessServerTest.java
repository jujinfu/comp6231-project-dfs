package accessServer;


import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

public class AccessServerTest {

    private AccessServer accessServer;

    public AccessServerTest() throws Exception{
        accessServer=new AccessServer();
    }

    @Test
    public void testFileExists() {
        try {if(accessServer==null) {

                accessServer=new AccessServer();
                String uri="\\sub1\\sub2\\some.txt";
                assert(accessServer.fileExists(uri));
                System.out.println("testing random file");
                uri="\\random_file.txt";
                assert(!accessServer.fileExists(uri));

        }            } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testCreateFile(){
        try {
            if(accessServer==null) {
                accessServer=new AccessServer();
            }
            String uri="\\as_create_file.txt";
            assert(accessServer.createFile(uri));
            assert(accessServer.fileExists(uri));
            assert(accessServer.deleteFile(uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
