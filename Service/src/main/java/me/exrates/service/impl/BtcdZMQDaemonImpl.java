package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Transaction;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.btcCore.btcDaemon.BtcDaemon;
import me.exrates.service.exception.BitcoinCoreException;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.zeromq.ZMQ.context;

@Log4j2
public class BtcdZMQDaemonImpl implements BtcDaemon{


    private static final String SOCKET_ADDRESS_BASE = "tcp://127.0.0.1:";
    private volatile boolean isActive = false;

    private BtcdClient btcdClient;
    private Context zmqContext;

    private ExecutorService listenerThreadPool = Executors.newFixedThreadPool(3);

    public BtcdZMQDaemonImpl(BtcdClient btcdClient, Context zmqContext) {
        this.btcdClient = btcdClient;
        this.zmqContext = zmqContext;
    }

    @Override
    public void init(Consumer<Block> blockHandler, Consumer<Transaction> walletHandler, Consumer<Transaction> instantSendHandler) {
        if (btcdClient == null) {
            throw new BitcoinCoreException("Client not initialized!");
        }
        if (zmqContext == null) {
            throw new BitcoinCoreException("ZmQ context not initialized!");
        }

        isActive = true;
        Properties nodeConfig = btcdClient.getNodeConfig();
        listenerThreadPool.submit(() -> initSubscriber(nodeConfig.getProperty("node.bitcoind.notification.block.port"),
                "hashblock", (hex) -> {
            Block block = getBlock(hex);
            blockHandler.accept(block);
        }));
        listenerThreadPool.submit(() -> initSubscriber(nodeConfig.getProperty("node.bitcoind.notification.wallet.port"),
                "hashtx", (hex) -> {
            Transaction transaction = getTransaction(hex);
            walletHandler.accept(transaction);
        }));
        listenerThreadPool.submit(() -> initSubscriber(nodeConfig.getProperty("node.bitcoind.notification.instantsend.port"),
                "hashtx", (hex) -> {
            Transaction transaction = getTransaction(hex);
            walletHandler.accept(transaction);
        }));
    }

    private void initSubscriber(String port, String topic, Consumer<String> handler) {
        log.debug("Subscribing on port: " + port);
        Socket subscriber = zmqContext.socket(ZMQ.SUB);
        if (port != null) {
            if (subscriber.connect(SOCKET_ADDRESS_BASE + port)) {
                subscriber.subscribe(topic);
                log.debug("Subscribed!");
                while (isActive) {
                    String hex = extractMessage(subscriber);
                    handler.accept(hex);
                }
                subscriber.close();
            } else {
                throw new BitcoinCoreException("Could not connect to port " + port);
            }


        }
    }

    private Block getBlock(String hex) {
        try {
            return btcdClient.getBlock(hex);
        } catch (BitcoindException | CommunicationException e) {
           throw new BitcoinCoreException(e);
        }
    }

    private Transaction getTransaction(String hex) {
        try {
            return btcdClient.getTransaction(hex);
        } catch (BitcoindException | CommunicationException e) {
            throw new BitcoinCoreException(e);
        }
    }

    private String extractMessage(Socket subscriber) {
        List<byte[]> multipartMessage = new ArrayList<>();
        byte[] message = subscriber.recv();
        multipartMessage.add(message);
        while (subscriber.hasReceiveMore()) {
            multipartMessage.add(subscriber.recv());
        }
        return DatatypeConverter.printHexBinary(multipartMessage.get(1)).toLowerCase();
    }

    public void destroy() {
        isActive = false;
        if (!zmqContext.isTerminated()) {
            zmqContext.term();
        }
    }


    public static void main(String[] args) {
        Context context = context(1);
        Socket subscriber = context.socket(ZMQ.SUB);

        subscriber.connect("tcp://127.0.0.1:5160");
        subscriber.subscribe("");
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("--- Start ---");
            List<byte[]> multipartMessage = new ArrayList<>();
            multipartMessage.add(subscriber.recv());
            while (subscriber.hasReceiveMore()) {
                multipartMessage.add(subscriber.recv());
            }
            System.out.println("1: " + new String(multipartMessage.get(0)));
            String hex = DatatypeConverter.printHexBinary(multipartMessage.get(1)).toLowerCase();
            System.out.println("2: " + hex);


            /*String contents = new String(subscriber.recv(0));
            System.out.println(contents);*/
        }
        subscriber.close();
        context.term();
    }
}
