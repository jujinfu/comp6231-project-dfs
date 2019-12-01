package accessServer;

import java.io.IOException;
import java.net.UnknownHostException;

public interface StorageManagementInterface {

    public abstract String syncCommand(String command) throws IOException;

    public abstract void updateFileMeta(String absoluteUri,String property);


}
