import accessServer.AccessServer;
import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class App  {

    private static EntityManagerFactory factory;

    public static void main(String args[]) {
        Registry registry;
        try {
            // create the (local) object registry
            registry = LocateRegistry.createRegistry(9999);
            // bind the object to the name "server"
            registry.rebind("server", new AccessServer());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        factory = EntityManagerHelper.getEntityManagerFactory();
        testDB();

    }

    //example
    public static void testDB(){

        EntityManager em = factory.createEntityManager();
        FileDirInfo file = em.createNamedQuery("FileDirInfo.findById", FileDirInfo.class).setParameter("id", 2).getSingleResult();
        System.out.println(file.toString());
        System.out.println("parent id = " + file.getParent().getId());
        em.close();
    }

}
