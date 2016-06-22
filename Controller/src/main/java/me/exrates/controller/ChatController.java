package me.exrates.controller;

import com.google.gson.Gson;
import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.model.ChatMessage;
import me.exrates.model.enums.ChatLang;
import me.exrates.service.ChatService;
import me.exrates.service.annotation.ThreadSafe;
import me.exrates.service.exception.IllegalChatMessageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static java.util.Collections.*;
import static me.exrates.model.enums.ChatLang.EN;
import static me.exrates.model.enums.ChatLang.RU;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@ThreadSafe
@RestController
public class ChatController {

    private final ChatService chatService;
    private final MessageSource messageSource;

    @Resource(name = "chatEN")
    private ChatWebSocketHandler enHandler;
    @Resource(name = "chatRU")
    private ChatWebSocketHandler ruHandler;
    @Resource(name = "chatCN")
    private ChatWebSocketHandler cnHandler;


    private final Gson gson = new Gson();
    private final Logger LOG = LogManager.getLogger(ChatController.class);


    public ChatController(final ChatService chatService, final MessageSource messageSource) {
        this.chatService = chatService;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/chat/new-message", method = POST)
    public ResponseEntity<Map<String,String>> newMessage(final @RequestParam("body") String body,
                                             final @RequestParam("lang") String lang,
                                             final Principal principal,
                                             final Locale locale)
    {
        ChatLang chatLang = ChatLang.toInstance(lang);
        final ChatMessage message;
        try {
            message = chatService.persistMessage(body, principal.getName(), chatLang);
        } catch (IllegalChatMessageException e) {
            LOG.error(e);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.put("Content-type", singletonList("application/json; charset=utf-8"));
            return new ResponseEntity<>(singletonMap("errorInfo",
                    messageSource.getMessage("chat.invalidSymbols", null, locale)), headers, BAD_REQUEST);
        }
        final List<WebSocketSession> sessions;
        if (chatLang.equals(RU)) {
            sessions = ruHandler.getSessions();
        } else if (chatLang.equals(EN)) {
            sessions = enHandler.getSessions();
        } else {
            sessions = cnHandler.getSessions();
        }
        sessions.forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(new TextMessage(gson.toJson(message)));
            } catch (IOException e) {
                LOG.error(e);
            }
        });
        return new ResponseEntity<>(OK);
    }

    @RequestMapping(value = "/chat/history", method = GET)
    public Set<ChatMessage> chatMessages(final @RequestParam("lang") String lang) {
        return chatService.getLastMessages(ChatLang.toInstance(lang));
    }
}
