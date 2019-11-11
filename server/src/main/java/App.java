import accessServer.AccessServer;
import accessServer.domain.EntityManagerHelper;
import accessServer.domain.entities.FileDirInfo;
import accessServer.domain.repositories.FileDirInfoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class App  {

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
    }


}
