
import accessServer.AccessServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ServerController extends Commands{


    private ServerSocket ss;
    private ArrayList<Socket> clients;

    public ServerController() {
        clients=new ArrayList<Socket>();
    }


    public void removeClient(Socket client) {
        if(clients.contains(client))
            clients.remove(client);
    }

    public String msgWrap(String msg) {
        if(!msg.endsWith("\n")) {
            msg+="\n";
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        msg="["+dateFormat.format(date)+"]"+msg;
        return msg;
    }

    public void listenConnection(int portNumber) throws IOException {
        ss=new ServerSocket(portNumber);
        System.out.println("Server Started at port "+portNumber);
    }

    public Socket acceptConnection() throws IOException {
        Socket client=ss.accept();
        clients.add(client);
        System.out.println("Client Connected "+client.getRemoteSocketAddress());
        return client;
    }

    public void sendMessage(String msg,Socket client) throws IOException {
        BufferedWriter out=new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        if(!msg.endsWith("\n")) {
            msg+="\n";
        }
        out.write(msg);
        out.flush();
        System.out.println("Msg "+msg.replace("\n", "")+" Sent to "+client.getRemoteSocketAddress());
    }

    public String processMessage(Socket client) throws IOException {

        BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
        String msg=in.readLine();

        String result="";
        try{
            result=handleCmd(msg);
        }catch (Exception e){
            msg=e.getMessage();
        }
        msg=client.getRemoteSocketAddress()+":"+msgWrap(msg);
        System.out.println("Msg "+msg.replace("\n", "")+" Received from "+client.getRemoteSocketAddress());
        System.out.println("Result: "+result);
        return msg+"\n"+msgWrap(result);
    }

    private String handleCmd(String msg) throws Exception {
        String cmd=msg.split(" ")[0];
        String uri=msg.split(" ")[1];
        AccessServer accessServer=new AccessServer();
        StringBuilder returnMsg=new StringBuilder();

        if(cmd.equals(create_file_cmd)){
            if(accessServer.createFile(uri)) {
                String syncResult=accessServer.syncCommand(msg);
                returnMsg.append("Create File: " + uri + "...Done\n"+syncResult);
            }else
                returnMsg.append("Create File: "+uri+"...Failed");
        }else if(cmd.equals(delete_file_cmd)) {
            if (accessServer.deleteFile(uri)) {
                String syncResult=accessServer.syncCommand(msg);
                returnMsg.append( "Delete File: " + uri + "...Done\n"+syncResult);
            }else
                returnMsg.append( "Delete File: "+uri+"...Failed");
        }else if(cmd.equals(create_dir_cmd)){
            if(accessServer.createDir(uri)) {
                String syncResult=accessServer.syncCommand(msg);
                returnMsg.append( "Create Dir: " + uri + "...Done\n"+syncResult);
            }else
                returnMsg.append( "Create Dir: "+uri+"...Failed");
        }else if(cmd.equals(delete_dir_cmd)){
            if(accessServer.deleteDir(uri)){
                String syncResult=accessServer.syncCommand(msg);
                returnMsg.append( "Delete Dir: "+uri+"...Done\n"+syncResult);
            }else
                returnMsg.append( "Delete Dir: "+uri+"...Failed");
        }else if(cmd.equals(list_files_cmd)){
            String[] list=accessServer.listFiles(uri);
            if(list!=null && list.length!=0){
                String s="Total Files: "+list.length+"\n";
                for (String s1 : list) {
                    s+=s1+"\n";
                }
                returnMsg.append( s+"List File: "+uri+"...Done");
            }else
                returnMsg.append( "List File: "+uri+"...Failed");
        }else if(cmd.equals(list_dir_cmd)){
            String[] list=accessServer.listSubDirs(uri);
            if(list!=null && list.length!=0){
                String s="Total Dir: "+list.length+"\n";
                for (String s1 : list) {
                    s+=s1+"\n";
                }
                returnMsg.append( s+"List Dir: "+uri+"...Done");
            }else
                returnMsg.append( "List Dir: "+uri+"...Failed");
        }else if(cmd.equals(exists_file_cmd)){
            if(accessServer.fileExists(uri))
                returnMsg.append( "File exists: "+uri+"...Done");
            else
                returnMsg.append( "File NOT exists: "+uri+"...Done");
        }else if(cmd.equals(exists_dir_cmd)){
            if(accessServer.dirExists(uri))
                returnMsg.append( "Dir exists: "+uri+"...Done");
            else
                returnMsg.append( "Dir NOT exists: "+uri+"...Done");
        }else{
            returnMsg.append("wrong command");
        }
        return returnMsg.toString();
    }
}