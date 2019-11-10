import accessServer.AccessServer;
import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CompletableFuture;

public class App  {

    private static EntityManagerFactory factory;

    public static void main(String args[]) throws Exception {
        ServerController sc=new ServerController();
        sc.listenConnection(9999);
        while(true) {
            Socket client=sc.acceptConnection();
            CompletableFuture.runAsync(()->{
                while(true) {
                    try {
                        String s=sc.processMessage(client);
                        sc.sendMessage(s,client);;
                    } catch (IOException e) {
                        if(e.getMessage()=="Connection reset") {
                            sc.removeClient(client);
                            return;
                        }
                        e.printStackTrace();
                    }
                }
            });
        }

        //factory = EntityManagerHelper.getEntityManagerFactory();
        //testDB();

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
