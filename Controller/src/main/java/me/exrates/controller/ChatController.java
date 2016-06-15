package me.exrates.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class ChatController {

//    @MessageMapping("/add" )
//    @SendTo("/topic/showResult")
//    public String addNum(String a) throws Exception {
//        return a;
//    }

//    @RequestMapping("/chat/poll")
//    public Map<String,String> pollMessages() {
//
//    }
}
