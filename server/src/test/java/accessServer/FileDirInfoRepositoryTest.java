package accessServer;

import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;
import accessServer.domain.repositories.FileDirInfoRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class FileDirInfoRepositoryTest {


    @Test
    @SneakyThrows
    public void testExists1(){

        try {
            //file to be tested
            String uri = "\\underroot.txt";
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
        }
    }

    @Test
    @SneakyThrows
    public void testExists2(){
        try {
            //file to be tested
            String uri = "\\subdir1\\subdir2\\underroot.txt";
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

        }
    }
    @Test
    @SneakyThrows
    public void testExists3(){
        String uri="\\somthing.txt";
        FileDirInfo actualFile = FileDirInfoRepository.getFile(uri);
        boolean actual = FileDirInfoRepository.exists(actualFile);
        assertFalse(actual);
    }

    @Test
    @SneakyThrows
    public void testExists4(){
        String uri="\\";
        FileDirInfo actualFile = FileDirInfoRepository.getFile(uri);
        boolean actual = FileDirInfoRepository.exists(actualFile);
        assertTrue(actual);
    }

    @Test
    @SneakyThrows
    public void testExists5(){
        String uri="\\sub1\\some.txt";
        FileDirInfo actualFile = FileDirInfoRepository.getFile(uri);
        boolean actual = FileDirInfoRepository.exists(actualFile);
        assertFalse(actual);
    }



    //pay attentation to data, I didn't prepare data
    @Test
    public void testGetFileById() {
        FileDirInfo file = FileDirInfoRepository.getFileById(1);
        assertEquals(file.getId(), 1);
    }

    @Test
    //pay attentation to data, I didn't prepare data
    public void testGetChildernById() {

        FileDirInfo p = FileDirInfoRepository.getRoot();
        FileDirInfo dir = FileDirInfo.builder().name("test_dir").isDir(true).parent(p).statusByUser("test").status("Ready").build();
        FileDirInfoRepository.createNewDir(dir);

        FileDirInfo file1=FileDirInfoRepository.createNewFile("\\test_dir\\"+"1.txt");



        List<FileDirInfo> files = FileDirInfoRepository.getChildren(dir);
        assertEquals(files.size(), 1);

        FileDirInfoRepository.deleteFileById(file1.getId());
        FileDirInfoRepository.deleteFileById(dir.getId());
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
        assertNotNull(root);
        assertEquals(1, root.getId());
        assertEquals("", root.getName());
        assertEquals(root,root.getParent());
    }

    @Test
    //pay attentation to data, I didn't prepare data
    public void testGetRootDirs() {
        List<FileDirInfo> root = FileDirInfoRepository.getRootDirAndFiles();
        assertNotNull(root);
        assertTrue(root.size() >= 1);
        root.forEach(f -> assertEquals(f.getParent().getId(), 1));
    }

    @Test
    public void testCreate() {
        FileDirInfo p = FileDirInfoRepository.getRoot();
        String fileName = UUID.randomUUID().toString();
        FileDirInfo dir = FileDirInfo.builder().name(fileName).isDir(true).parent(p).statusByUser("test").status("Ready").build();
        FileDirInfoRepository.createNewDir(dir);

        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Query q = em.createQuery("select f from file_dir_info f where f.name=:name").setParameter("name", fileName);
        List<FileDirInfo> actualDir = q.getResultList();

        assertNotNull(actualDir);
        assertTrue(actualDir.size() == 1);
        FileDirInfo file = actualDir.get(0);
        assertEquals(fileName, file.getName());
        assertEquals(p.getId(), file.getParent().getId());

        Query q2 = em.createNativeQuery("delete from file_dir_info f where f.name ='" + fileName + "'");
        q2.executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testDeleteFile() {
        FileDirInfo p = FileDirInfoRepository.getRoot();
        Integer id = 12345;
        String fileName = UUID.randomUUID().toString();
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

        assertEquals(0, actualDir.size());

    }

    @Test
    public void testCreateByNativeQuery() {
        String name = UUID.randomUUID().toString();
        Integer pId = 1;
        FileDirInfo file = new FileDirInfo();
        file.setDir(true);
        file.setName(name);
        file.setStatusByUser("test");


        FileDirInfoRepository.createNewDir(file, pId);

        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        Query q2 = em.createQuery("select f from file_dir_info f where f.name=:name").setParameter("name", name);
        List<FileDirInfo> actualResult = q2.getResultList();

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
        String fileName = UUID.randomUUID().toString();
        Date oldLastModifiedDate = getDatBefore(7);
        //String qs = "insert into file_dir_info (id, name,is_dir,status_by_user,parent) values (" + id + ",'" + fileName + "',true,'test',1);";

        EntityManager em = EntityManagerHelper.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Query q = em.createNativeQuery("insert into file_dir_info (id, name,is_dir,status_by_user,parent, last_modified_date) " +
                "VALUES (:id, :name, :is_dir, :status_by_user, :parent, :last_modified_date)")
                .setParameter("id", id)
                .setParameter("name", fileName)
                .setParameter("is_dir", true)
                .setParameter("status_by_user", "test")
                .setParameter("parent", 1)
                .setParameter("last_modified_date", oldLastModifiedDate);
        q.executeUpdate();
        em.getTransaction().commit();

        String newStatusByUser = "test1234";
        FileDirInfo toBeUpdated = FileDirInfo.builder().id(id).statusByUser(newStatusByUser).build();

        FileDirInfoRepository.update(toBeUpdated);

        Query q2 = em.createQuery("select f from file_dir_info f where f.id=:id")
                .setParameter("id", id);
        List<FileDirInfo> actualResult = q2.getResultList();

        Query q3 = em.createNativeQuery("delete from file_dir_info f where f.id =" + id+ "");
        em.getTransaction().begin();
        q3.executeUpdate();
        em.getTransaction().commit();
        em.close();

        assertEquals(1, actualResult.size());
        FileDirInfo actualUpdatedFile = actualResult.get(0);
        assertTrue(oldLastModifiedDate.getTime() < actualUpdatedFile.getLastModifiedDate().getTime());
        assertEquals(newStatusByUser, actualUpdatedFile.getStatusByUser());


    }

    @Test
    @SneakyThrows
    public void testCreateNewFileByUri(){
        String name = UUID.randomUUID().toString();
        String uri = "\\" + name;

        FileDirInfo file = FileDirInfoRepository.createNewFile(uri);

        List<FileDirInfo> actualResult = getByName(name);
        deleteByName(name);

        assertEquals( 1, actualResult.size());
        FileDirInfo actualFile = actualResult.get(0);
        assertEquals(name, actualFile.getName());
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

    private Date getDatBefore(int days) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (-1) * days);
        return cal.getTime();
    }

}
