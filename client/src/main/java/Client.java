import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    ServerInterface rmiServer;
    Registry registry;

    public void start() throws Exception{
        // find the (local) object registry
        registry = LocateRegistry.getRegistry(9999);
        // find the server object
        rmiServer = (ServerInterface) (registry.lookup("server"));
        rmiServer.download("Hello World");
    }

    public File openFile(String uri) throws Exception{
        return null;
    }
    public boolean saveFile(String uri,byte[] content) throws Exception{
        return false;
    }
    public boolean closeFile(String uri) throws Exception{
        return false;
    }
    public boolean deleteFile(String uri) throws Exception{
        return false;
    }
    public boolean uploadFile(String uri, File file) throws Exception{
        return false;
    }
    public File downloadFile(String uri) throws Exception{
        return null;
    }
    public String getFileMeta(String uri) throws Exception{
        return null;
    }
    public String listDirectory(String uri) throws Exception{
        return null;
    }
}