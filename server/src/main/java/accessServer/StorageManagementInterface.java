package accessServer;

public interface StorageManagementInterface {

    public abstract void syncFile(String fromAbsoluteUri,String toAbsoluteUri);

    public abstract void updateFileMeta(String absoluteUri,String property);


}
