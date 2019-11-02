import java.io.File;
import java.util.Date;

public class App {
    public static final void main(final String args[]){

        try {
            Client client = new Client();
            client.start();
            client.listSubDirs("/");
            client.listFiles("/");
            File file=client.openFile("/test.txt");
            client.saveFile("/test.txt",("Hello "+(new Date().getTime())).getBytes("utf-8"));
            client.closeFile("/text.txt");
            client.deleteFile("/text.txt");

            client.uploadFile("/upload.txt",file);
            file=client.downloadFile("/upload.txt");

            client.getFileMeta("/upload.txt");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
