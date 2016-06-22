package me.exrates.service.impl;

import me.exrates.dao.ChatDao;
import me.exrates.model.ChatMessage;
import me.exrates.model.User;
import me.exrates.model.enums.ChatLang;
import me.exrates.service.ChatService;
import me.exrates.service.UserService;
import me.exrates.service.annotation.Mutable;
import me.exrates.service.exception.IllegalChatMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static me.exrates.model.enums.ChatLang.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final EnumMap<ChatLang, Triplet> chats = new EnumMap<>(ChatLang.class);

    private final int MESSAGE_BARRIER = 50;
    private final int CACHE_BARRIER = 150;
    private final boolean INCLUSIVE = true;

    private final ChatDao chatDao;
    private final UserService userService;

    private AtomicLong GENERATOR;
    private long flushCursor;

    private final Predicate<String> deprecatedChars = Pattern.compile("^[^<>{}&*'\"/;`%]*${1,256}$").asPredicate();

    @Autowired
    public ChatServiceImpl(final ChatDao chatDao, final UserService userService) {
        this.chatDao = chatDao;
        this.userService = userService;
    }

    @PostConstruct
    public void cacheWarm() {
        final TreeSet<ChatMessage> enMessages = new TreeSet<>(chatDao.findLastMessages(EN, MESSAGE_BARRIER));
        final TreeSet<ChatMessage> ruMessages = new TreeSet<>(chatDao.findLastMessages(RU, MESSAGE_BARRIER));
        final TreeSet<ChatMessage> cnMessages = new TreeSet<>(chatDao.findLastMessages(CN, MESSAGE_BARRIER));

        final ChatMessage enTail = enMessages.isEmpty() ? null : enMessages.last();
        final ChatMessage ruTail = ruMessages.isEmpty() ? null : ruMessages.last();
        final ChatMessage cnTail = cnMessages.isEmpty() ? null : cnMessages.last();

        final Triplet en = new Triplet(new ReentrantReadWriteLock(), enMessages, enTail);
        final Triplet ru = new Triplet(new ReentrantReadWriteLock(), ruMessages, ruTail);
        final Triplet cn = new Triplet(new ReentrantReadWriteLock(), cnMessages, cnTail);

        chats.put(EN, en);
        chats.put(RU, ru);
        chats.put(CN, cn);

        final long enId = enMessages.isEmpty() ? 0 : enMessages.first().getId();
        final long ruId = ruMessages.isEmpty() ? 0 : ruMessages.first().getId();
        final long cnId = cnMessages.isEmpty() ? 0 : cnMessages.first().getId();

        GENERATOR = new AtomicLong(Math.max(Math.max(enId, ruId), cnId));
        flushCursor = GENERATOR.get();
    }

    @Override
    public ChatMessage persistMessage(final String body, final String email, ChatLang lang) throws IllegalChatMessageException {
        if (!deprecatedChars.test(body)) {
            throw new IllegalChatMessageException("Message contains invalid symbols : " + body);
        }
        final User user = userService.findByEmail(email);
        final ChatMessage message = new ChatMessage();
        message.setBody(body);
        message.setUserId(user.getId());
        message.setNickname(user.getNickname());
        message.setId(GENERATOR.incrementAndGet());
        final Triplet tri = chats.get(lang);
        try {
            tri.lock.writeLock().lock();
            tri.cache.add(message);
            if (isNull(tri.tail)) {
                tri.tail = tri.cache.last();
            }
            if (tri.cache.size() > MESSAGE_BARRIER) {
                tri.tail = tri.cache.lower(tri.tail);
            }
            if (tri.cache.size() == CACHE_BARRIER) {
                tri.cache = new TreeSet<>(tri.cache.headSet(tri.tail, INCLUSIVE));
            }
        } finally {
            tri.lock.writeLock().unlock();
        }
        return message;
    }

    public NavigableSet<ChatMessage> getLastMessages(final ChatLang lang) {
        final Triplet tri = chats.get(lang);
        if (tri.cache.size() == 0) {
            return new TreeSet<>();
        }
        final NavigableSet<ChatMessage> result;
        try {
            tri.lock.readLock().lock();
            result = new TreeSet<>(tri.cache.headSet(tri.tail, INCLUSIVE));
        } finally {
            tri.lock.readLock().unlock();
        }
        return result;
    }

    @Scheduled(fixedDelay = 60000L, initialDelay = 60000L)
    public void flushCache() {
        flushNewData(EN,RU,CN);
    }

    private void flushNewData(final ChatLang ...chatLangs) {
        for (ChatLang lang : chatLangs) {
            final ChatMessage cacheCeil = new ChatMessage();
            cacheCeil.setId(flushCursor);
            final ChatMessage higher = getLastMessages(lang).lower(cacheCeil);
            if (higher != null) {
                final NavigableSet<ChatMessage> newMessages = getLastMessages(lang).headSet(higher, INCLUSIVE);
                chatDao.persist(lang, newMessages);
                final long newFlushCursor = newMessages.first().getId();
                if (flushCursor < newFlushCursor) {
                    flushCursor = newFlushCursor;
                }
            }
        }
    }

    private static class Triplet {

        private final ReadWriteLock lock;
        private NavigableSet<ChatMessage> cache;
        private ChatMessage tail;

        Triplet(final ReadWriteLock lock, @Mutable NavigableSet<ChatMessage> cache, @Mutable ChatMessage tail) {
            this.lock = lock;
            this.cache = cache;
            this.tail = tail;
        }
    }
}
