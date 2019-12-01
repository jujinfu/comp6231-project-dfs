package accessServer;

import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;
import accessServer.domain.repositories.FileDirInfoRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class FileDirInfoRepositoryTest {

    private static String tempParentName = "sub1_test";
    private static String tempFileName = "sub2_test";
    private static String SLASH = "/";

    @BeforeEach
    public void beforeEach(){
        System.out.println("----------------beforeEach--------------------");
        tempFileName = "sub1";
        tempParentName = "sub2";
    }

    @AfterEach
    public void afterEach(){
        System.out.println("----------------afterEach--------------------");
        deleteByName(tempFileName);
        deleteByName(tempParentName);
    }

    @Test
    public void testExists1(){

        try {
            //file to be tested
            String uri = "/underroot.txt";
            //make sure new file does not exist
            assertTrue(FileDirInfoRepository.getFile(uri) == null);
            //create file
            FileDirInfo file=FileDirInfoRepository.createNewFile(uri);
            //make sure create did return a file
            assertNotEquals(null,file);
            //make sure exists function returns right value
            assertTrue(FileDirInfoRepository.exists(file));
            //cleanup
            assertTrue(FileDirInfoRepository.deleteFileById(file.getId()));

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            afterEach();
        }
    }

    @Test
    public void testExists2(){
        try {
            //file to be tested
            String uri = "/subdir1/subdir2/underroot.txt";
            //make sure new file does not exist
            assertTrue(FileDirInfoRepository.getFile(uri) == null);

            /*//create file
            FileDirInfo file=FileDirInfoRepository.createNewFile(uri);
            //make sure create did return a file
            assertNotEquals(null,file);
            //make sure exists function returns right value
            assertTrue(FileDirInfoRepository.exists(file));
            //cleanup
            assertTrue(FileDirInfoRepository.deleteFileById(file.getId()));
            */


        }catch(Exception e){
            e.printStackTrace();

        }finally {
            afterEach();
        }
    }
    @Test
    public void testExists3(){
        String uri="/somthing.txt";
        FileDirInfo actualFile = FileDirInfoRepository.getFile(uri);
        boolean actual = FileDirInfoRepository.exists(actualFile);
        afterEach();
        assertFalse(actual);
    }

    @Test
    public void testExists4(){
        String uri="/";
        FileDirInfo actualFile = FileDirInfoRepository.getFile(uri);
        boolean actual = FileDirInfoRepository.exists(actualFile);
        afterEach();
        assertTrue(actual);
    }

    @Test
    public void testExists5(){
        String uri="/sub1/some.txt";
        FileDirInfo actualFile = FileDirInfoRepository.getFile(uri);
        boolean actual = FileDirInfoRepository.exists(actualFile);
        afterEach();
        assertFalse(actual);
    }

    @Test
    public void testGetFileById() {
        FileDirInfo file = FileDirInfoRepository.getFileById(1);
        afterEach();
        assertEquals(file.getId(), 1);
    }

    @Test
    public void testGetChildernById() {
        Integer parentId = 12345;
        insertFileByName(tempParentName, true, 1, parentId);
        FileDirInfo dir = FileDirInfo.builder().id(parentId).build();
        Integer childId = 13345;
        insertFileByName(tempFileName, false, parentId, childId);
        Integer childId2 = 13346;
        insertFileByName(tempFileName, false, parentId, childId2);

        List<FileDirInfo> files = FileDirInfoRepository.getChildren(dir);

        afterEach();
        assertEquals(files.size(), 2);
    }

    //pay attentation to data, I didn't prepare data

    /*  @Test
    public void testGetFileByNameAndParnetName() {
        List<FileDirInfo> files = FileDirInfoRepository.getFileByNameParentName("text1.txt", "sub2");
        files.forEach(f -> System.out.println(f.getParent().getName()));
        assertTrue(files.size() > 1);

    }
    */

    /*
    root has a name of ""
    root has itself as parent because db constraint requires one
    root has id of 1
     */
    @Test
    public void testGetRoot() {
        FileDirInfo root = FileDirInfoRepository.getRoot();
        afterEach();
        assertNotNull(root);
        assertEquals(1, root.getId());
        assertEquals("", root.getName());
        assertNull(root.getParent());
    }

    @Test
    public void testGetRootDirs() {
        insertFileByName(tempParentName);
        insertFileByName(tempFileName);

        List<FileDirInfo> root = FileDirInfoRepository.getRootDirAndFiles();

        afterEach();
        assertNotNull(root);
        assertTrue(root.size() >= 1);
        root.forEach(f -> assertEquals(f.getParent().getId(), 1));
    }

    @Test
    public void testCreateDir() {
        Integer pid = 12345;
        insertFileByName(tempParentName, true, 1, pid);
        String uri = SLASH + tempParentName + SLASH + tempFileName;

        FileDirInfoRepository.createNewDir(uri);

        List<FileDirInfo> actualDir = getByName(tempFileName);

        afterEach();
        assertNotNull(actualDir);
        assertTrue(actualDir.size() == 1);
        FileDirInfo file = actualDir.get(0);
        assertEquals(tempFileName, file.getName());
        assertTrue(file.isDir());
        assertEquals(pid, file.getParent().getId());
    }

    @Test
    public void testDeleteFile() {
        FileDirInfo p = FileDirInfoRepository.getRoot();
        Integer id = 12345;
        String fileName = tempFileName;
        //String qs = "insert into file_dir_info (id, name,is_dir,status_by_user,parent) values (" + id + ",'" + fileName + "',true,'test',1);";

        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Query q = em.createNativeQuery("insert into file_dir_info (id, name,is_dir,status_by_user,parent) " +
                "VALUES (:id, :name, :is_dir, :status_by_user, :parent)")
                .setParameter("id", id)
                .setParameter("name", fileName)
                .setParameter("is_dir", true)
                .setParameter("status_by_user", "test")
                .setParameter("parent", 1);
        q.executeUpdate();
        em.getTransaction().commit();

        boolean isDeleted = FileDirInfoRepository.deleteFileById(id);
        assertTrue(isDeleted);
        em.getTransaction().begin();
        Query q2 = em.createQuery("select f from file_dir_info f where f.id=:id").setParameter("id", id);
        List<FileDirInfo> actualDir = q2.getResultList();

        afterEach();
        assertEquals(0, actualDir.size());

    }

    @Test
    public void testCreateByNativeQuery() {
        String name = tempFileName;
        Integer pId = 1;
        FileDirInfo file = new FileDirInfo();
        file.setDir(true);
        file.setName(name);
        file.setStatusByUser("test");


        FileDirInfoRepository.createNewDir(file, pId);

        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        Query q2 = em.createQuery("select f from file_dir_info f where f.name=:name").setParameter("name", name);
        List<FileDirInfo> actualResult = q2.getResultList();

        afterEach();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() == 1);
        FileDirInfo actualFile = actualResult.get(0);

        assertEquals(name, actualFile.getName());
        assertEquals(pId, actualFile.getParent().getId());
        assertTrue(actualFile.isDir());

        Query q = em.createNativeQuery("delete from file_dir_info f where f.name ='" + name + "'");
        em.getTransaction().begin();
        q.executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testUpdate() {
        FileDirInfo p = FileDirInfoRepository.getRoot();
        Integer id = 12345;
        String fileName = tempFileName;
        Date oldLastModifiedDate = getDatBefore(7);
        //String qs = "insert into file_dir_info (id, name,is_dir,status_by_user,parent) values (" + id + ",'" + fileName + "',true,'test',1);";

        insertFileByNameAndId(fileName, id);

        String newStatusByUser = "test1234";
        FileDirInfo toBeUpdated = FileDirInfo.builder().id(id).statusByUser(newStatusByUser).build();

        FileDirInfoRepository.update(toBeUpdated);

        List<FileDirInfo> actualResult = getByName(fileName);

        afterEach();
        assertEquals(1, actualResult.size());
        FileDirInfo actualUpdatedFile = actualResult.get(0);
        assertTrue(oldLastModifiedDate.getTime() < actualUpdatedFile.getLastModifiedDate().getTime());
        assertEquals(newStatusByUser, actualUpdatedFile.getStatusByUser());
    }

    @Test
    public void testCreateNewFileByUri(){
        String name = tempFileName;
        String uri = "/" + name;

        FileDirInfo file = FileDirInfoRepository.createNewFile(uri);

        List<FileDirInfo> actualResult = getByName(name);
        deleteByName(name);

        assertEquals( 1, actualResult.size());
        FileDirInfo actualFile = actualResult.get(0);
        assertEquals(name, actualFile.getName());
    }

    @Test
    public void testCreateNewFileByNestedUri(){
        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Query q = em.createNativeQuery("insert into file_dir_info ( name,is_dir,status_by_user,parent) " +
                "VALUES (:name, :is_dir, :status_by_user, :parent)")
                .setParameter("name", tempParentName)
                .setParameter("is_dir", true)
                .setParameter("status_by_user", "test")
                .setParameter("parent", 1);
        q.executeUpdate();
        em.getTransaction().commit();
        String uri = "/" + tempParentName + "/" + tempFileName;

        FileDirInfo file = FileDirInfoRepository.createNewFile(uri);

        List<FileDirInfo> actualResult = getByName(tempFileName);

        afterEach();
        assertEquals( 1, actualResult.size());
        FileDirInfo actualFile = actualResult.get(0);
        assertEquals(tempFileName, actualFile.getName());
    }

    private List<FileDirInfo> getByName(String name){
        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        Query q2 = em.createQuery("select f from file_dir_info f where f.name=:name")
                .setParameter("name", name);
        List<FileDirInfo> actualResult = q2.getResultList();
        em.close();
        return actualResult;
    }

    private void deleteByName(String name){
        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        Query q = em.createNativeQuery("delete from file_dir_info f where f.name ='" + name + "'");
        em.getTransaction().begin();
        q.executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    private void insertFileByName(String name){
        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Query q = em.createNativeQuery("insert into file_dir_info (name,is_dir,status_by_user,parent) " +
                "VALUES (:name, :is_dir, :status_by_user, :parent)")
                .setParameter("name", name)
                .setParameter("is_dir", false)
                .setParameter("status_by_user", "test")
                .setParameter("parent", 1);
        q.executeUpdate();
        em.getTransaction().commit();
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

    private void insertFileByNameAndId(String name, Integer id){
        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Query q = em.createNativeQuery("insert into file_dir_info (id, name,is_dir,status_by_user,parent) " +
                "VALUES (:id, :name, :is_dir, :status_by_user, :parent)")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("is_dir", false)
                .setParameter("status_by_user", "test")
                .setParameter("parent", 1);
        q.executeUpdate();
        em.getTransaction().commit();
    }

    private Date getDatBefore(int days) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (-1) * days);
        return cal.getTime();
    }

}
