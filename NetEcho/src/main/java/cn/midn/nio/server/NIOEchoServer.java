package cn.midn.nio.server;

import cn.midn.commons.ServerInfo;
import com.sun.org.apache.bcel.internal.generic.Select;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOEchoServer {

public static void main(String[] args) throws Exception {
    new EchoServerHandler();
}

}
class SocketClientChannelThread implements Runnable{
    private SocketChannel socketChannel;
    private boolean flag = true;
    public SocketClientChannelThread(){}
    public SocketClientChannelThread( SocketChannel socketChannel){
        this.socketChannel = socketChannel;
        System.out.println("服务器daunt链接成功，可进行数据交互操作");

    }
    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(50);
        try  {
            while (flag){
                buffer.clear(); // 清空缓存操作 可是该缓存空间重复使用
                int readCount = this.socketChannel.read(buffer);
                String readMessage = new String(buffer.array(), 0, readCount).trim();
                System.out.println("服务器接收消息："+ readMessage);
                String writeMessage = "【echo】" + readMessage + "\n";
                if ("exit".equalsIgnoreCase(readMessage)){
                    writeMessage = "[echo] bye..."; // 结束消息
                    this.flag = false;
                }
                buffer.clear(); // 将保存的内容清除
                buffer.put(writeMessage.getBytes());// 保存回应信息
                buffer.flip(); // 重置缓存区
                this.socketChannel.write(buffer);
            }
            this.socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class EchoServerHandler implements AutoCloseable{
    private ExecutorService executorService;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private SocketChannel clientChannel;
    public EchoServerHandler() throws Exception{
        executorService = Executors.newFixedThreadPool(5);
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);// 设置为非阻塞模式
        this.serverSocketChannel.bind(new InetSocketAddress(ServerInfo.PORT));
        // nio之中的reactor是的模型重点在于所有的channel需要向selector进行注册
        this.selector = Selector.open(); // 获取selector的实例
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT); // 服务器端进行接收
        System.out.println("服务器端程序启动，监听端口：" + ServerInfo.PORT);
        this.clientHandel();
    }

    private void  clientHandel() throws IOException {
        int keySelector = 0;
        while ((keySelector = this.selector.select())>0){ // 需进行链接等待
            Set<SelectionKey> keys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()){
                SelectionKey next = iterator.next(); // 获取每个通道
                if (next.isAcceptable()){ // 该通道为接受状态
                    this.clientChannel = this.serverSocketChannel.accept();// 等待链接
                    if (this.clientChannel != null){
                        this.executorService.submit(new SocketClientChannelThread(this.clientChannel));
                    }
                }
                iterator.remove();
            }
        }
    }

    @Override
    public void close() throws Exception {
        this.executorService.shutdown();
        this.serverSocketChannel.close();
    }
}
