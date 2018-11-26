package cn.midn.bio.client;


import cn.midn.commons.ServerInfo;
import cn.midn.util.InputUtil;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

class EchoClientHandler implements AutoCloseable{
    private Socket client;

    public EchoClientHandler() throws IOException {
        this.client = new Socket(ServerInfo.ECHOSERVERHOST, ServerInfo.PORT);
        System.out.println("成功连接服务器端，可进行消息发送");
        this.accessServer();
    }

    private void accessServer() throws IOException {
        Scanner scanner = new Scanner(this.client.getInputStream());
        scanner.useDelimiter("\n");
        PrintStream out = new PrintStream(this.client.getOutputStream());
        boolean flag = true;
        while (flag){
            String data = InputUtil.getString("请输入发送数据：");
            out.println(data);
            if ("exit".equalsIgnoreCase(data)){
                flag =false;
            }
            if (scanner.hasNext()){
                System.out.println(scanner.next());
            }
        }

    }
    @Override
    public void close() throws Exception {
        this.client.close();
    }
}

public class EchoClient {
    public static void main(String[] args){
        try {
            EchoClientHandler echoClientHandler = new EchoClientHandler();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
