package cn.midn.aio.server.client;

import cn.midn.commons.ServerInfo;
import cn.midn.util.InputUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

class ClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel clientChannel;
    private CountDownLatch latch;

    public ClientReadHandler(AsynchronousSocketChannel clientChannel,CountDownLatch latch){
        this.clientChannel = clientChannel;
        this.latch = latch;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();
        String receiveMsg = new String(buffer.array(), 0, buffer.remaining());
        System.out.println(receiveMsg);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.latch.countDown();
    }
}

class ClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel clientChannel;
    private CountDownLatch latch;

    public ClientWriteHandler(AsynchronousSocketChannel clientChannel, CountDownLatch latch) {
        this.clientChannel = clientChannel;
        this.latch = latch;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if (buffer.hasRemaining()){
            this.clientChannel.write(buffer,buffer,this);

        }else {
            ByteBuffer readBuffer = ByteBuffer.allocate(50);
            this.clientChannel.read(readBuffer,readBuffer,new ClientReadHandler(this.clientChannel,this.latch));
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.latch.countDown();
    }
}

class AIOClientThread implements Runnable {

    private AsynchronousSocketChannel clientChannel;
    private CountDownLatch latch;

    public AIOClientThread() throws IOException {
        this.clientChannel = AsynchronousSocketChannel.open()        ;
        this.clientChannel.connect(new InetSocketAddress(ServerInfo.ECHOSERVERHOST, ServerInfo.PORT));
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            this.latch.await();
            this.clientChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendMsg(String msg) {
        ByteBuffer buffer = ByteBuffer.allocate(50);
        buffer.put(msg.getBytes());
        buffer.flip();
        this.clientChannel.write(buffer, buffer, new ClientWriteHandler(this.clientChannel, this.latch));
        if ("exit".equalsIgnoreCase(msg)) {
            return false;
        }
        return true;
    }
}


public class AIOEchoClient {
    public static void main(String[] args) throws IOException {
        AIOClientThread client = new AIOClientThread();
        new Thread(client).start();
        while (client.sendMsg(InputUtil.getString("请输入信息"))){

        }

    }
}