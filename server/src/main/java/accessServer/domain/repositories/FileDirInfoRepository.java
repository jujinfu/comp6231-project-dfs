package accessServer.domain.repositories;

import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class FileDirInfoRepository {

    private static EntityManagerFactory factory = EntityManagerHelper.getEntityManagerFactory();

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

    public static List<FileDirInfo> getFileByNameParentName(String name, String parentName) {
        EntityManager em = factory.createEntityManager();
        List<FileDirInfo> files = em
                .createNamedQuery("FileDirInfo.fileExists", FileDirInfo.class)
                .setParameter("name", name)
                .setParameter("parent", parentName)
                .getResultList();
        em.close();
        return files;
    }

    // to check if file exists in db
    //"\test_dir\test.txt"
    public static boolean isExists(String uri){
        if(!uri.startsWith("\\"))
            return false;
        String[] list=uri.split("\\\\");
        String rootName=getRoot().getName();
        for(int i=1;i<list.length;i++){
            FileDirInfo fileDirInfo= getFileInDir(list[i],rootName);
            if(fileDirInfo==null)
                return false;
            rootName=fileDirInfo.getName();
        }
        return true;
    }

    private static FileDirInfo getFileInDir(String fileName,String parentName){
        EntityManager em = factory.createEntityManager();
        FileDirInfo files = em
                .createNamedQuery("FileDirInfo.fileExists", FileDirInfo.class)
                .setParameter("name", fileName)
                .setParameter("parent",parentName)
                .getSingleResult();
        em.close();
        return files;
    }



    public static FileDirInfo createNewFile(FileDirInfo fileDirInfo){
        fileDirInfo.setDir(false);
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        em.persist(fileDirInfo);
        em.getTransaction().commit();
        em.close();
        return fileDirInfo;
    }

    public static FileDirInfo createNewDir(FileDirInfo fileDirInfo) {
        fileDirInfo.setDir(true);
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
