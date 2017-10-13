package me.exrates.service.impl;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;

import static org.zeromq.ZMQ.context;

public class BtcZMQServiceImpl {


    public static void main(String[] args) {
        Context context = context(1);
        Socket subscriber = context.socket(ZMQ.SUB);

        subscriber.connect("tcp://127.0.0.1:5159");
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
