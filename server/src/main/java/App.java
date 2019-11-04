import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class App  {

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
    }
}
