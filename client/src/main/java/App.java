import java.io.File;
import java.util.Date;
import java.util.Scanner;

public class App extends Commands{

    public static final void main(final String args[]){
        String serverIP="34.202.163.106";
        String serverIP2="35.156.72.221";

        try {
            Client client=new Client(serverIP);

            help();
            System.out.println("please enter command...");
            while(true){
                Scanner in = new Scanner(System.in);
                String s = in.nextLine();

                if(s.equals(help_cmd)){
                    help();
                    continue;
                }
                if(s.equals(exit_cmd)){
                    System.out.println("Exiting program");
                    System.exit(0);
                }
                if(s.split(" ").length!=2){
                    System.out.println("wrong command, try again. type help for instructions");
                    continue;
                }
                String cmd=s.split(" ")[0];
                String uri=s.split(" ")[1];
                String resp="";
                if(cmd.equals(create_file_cmd)){
                    System.out.println("creating file "+uri);
                    resp=client.createFile(uri);
                }else if(cmd.equals(delete_file_cmd)){
                    System.out.println("deleting file "+uri);
                    resp= client.deleteFile(uri);
                }else if(cmd.equals(create_dir_cmd)){
                    System.out.println("creating dir "+uri);
                    resp=client.createDir(uri);
                }else if(cmd.equals(delete_dir_cmd)){
                    System.out.println("deleting dir "+uri);
                    resp=client.deleteDir(uri);
                } else if (cmd.equals(list_files_cmd)) {
                    System.out.println("listing file of "+uri);
                    resp=client.listFiles(uri);
                }else if (cmd.equals(list_dir_cmd)) {
                    System.out.println("listing dir of "+uri);
                    resp=client.listSubDirs(uri);
                }else if (cmd.equals(exists_dir_cmd)) {
                    System.out.println("checking if dir exists  "+uri);
                    resp=client.dirExists(uri);
                }else if (cmd.equals(exists_file_cmd)) {
                    System.out.println("checking if file exists "+uri);
                    resp=client.fileExists(uri);
                }else{
                    resp="Wrong command "+s;
                }
                System.out.println("Server resp: "+resp);
                System.out.println("Command finished");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void help(){
        System.out.println("command example:");
        System.out.println("help # show instructions");

        System.out.println(create_file_cmd+" "+"/test.txt");
        System.out.println(create_dir_cmd+" "+"/testdir");

        System.out.println(list_files_cmd+" "+"/");
        System.out.println(list_dir_cmd+" "+"/");

        System.out.println(exists_file_cmd+" "+"/test.txt");
        System.out.println(exists_dir_cmd+" "+"/testdir");

        System.out.println(delete_file_cmd+" "+"/test.txt");
        System.out.println(delete_dir_cmd+" "+"/testdir");

        System.out.println();
        System.out.println("available commands");
        System.out.println(create_file_cmd+"\n"+delete_file_cmd);
        System.out.println(create_dir_cmd+"\n"+delete_dir_cmd);
        System.out.println(list_files_cmd+"\n"+list_dir_cmd);
        System.out.println(exists_file_cmd+"\n"+exists_dir_cmd);

    }

}
