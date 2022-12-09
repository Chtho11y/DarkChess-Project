package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ConnectThread extends Thread{

    static Server father;
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;
    int id;
    static int counter;

    public ConnectThread(Socket socket) throws IOException {
        this.socket = socket;
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer=new PrintWriter(socket.getOutputStream());
        id=++counter;
    }

    @Override
    public void run(){
        seedMessage("hello!");
        while(true){
            try {
                String s=reader.readLine();
                if(s==null||isInterrupted())return;
                WebMessage msg=new WebMessage(s);

                if(msg.isDisconnect()){
                    seedMessage("close");
                }else if(msg.isCloseAll()){
                    father.close();
                }else if(msg.isClosed()){
                    father.kill(id);
                    close();
                    System.out.println("disconnect:"+socket.getInetAddress());
                    return;
                }else if(msg.getMessage().equals("name")) {

                } else{
                    System.out.println(s);
                }

            } catch (IOException e) {
                if(isInterrupted())return;
                father.kill(id);
                return;
            }
        }
    }

    public void seedMessage(String s){
        writer.println(s);
        writer.flush();
        System.out.println("send:"+s);
    }

    public int getConnectId(){
        return id;
    }

    static void setFather(Server fa){
        father=fa;
    }

    void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
        interrupt();
    }
}

public class Server extends Thread {

    private ServerSocket serverSocket;
    ArrayList<ConnectThread> clients;

    ArrayList<String> members;

    void setName(int id,String name){
        members.set(id,name);
        StringBuilder s=new StringBuilder();
        for (String member : members) {
            s.append(member);
            s.append(' ');
        }
        send(WebMessage.toString("name",s.toString()));
    }

    Server() throws IOException {
        serverSocket=new ServerSocket(1145);
        ConnectThread.setFather(this);
        clients=new ArrayList<>();
    }

    @Override
    public void run(){
        while(true){
            try {
                Socket server= serverSocket.accept();

                if(isInterrupted()){
                    for (ConnectThread client : clients) {
                        try {
                            client.close();
                        } catch (IOException e) {

                        }
                    }
                    clients.clear();
                    return;
                }

                ConnectThread cl=new ConnectThread(server);
                System.out.println("connected:"+server.getInetAddress());
                members.add("");
                cl.start();
                clients.add(cl);
            } catch (IOException e) {
                return;
            }
        }
    }

    public void kill(int id){
        clients.removeIf(e->e.getConnectId()==id);
    }

    public void close(){
        try {
            interrupt();
            serverSocket.close();
        } catch (IOException e) {
        }
    }

    public void send(String s){
        for (ConnectThread client : clients) {
            client.seedMessage(s);
        }
    }
}
