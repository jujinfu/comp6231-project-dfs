package accessServer;


import accessServer.domain.EntityManagerHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.rmi.RemoteException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class AccessServerTest {

    private static AccessServer accessServer;
    private static String tempParentName = "sub1_test";
    private static String tempFileName = "sub2_test";
    private static String tempFileName2 = "sub3_test";

//    public AccessServerTest() throws Exception{
//        System.out.println("----------------constructor--------------------");
//        accessServer=new AccessServer();
//    }

    @BeforeAll
    public static void beforeClass() throws Exception{
        log.info("----------------beforeAll--------------------");
        accessServer=new AccessServer();
    }


//    @BeforeEach
//    public void beforeEach(){
//        System.out.println("----------------beforeEach--------------------");
//        tempFileName = "sub1_test";
//        tempParentName = "sub2_test";
//        tempFileName2 = "sub3_test";
//    }

   @AfterEach
    public void afterEach(){
        log.info("----------------after Each--------------------");
        deleteByName(tempFileName2);
        deleteByName(tempFileName);
        deleteByName(tempParentName);;
    }

    private static void deleteByName(String name){
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
        em.close();
    }

    @Test
    @SneakyThrows
    public void testFileExists() {
        log.info("----------------testFileExists--------------------");
        //afterEach();
            if (accessServer == null) {
                Integer pId = 12345;
                Integer fId = 12346;
                Integer fId2 = 12347;
                insertFileByName(tempParentName, true, 1, pId);
                insertFileByName(tempFileName, true, pId, fId);
                insertFileByName(tempFileName2, false, fId, fId2);

                accessServer = new AccessServer();
                String uri = "/" + tempParentName + "/" + tempFileName + "/" + tempFileName2;
                boolean result = accessServer.fileExists(uri);

                assert (result);
                System.out.println("testing random file");
                uri = "/random_file.txt";
                assert (!accessServer.fileExists(uri));

            }
    }

    @Test
    @SneakyThrows
    public void testCreateFile() {
        log.info("----------------testCreateFile--------------------");
        //afterEach();
        String uri = "/" + tempFileName;
        assert (accessServer.createFile(uri));
        assert (accessServer.fileExists(uri));
        assert (accessServer.deleteFile(uri));
    }

    @Test
    @SneakyThrows
    public void testCreateDir() {
        log.info("----------------testCreateDir--------------------");
        //afterEach();
        String uri = "/" + tempFileName;
        assert (accessServer.createDir(uri));
        assert (accessServer.dirExists(uri));
        assert (accessServer.deleteDir(uri));
    }

    @Test
    @SneakyThrows
    public void testCreateDir_failFileNotEmpty() {
        log.info("----------------testCreateDir_failFileNotEmpty--------------------");
        //afterEach();
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
        log.info("---------------- testCreateDir_failDirIsFile--------------------");
        //afterEach()
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
        log.info("----------------testCreateDir_failDirNotExist--------------------");
        //afterEach();
        String dir = UUID.randomUUID().toString();
        String uri = "/" + dir  ;

        assertThrows(RemoteException.class, () ->
                        accessServer.deleteDir(uri),
                "Directory not exists in db");
    }

    @Test
    public void testCreateNewFileByUri_failFileAlreadyExist() {
        log.info("----------------testCreateNewFileByUri_failFileAlreadyExist--------------------");
        //afterEach();
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
        log.info("----------------testCreateNewFileByUri_failParentNotExist--------------------");
       // afterEach();
        String dummyParent = UUID.randomUUID().toString();
        String uri = "/" + "dummyParent" + "/" + tempFileName;

        assertThrows(RemoteException.class, () ->
                        accessServer.createFile(uri),
                "parent not exist in db, uri: " + uri);
    }

    @Test
    @SneakyThrows
    public void testListFiles() {
        log.info("----------------testListFiles--------------------");
        //afterEach();
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
        log.info("----------------testListFiles_emptyNoFile--------------------");
        //afterEach();
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
        log.info("----------------testListSubDir--------------------");
       // afterEach();
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
        log.info("----------------testListSubDir_noSubDir--------------------");
       // afterEach();
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
