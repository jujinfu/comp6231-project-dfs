package accessServer.domain;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerHelper {

    private static final String NAME = "MATADATADB";
    private static EntityManagerFactory emFactory;

    //creating factory is costly, only once in application life time
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emFactory == null) {
            emFactory = Persistence.createEntityManagerFactory(NAME);
        }
        return emFactory;
    }

    //close when application close, once in life cycle
    public static void close() {
        if (emFactory != null) {
            emFactory.close();
        }
    }
}
