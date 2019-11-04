import accessServer.AccessServerInterface;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    AccessServerInterface accessServer;
    Registry registry;

    public void start() throws Exception {
        // find the (local) object registry
        registry = LocateRegistry.getRegistry(9999);
        // find the server object
        accessServer = (AccessServerInterface) (registry.lookup("server"));
    }

    public File openFile(String uri) throws Exception {
        if (!Files.exists(Paths.get(uri))) {
            throw new FileNotFoundException("Unable to open file in path " + uri);
        }
        return new File(uri);
    }

    public boolean saveFile(String uri, byte[] content) throws Exception {
       // byte[] content = FileUtils.readFileToByteArray(openFile(uri));
        accessServer.saveFile(uri, content);
        return false;
    }

    public boolean closeFile(String uri) throws Exception {
        return false;
    }

    public boolean deleteFile(String uri) throws Exception {
        return false;
    }

    public boolean uploadFile(String uri, File file) throws Exception {
        byte[] content = FileUtils.readFileToByteArray(file);
        //rmiServer.createFile(uri);
        return false;
    }

    public File downloadFile(String uri) throws Exception {
        //rmiServer.download(uri);
        return null;
    }

    public String getFileMeta(String uri) throws Exception {
        return null;
    }

    public String[] listSubDirs(String uri) throws Exception {
        return null;
    }

    public String[] listFiles(String uri) throws Exception {
        return null;
    }

}
