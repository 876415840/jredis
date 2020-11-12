package org.jredis.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @Description: 接受連線請求線程
 * @Author MengQingHao
 * @Date 2020/5/12 11:20 上午
 */
@Slf4j
public class Acceptor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;

    public Acceptor(Selector selector, ServerSocketChannel ssc) {
        this.ssc=ssc;
        this.selector=selector;
    }

    @Override
    public void run() {
        try {
            // 接受client連線請求
            SocketChannel sc= ssc.accept();
            log.info("{} is connected.", sc.socket().getRemoteSocketAddress().toString());

            if(sc!=null) {
                // 設置為非阻塞
                sc.configureBlocking(false);
                // SocketChannel向selector註冊一個OP_READ事件，然後返回該通道的key
                SelectionKey sk = sc.register(selector, SelectionKey.OP_READ);
                // 使一個阻塞住的selector操作立即返回
                selector.wakeup();
                // 給定key一個附加的TCPHandler對象
                sk.attach(new TCPHandler(sk, sc));
            }

        } catch (IOException e) {
            log.error("accept error ", e);
        }
    }


}
