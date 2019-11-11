import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;


public class Client extends Commands {
    private String serverIP="34.202.163.106";
    private int serverPort=9999;


    public String createFile(String uri) throws Exception{
        Socket clientSocket=new Socket(serverIP,serverPort);
        PrintWriter printWriter=new PrintWriter(clientSocket.getOutputStream(), true);
        printWriter.println(create_file_cmd+" "+uri);
        System.out.println("Send to server: "+create_file_cmd+" "+uri);
        String resp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        return resp;
    }

    public String deleteFile(String uri) throws Exception {
        Socket clientSocket=new Socket(serverIP,serverPort);
        PrintWriter printWriter=new PrintWriter(clientSocket.getOutputStream(), true);
        printWriter.println(delete_file_cmd+" "+uri);
        String resp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        return resp;
    }

    public String createDir(String uri) throws Exception{
        Socket clientSocket=new Socket(serverIP,serverPort);
        PrintWriter printWriter=new PrintWriter(clientSocket.getOutputStream(), true);
        printWriter.println(create_dir_cmd+" "+uri);
        String resp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        return resp;
    }

    public String deleteDir(String uri) throws Exception{
        Socket clientSocket=new Socket(serverIP,serverPort);
        PrintWriter printWriter=new PrintWriter(clientSocket.getOutputStream(), true);
        printWriter.println(delete_dir_cmd+" "+uri);
        String resp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        return resp;
    }

    public boolean uploadFile(String uri, File file) throws Exception {
        //rmiServer.createFile(uri);
        return false;
    }

    public File downloadFile(String uri) throws Exception {
        //rmiServer.download(uri);
        return null;
    }

    public String getFileMeta(String uri) throws Exception {
        return null;
    }

    public String[] listSubDirs(String uri) throws Exception {
        return null;
    }

    public String[] listFiles(String uri) throws Exception {
        return null;
    }

}
