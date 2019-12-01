package accessServer;

import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;
import accessServer.domain.repositories.FileDirInfoRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class FileDirInfoRepositoryTest {

    private static String tempParentName = "sub1_repo_test";
    private static String tempFileName = "sub2_repo_test";
    private static String tempFileName2 = "sub3_repo_test";
    private static String SLASH = "/";

    @AfterEach
    public void afterEach(){
        System.out.println("----------------afterEach--------------------");
        deleteByName(tempFileName2);
        deleteByName(tempFileName);
        deleteByName(tempParentName);
    }

    @AfterAll
    public static void afterAll(){
        System.out.println("----------------afterAll--------------------");
        deleteByName(tempFileName2);
        deleteByName(tempFileName);
        deleteByName(tempParentName);
    }

    @Test
    public void testExists1() {
        afterEach();
        //file to be tested
        String uri = "/underroot.txt";
        //make sure new file does not exist
        assertTrue(FileDirInfoRepository.getFile(uri) == null);
        //create file
        FileDirInfo file = FileDirInfoRepository.createNewFile(uri);
        //make sure create did return a file
        assertNotEquals(null, file);
        //make sure exists function returns right value
        assertTrue(FileDirInfoRepository.exists(file));
        //cleanup
        assertTrue(FileDirInfoRepository.deleteFileById(file.getId()));

        afterEach();
    }

    @Test
    public void testExists2() {
        afterEach();

        //file to be tested
        String uri = "/subdir1/subdir2/underroot.txt";
        //make sure new file does not exist
        assertNull(FileDirInfoRepository.getFile(uri));
    }

    @Test
    public void testExists3(){
        afterEach();
        String uri="/somthing.txt";
        FileDirInfo actualFile = FileDirInfoRepository.getFile(uri);
        boolean actual = FileDirInfoRepository.exists(actualFile);

        assertFalse(actual);
    }

    @Test
    public void testExists4(){
        afterEach();
        String uri="/";
        FileDirInfo actualFile = FileDirInfoRepository.getFile(uri);
        boolean actual = FileDirInfoRepository.exists(actualFile);

        assertTrue(actual);
    }

    @Test
    public void testExists5(){
        afterEach();
        String uri="/sub1/some.txt";
        FileDirInfo actualFile = FileDirInfoRepository.getFile(uri);
        boolean actual = FileDirInfoRepository.exists(actualFile);

        assertFalse(actual);
    }

    @Test
    public void testGetFileById() {
        afterEach();
        FileDirInfo file = FileDirInfoRepository.getFileById(1);

        assertEquals(file.getId(), 1);
    }

    @Test
    public void testGetChildernById() {
        log.info("Running repo testGetChildernById");
        afterEach();
        Integer parentId = 12345;
        insertFileByName(tempParentName, true, 1, parentId);
        FileDirInfo dir = FileDirInfo.builder().id(parentId).build();
        Integer childId = 13345;
        insertFileByName(tempFileName, false, parentId, childId);
        Integer childId2 = 13346;
        insertFileByName(tempFileName2, false, parentId, childId2);

        List<FileDirInfo> files = FileDirInfoRepository.getChildren(dir);

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
        afterEach();
        FileDirInfo root = FileDirInfoRepository.getRoot();

        assertNotNull(root);
        assertEquals(1, root.getId());
        assertEquals("", root.getName());
        assertNull(root.getParent());
    }

    @Test
    public void testGetRootDirs() {
        log.info("Running repo testGetRootDirs");
        afterEach();
        insertFileByName(tempParentName, true, 1, 12345);
        insertFileByName(tempFileName, true, 1, 12346);

        List<FileDirInfo> root = FileDirInfoRepository.getRootDirAndFiles();

        assertNotNull(root);
        assertTrue(root.size() >= 1);
        root.forEach(f -> assertEquals(f.getParent().getId(), 1));
    }

    @Test
    public void testCreateDir() {
        log.info("Running repo testCreateDir");
        afterEach();
        Integer pid = 12345;
        insertFileByName(tempParentName, true, 1, pid);
        String uri = SLASH + tempParentName + SLASH + tempFileName;

        FileDirInfoRepository.createNewDir(uri);

        List<FileDirInfo> actualDir = getByName(tempFileName);

        assertNotNull(actualDir);
        assertTrue(actualDir.size() == 1);
        FileDirInfo file = actualDir.get(0);
        assertEquals(tempFileName, file.getName());
        assertTrue(file.isDir());
        assertEquals(pid, file.getParent().getId());
    }

    @Test
    public void testDeleteFile() {
        log.info("Running repo testDeleteFile");
        afterEach();
        FileDirInfo p = FileDirInfoRepository.getRoot();
        Integer id = 12345;
        String fileName = tempFileName;
        //String qs = "insert into file_dir_info (id, name,is_dir,status_by_user,parent) values (" + id + ",'" + fileName + "',true,'test',1);";

        insertFileByName(fileName, true, 1, id);

        boolean isDeleted = FileDirInfoRepository.deleteFileById(id);
        assertTrue(isDeleted);

        List<FileDirInfo> actualDir = getByName(fileName);

        assertEquals(0, actualDir.size());

    }

    @Test
    public void testCreateByNativeQuery() {
        log.info("Running repo testCreateByNativeQuery");
        afterEach();
        String name = tempFileName;
        Integer pId = 1;
        FileDirInfo file = new FileDirInfo();
        file.setDir(true);
        file.setName(name);
        file.setStatusByUser("test");

        FileDirInfoRepository.createNewDir(file, pId);

        List<FileDirInfo> actualResult = getByName(name);
        assertNotNull(actualResult);
        assertTrue(actualResult.size() == 1);
        FileDirInfo actualFile = actualResult.get(0);
        assertEquals(name, actualFile.getName());
        assertEquals(pId, actualFile.getParent().getId());
        assertTrue(actualFile.isDir());

    }

    @Test
    public void testUpdate() {
        log.info("Running repo testUpdate");
        afterEach();
        Integer id = 12345;
        String fileName = tempFileName;
        Date oldLastModifiedDate = getDatBefore(7);

        insertFileByName(fileName,true, 1, id);

        String newStatusByUser = "test1234";
        FileDirInfo toBeUpdated = FileDirInfo.builder().id(id).statusByUser(newStatusByUser).build();

        FileDirInfoRepository.update(toBeUpdated);

        List<FileDirInfo> actualResult = getByName(fileName);

        assertEquals(1, actualResult.size());
        FileDirInfo actualUpdatedFile = actualResult.get(0);
        assertTrue(oldLastModifiedDate.getTime() < actualUpdatedFile.getLastModifiedDate().getTime());
        assertEquals(newStatusByUser, actualUpdatedFile.getStatusByUser());
    }

    @Test
    public void testCreateNewFileByUri(){
        log.info("Running repo testCreateNewFileByUri");
        afterEach();
        String name = tempFileName;
        String uri = "/" + name;

        FileDirInfoRepository.createNewFile(uri);

        List<FileDirInfo> actualResult = getByName(name);
        assertEquals( 1, actualResult.size());
        FileDirInfo actualFile = actualResult.get(0);
        assertEquals(name, actualFile.getName());
    }

    @Test
    public void testCreateNewFileByNestedUri(){
        log.info("Running repo testCreateNewFileByNestedUri");
        afterEach();
        Integer pId = 12345;
        Integer fId = 12346;
        insertFileByName(tempParentName, true, 1,pId);
        insertFileByName(tempParentName, true, pId, fId);

        String uri = "/" + tempParentName + "/" + tempFileName;

        FileDirInfo file = FileDirInfoRepository.createNewFile(uri);

        List<FileDirInfo> actualResult = getByName(tempFileName);

        assertEquals( 1, actualResult.size());
        FileDirInfo actualFile = actualResult.get(0);
        assertEquals(tempFileName, actualFile.getName());

        assertEquals(tempFileName, file.getName());
        assertEquals(tempParentName, file.getParent().getName());
    }

    private List<FileDirInfo> getByName(String name){
        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        Query q2 = em.createQuery("select f from file_dir_info f where f.name=:name")
                .setParameter("name", name);
        List<FileDirInfo> actualResult = q2.getResultList();
        em.close();
        return actualResult;
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
    }

    private Date getDatBefore(int days) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (-1) * days);
        return cal.getTime();
    }

}
