package accessServer.domain.repositories;

import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public class FileDirInfoRepository {

    private static EntityManagerFactory factory = EntityManagerHelper.getEntityManagerFactory();
    private static final String ROOT = "";
    private static final String SLASH = "/";


    public static FileDirInfo getFileById(Integer id) {
        EntityManager em = factory.createEntityManager();
        FileDirInfo file = em.createNamedQuery("FileDirInfo.findById", FileDirInfo.class)
                .setParameter("id", id).getResultList().stream().findFirst().orElse(null);
        em.close();
        return file;
    }

    public static FileDirInfo getRoot() {
        EntityManager em = factory.createEntityManager();
        FileDirInfo file = em.createNamedQuery("FileDirInfo.getRoot", FileDirInfo.class)
                .getResultList().stream().findFirst().orElse(null);
        em.close();
        return file;
    }

    public static List<FileDirInfo> getRootDirAndFiles() {
        EntityManager em = factory.createEntityManager();
        List<FileDirInfo> files = em.createNamedQuery("FileDirInfo.getRootDirs", FileDirInfo.class)
                .getResultList();
        em.close();
        return files;
    }

    public static List<FileDirInfo> getChildren(FileDirInfo dir) {
        EntityManager em = factory.createEntityManager();
        List<FileDirInfo> files = em.createNamedQuery("FileDirInfo.getChildren", FileDirInfo.class)
                .setParameter("parent", dir.getId()).getResultList();
        em.close();
        return files;
    }

//    public static FileDirInfo getFile(String uri){
//        if(!uri.startsWith("\\"))
//            return null;
//        String[] list=uri.split("\\\\");
//        String rootName=getRoot().getName();
//        FileDirInfo fileDirInfo=null;
//        for(int i=1;i<list.length;i++){
//            fileDirInfo= getFileInDir(list[i],rootName);
//            if(fileDirInfo==null)
//                return null;
//            rootName=fileDirInfo.getName();
//        }
//        return fileDirInfo;
//    }

//        public static FileDirInfo getFile(String uri){
//        if(!uri.startsWith("\\"))
//            return null;
//        String[] list=uri.split("\\\\");
//        String rootName=getRoot().getName();
//        FileDirInfo fileDirInfo=null;
//        for(int i=1;i<list.length;i++){
//            fileDirInfo= getFileInDir(list[i],rootName);
//            if(fileDirInfo==null)
//                return null;
//            rootName=fileDirInfo.getName();
//        }
//        return fileDirInfo;
//    }

//    public static boolean exists1(String uri){
//        return getFile(uri)==null;
//    }

    public static boolean exists(FileDirInfo file){
        return file != null;
    }

    public static FileDirInfo getFile(String uri){
        if(!uri.startsWith(SLASH)){
            throw new IllegalArgumentException("invalid path " + uri);
        }
        String[] list=uri.split(SLASH);
        if(list.length == 0){ //is root
            return getRoot();
        }
        if(list.length > 1){
            list[0] = ROOT;
        }
        String fileName = list[list.length-1];
        String parentName = list.length == 2 ? ROOT : list[list.length-2];
        List<FileDirInfo> files = getFileInDir(fileName, parentName);
        if(files.isEmpty()){
            return null;
        }
        for(FileDirInfo file : files){
            if(isSingleFileExist(list, file)){
                return file;
            }
        }
        return null;
    }

    private static boolean isSingleFileExist(String[] list, FileDirInfo file){
        FileDirInfo parent = file.getParent();
        if(list.length == 2 && ROOT.equals(parent.getName())){ // like /some.txt
            return true;
        }
        for(int i = list.length-2; i >= 0; i--){
            if(!list[i].equals(parent.getName())){
                return false;
            }
            parent = parent.getParent();
        }
        return true;
    }

    public static FileDirInfo getFileByName(String name){
        EntityManager em = factory.createEntityManager();
        FileDirInfo file = em
                .createNamedQuery("FileDirInfo.fileExists", FileDirInfo.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
        em.close();
        return file;
    }

    private static List<FileDirInfo> getFileInDir(String fileName, String parentName) {
        EntityManager em = factory.createEntityManager();
        List<FileDirInfo> files = em
                .createNamedQuery("FileDirInfo.fileExists", FileDirInfo.class)
                .setParameter("name", fileName)
                .setParameter("parent", parentName)
                .getResultList();
        em.close();
        return files;
    }

    public static FileDirInfo createNewFile(String uri) {
        if(!uri.startsWith(SLASH)){
            return null;
        }
        FileDirInfo parent = getParentByUri(uri);
        FileDirInfo fileDirInfo=FileDirInfo
                .builder()
                .isDir(false)
                .name(getFileNameByUri(uri))
                .parent(parent)
                .status("Ready")
                .build();
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(fileDirInfo);
        em.getTransaction().commit();
        em.close();
        return fileDirInfo;
    }

    public static FileDirInfo createNewDir(String uri) {
        if(!uri.startsWith(SLASH)){
            return null;
        }
        FileDirInfo parent = getParentByUri(uri);
        FileDirInfo fileDirInfo=FileDirInfo
                .builder()
                .isDir(true)
                .name(getFileNameByUri(uri))
                .parent(parent)
                .status("Ready")
                .build();
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(fileDirInfo);
        em.getTransaction().commit();
        em.close();
        return fileDirInfo;
    }

    private static String getParentPathByUri(String uri) {
        String[] list = uri.split(SLASH);
        if (list.length == 0) { //is root
            return null;
        }
        return list.length == 2 ?
                ROOT
                : uri.substring(0, uri.lastIndexOf(SLASH));
    }

    private static String getFileNameByUri(String uri) {
        if (uri.length() == 1) {
            return ROOT;
        }
        return uri.substring(uri.lastIndexOf(SLASH) + 1);
    }

    public static FileDirInfo getParentByUri(String uri) {
        String parentUri = getParentPathByUri(uri);
        if (parentUri == null) {
            return null;
        }
        return ROOT.equals(parentUri) ?
                getRoot() :
                getFile(parentUri);
    }

    public static FileDirInfo createNewDir(FileDirInfo fileDirInfo) {
        fileDirInfo.setDir(true);
        if(fileDirInfo.getParent()==null)
            throw new IllegalArgumentException("dir no parent");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(fileDirInfo);
        em.getTransaction().commit();
        em.close();
        return fileDirInfo;
    }

    public static FileDirInfo createNewDir(FileDirInfo fileDirInfo, Integer parentId) {
        String q = "insert into file_dir_info (name,is_dir,status_by_user,parent) " +
                "VALUES (:name, :is_dir, :status_by_user, :parent)";
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery(q)
                .setParameter("name", fileDirInfo.getName())
                .setParameter("is_dir", fileDirInfo.isDir())
                .setParameter("status_by_user", fileDirInfo.getStatusByUser())
                .setParameter("parent", parentId)
                .executeUpdate();
        em.getTransaction().commit();
        em.close();
        return fileDirInfo;
    }

    public static FileDirInfo update(FileDirInfo fileDirInfo) {
        EntityManager em = factory.createEntityManager();
        FileDirInfo file = em.find(FileDirInfo.class, fileDirInfo.getId());
        file.setLastModifiedDate(new Date());
        file.setStatusByUser(fileDirInfo.getStatusByUser());
        em.getTransaction().begin();
        em.persist(file);
        em.getTransaction().commit();
        em.close();
        return file;
    }


    public static boolean deleteFileById(Integer id) {
        EntityManager em = factory.createEntityManager();
        FileDirInfo file = em.find(FileDirInfo.class, id);
        if (file == null) {
            em.close();
            return false;
        }
        em.getTransaction().begin();
        em.remove(file);
        em.getTransaction().commit();
        em.close();
        return true;
    }

}
