import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;


public class Client extends Commands {
    private String serverIP;
    private int serverPort=9999;

    public Client(String serverIP){
        this.serverIP=serverIP;
    }

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

    public String listFiles(String uri) throws Exception {
        Socket clientSocket=new Socket(serverIP,serverPort);
        PrintWriter printWriter=new PrintWriter(clientSocket.getOutputStream(), true);
        printWriter.println(list_files_cmd+" "+uri);
        String resp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        return resp;
    }
    public String listSubDirs(String uri) throws Exception {
        Socket clientSocket=new Socket(serverIP,serverPort);
        PrintWriter printWriter=new PrintWriter(clientSocket.getOutputStream(), true);
        printWriter.println(list_dir_cmd+" "+uri);
        String resp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        return resp;
    }

    public String fileExists(String uri) throws Exception{
        Socket clientSocket=new Socket(serverIP,serverPort);
        PrintWriter printWriter=new PrintWriter(clientSocket.getOutputStream(), true);
        printWriter.println(exists_file_cmd+" "+uri);
        String resp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        return resp;
    }

    public String dirExists(String uri) throws Exception{
        Socket clientSocket=new Socket(serverIP,serverPort);
        PrintWriter printWriter=new PrintWriter(clientSocket.getOutputStream(), true);
        printWriter.println(exists_dir_cmd+" "+uri);
        String resp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        return resp;
    }
    //--------below are not supported yet
    public boolean uploadFile(String uri, File file) throws Exception {
        throw new UnsupportedOperationException("not implemented");
        //return false;
    }

    public File downloadFile(String uri) throws Exception {
        //rmiServer.download(uri);
        throw new UnsupportedOperationException("not implemented");
        //return null;
    }

    public String getFileMeta(String uri) throws Exception {
        throw new UnsupportedOperationException("not implemented");
        //return null;
    }





}
