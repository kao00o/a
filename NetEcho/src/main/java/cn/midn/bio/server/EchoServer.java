package cn.midn.bio.server;

import cn.midn.commons.ServerInfo;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class EchoServerHandler implements  AutoCloseable{
    private ServerSocket serverSocket = null;
    public EchoServerHandler() throws IOException {
        serverSocket = new ServerSocket(ServerInfo.PORT);
        System.out.println("ECHO 服务器端已经起东监听"+ ServerInfo.PORT);
        this.clientConnent();
    }
    private void clientConnent() throws IOException {
        boolean serverFlag = true;
        Socket client = this.serverSocket.accept();
        Thread clientThread = new Thread(()->{
            try {
                Scanner scanner = new Scanner(client.getInputStream());
                PrintStream out = new PrintStream(client.getOutputStream());
                scanner.useDelimiter("\n");
                boolean clientFlag = true;
                while (clientFlag){
                    if (scanner.hasNext()){
                        String inputData = scanner.next();
                        if ("exit".equals(inputData)){
                            out.println("[exho] bye...");
                            clientFlag = false;
                        }else {
                            out.println("[echo] " + inputData);
                        }
                    }
                }
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();
    }

    @Override
    public void close() throws Exception {
        this.serverSocket.close();
    }
}



public class EchoServer {

    public static void main(String[] args) throws IOException {
        new EchoServerHandler();
    }

}
