package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Thread {
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;

    String title;
    String number;
    ArrayList<String> members;


    boolean closed;

   void connect(String ip) throws IOException {
        socket=new Socket(ip,1145);
        writer=new PrintWriter(socket.getOutputStream());
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
       System.out.println("connected:"+ip);
    }

    void send(String message){
        writer.println(message);
        writer.flush();
    }

    void close() throws IOException {
       send("closed");
       writer.close();
       reader.close();
       socket.close();
       closed=true;
    }

    boolean isClosed(){
       return closed;
    }

    @Override
    public void run(){
       while(true){
           try {
               String s=reader.readLine();
               if(s==null||s.equals("close")) {
                   close();
                   return;
               }else System.out.println(s);
           } catch (IOException e) {
               System.out.println("connect failed.");
           }

       }
    }
}
