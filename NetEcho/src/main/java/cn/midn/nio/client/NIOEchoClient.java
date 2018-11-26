package cn.midn.nio.client;

import cn.midn.commons.ServerInfo;
import cn.midn.util.InputUtil;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOEchoClient {

    public static void main(String[] args) {
        try {
            new EchoClientHandle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class EchoClientHandle implements AutoCloseable {

    private SocketChannel clientChannel;

    public EchoClientHandle() throws Exception {
        this.clientChannel = SocketChannel.open();
        this.clientChannel.connect(new InetSocketAddress(ServerInfo.ECHOSERVERHOST, ServerInfo.PORT));
        this.accessServer();
    }

    public void accessServer() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(50);
        boolean falg = true;
        while (falg) {
            buffer.clear();
            String msg = InputUtil.getString("请输入内容：");
            buffer.put(msg.getBytes());
            buffer.flip();
            this.clientChannel.write(buffer);
            buffer.clear();
            int readCount = this.clientChannel.read(buffer);
            buffer.flip();
            System.out.println(new String(buffer.array(), 0, readCount));
            if ("exit".equalsIgnoreCase(msg)) {
                falg = false;
            }
        }

    }

    @Override
    public void close() throws Exception {
        this.clientChannel.close();
    }
}