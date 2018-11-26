package cn.midn.aio.server;


import cn.midn.commons.ServerInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;


class EchoHandler implements CompletionHandler<Integer, ByteBuffer>{

    private boolean exit = false; //
    private AsynchronousSocketChannel clientChannel;
    public EchoHandler(AsynchronousSocketChannel clientChannel){
        this.clientChannel = clientChannel;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();
        String readMasg = new String (buffer.array(), 0 ,buffer.remaining()).trim();
        System.out.println("服务器端接收到：" + readMasg + "\n");
        String resultMsg = "[echo]"+ readMasg + "\n";
        if ("exit".equalsIgnoreCase(readMasg)){
            resultMsg = "[echo] bye... \n";
        }
        this.echoWrite(resultMsg);
    }

    private void echoWrite(String result){
        ByteBuffer buffer = ByteBuffer.allocate(50);
        buffer.put(result.getBytes());
        buffer.flip();
        this.clientChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()){ // 当前有数据
                    EchoHandler.this.clientChannel.write(buffer,buffer,this);
                }else {
                    if (EchoHandler.this.exit == false){
                        ByteBuffer buffer1 = ByteBuffer.allocate(50);
                        EchoHandler.this.clientChannel.read(buffer1,buffer1, new EchoHandler(EchoHandler.this.clientChannel));
                    }
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    EchoHandler.this.clientChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AIOServerThread>{

    @Override
    public void completed(AsynchronousSocketChannel result, AIOServerThread attachment) {
        attachment.getServerSocketChannel().accept(attachment, this);// 接受链接对象
        ByteBuffer buffer = ByteBuffer.allocate(50);
        result.read( buffer, buffer, new EchoHandler(result));
    }

    @Override
    public void failed(Throwable exc, AIOServerThread attachment) {
        System.out.println("服务器客户端连接失败。。");
        attachment.getLatch().countDown(); //恢复执行
    }
}

class AIOServerThread implements Runnable{ // 进行io处理的线程类
    private AsynchronousServerSocketChannel serverSocketChannel;
    private CountDownLatch latch;// 线程等待操作
    public AIOServerThread() throws IOException {
        this.latch = new CountDownLatch(1);
        this.serverSocketChannel = AsynchronousServerSocketChannel.open();
        this.serverSocketChannel.bind(new InetSocketAddress(ServerInfo.PORT));
        System.out.println("服务器启动成功，端口：" + ServerInfo.PORT);
    }

    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    @Override
    public void run() {
        this.serverSocketChannel.accept(this, new AcceptHandler());
        try {
            this.latch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

public class AIOEchoServer {
    public static void main(String[] args) throws IOException {
      new Thread(new AIOServerThread()).start();

    }
}
