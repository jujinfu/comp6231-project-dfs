
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

        try{
            handleCmd(msg);
        }catch (Exception e){
            msg=e.getMessage();
        }
        msg=client.getRemoteSocketAddress()+":"+msgWrap(msg);
        System.out.println("Msg "+msg.replace("\n", "")+" Received from "+client.getRemoteSocketAddress());
        return msg;
    }

    private String handleCmd(String msg) throws Exception {
        String cmd=msg.split(" ")[0];
        String uri=msg.split(" ")[1];
        AccessServer accessServer=new AccessServer();
        if(cmd.equals(create_file_cmd)){
            accessServer.createFile(uri);
            return "Create File: "+uri+"...Done";
        }else if(cmd.equals(delete_file_cmd)){
            accessServer.deleteFile(uri);
            return "Delete File: "+uri+"...Done";
        }
        return null;
    }
}