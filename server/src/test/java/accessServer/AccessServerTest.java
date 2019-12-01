package accessServer;


import accessServer.domain.EntityManagerHelper;
import accessServer.domain.repositories.FileDirInfoRepository;
import lombok.SneakyThrows;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.Inflater;

import static org.junit.jupiter.api.Assertions.*;

public class AccessServerTest {

    private static AccessServer accessServer;
    private static String tempParentName;
    private static String tempFileName;
    private static String tempFileName2;

//    public AccessServerTest() throws Exception{
//        accessServer=new AccessServer();
//    }

    @BeforeAll
    public static void beforeClass() throws Exception{
        System.out.println("----------------beforeAll--------------------");
        accessServer=new AccessServer();
    }

    @BeforeEach
    public void beforeEach(){
        System.out.println("----------------beforeEach--------------------");
        tempFileName = "sub1";
        tempParentName = "sub2";
        tempFileName2 = "sub3";
    }

    @AfterEach
    public void afterEach(){
        System.out.println("----------------afterEach--------------------");
        deleteByName(tempFileName2);
        deleteByName(tempFileName);
        deleteByName(tempParentName);
    }

    private void deleteByName(String name){
        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        Query q = em.createNativeQuery("delete from file_dir_info f where f.name ='" + name + "'");
        em.getTransaction().begin();
        q.executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    private void insertFileByName(String name, boolean isDir, Integer parent, Integer id){
        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Query q = em.createNativeQuery("insert into file_dir_info (id, name,is_dir,status_by_user,parent) " +
                "VALUES (:id, :name, :is_dir, :status_by_user, :parent)")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("is_dir", isDir)
                .setParameter("status_by_user", "test")
                .setParameter("parent", parent);
        q.executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    @SneakyThrows
    public void testFileExists() {
            if (accessServer == null) {
                Integer pId = 12345;
                Integer fId = 12346;
                Integer fId2 = 12347;
                insertFileByName(tempParentName, true, 1, pId);
                insertFileByName(tempFileName, true, pId, fId);
                insertFileByName(tempFileName2, false, fId, fId2);

                accessServer = new AccessServer();
                String uri = "/" + tempParentName + "/" + tempFileName + "/" + tempFileName2;
                assert (accessServer.fileExists(uri));
                System.out.println("testing random file");
                uri = "/random_file.txt";
                assert (!accessServer.fileExists(uri));

            }
    }

    @Test
    @SneakyThrows
    public void testCreateFile() {
        String uri = "/" + tempFileName;
        assert (accessServer.createFile(uri));
        assert (accessServer.fileExists(uri));
        assert (accessServer.deleteFile(uri));
    }

    @Test
    @SneakyThrows
    public void testCreateDir() {
        String uri = "/" + tempFileName;
        assert (accessServer.createDir(uri));
        assert (accessServer.dirExists(uri));
        assert (accessServer.deleteDir(uri));
    }

    @Test
    @SneakyThrows
    public void testCreateDir_failFileNotEmpty() {
        Integer pid = 12345;
        Integer fId = 12346;
        String uri = "/" + tempParentName ;
        insertFileByName(tempParentName, true, 1, pid);
        insertFileByName(tempFileName, false, pid, fId);

        assertThrows(RemoteException.class, () ->
                        accessServer.deleteDir(uri),
                "Directory is not empty");
    }

    @Test
    @SneakyThrows
    public void testCreateDir_failDirIsFile() {
        Integer pid = 12345;
        Integer fId = 12346;
        String uri = "/" + tempParentName + "/" + tempFileName ;
        insertFileByName(tempParentName, true, 1, pid);
        insertFileByName(tempFileName, false, pid, fId);

        assertThrows(RemoteException.class, () ->
                        accessServer.deleteDir(uri),
                "Directory not exists in db");
    }

    @Test
    @SneakyThrows
    public void testCreateDir_failDirNotExist() {
        String dir = UUID.randomUUID().toString();
        String uri = "/" + dir  ;

        assertThrows(RemoteException.class, () ->
                        accessServer.deleteDir(uri),
                "Directory not exists in db");
    }

    @Test
    public void testCreateNewFileByUri_failFileAlreadyExist() {
        Integer pid = 12345;
        Integer fId = 12346;
        String uri = "/" + tempParentName + "/" + tempFileName;
        insertFileByName(tempParentName, true, 1, pid);
        insertFileByName(tempFileName, false, pid, fId);

        assertThrows(RemoteException.class, () ->
                        accessServer.createFile(uri),
                "File exists in db");
    }

    @Test
    public void testCreateNewFileByUri_failParentNotExist() {
        String dummyParent = UUID.randomUUID().toString();
        String uri = "/" + "dummyParent" + "/" + tempFileName;

        assertThrows(RemoteException.class, () ->
                        accessServer.createFile(uri),
                "parent not exist in db, uri: " + uri);
    }

    @Test
    @SneakyThrows
    public void testListFiles() {
        Integer pid = 12345;
        Integer fId = 12346;
        Integer fId2 = 12347;
        String uri = "/" + tempParentName;
        insertFileByName(tempParentName, true, 1, pid);
        insertFileByName(tempFileName, false, pid, fId);
        insertFileByName(tempFileName2, true, pid, fId2);

        String[] files = accessServer.listFiles(uri);

        assertNotNull(files);
        assertEquals(1, files.length);
        assertEquals(tempFileName, files[0]);
    }

    @Test
    @SneakyThrows
    public void testListFiles_emptyNoFile() {
        Integer pid = 12345;
        Integer fId2 = 12347;
        String uri = "/" + tempParentName;
        insertFileByName(tempParentName, true, 1, pid);
        insertFileByName(tempFileName2, true, pid, fId2);

        String[] files = accessServer.listFiles(uri);

        assertNotNull(files);
        assertEquals(0, files.length);
    }


    @Test
    @SneakyThrows
    public void testListSubDir() {
        Integer pid = 12345;
        Integer fId = 12346;
        Integer fId2 = 12347;
        String uri = "/" + tempParentName;
        insertFileByName(tempParentName, true, 1, pid);
        insertFileByName(tempFileName, false, pid, fId);
        insertFileByName(tempFileName2, true, pid, fId2);

        String[] files = accessServer.listSubDirs(uri);

        assertNotNull(files);
        assertEquals(1, files.length);
        assertEquals(tempFileName2, files[0]);
    }

    @Test
    @SneakyThrows
    public void testListSubDir_noSubDir() {
        Integer pid = 12345;
        Integer fId = 12346;
        Integer fId2 = 12347;
        String uri = "/" + tempParentName;
        insertFileByName(tempParentName, true, 1, pid);
        insertFileByName(tempFileName, false, pid, fId);
        insertFileByName(tempFileName2, false, pid, fId2);

        String[] files = accessServer.listSubDirs(uri);

        assertNotNull(files);
        assertEquals(0, files.length);
    }
}
