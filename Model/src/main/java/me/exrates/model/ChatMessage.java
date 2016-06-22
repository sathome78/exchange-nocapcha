package me.exrates.model;

import java.util.Comparator;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class ChatMessage implements Comparable<ChatMessage> {

    private Integer userId;
    private String  nickname;
    private String  body;
    private long id;

    private static final Comparator<ChatMessage> comparator = Comparator.comparingLong(ChatMessage::getId);

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage that = (ChatMessage) o;

        if (id != that.id) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (nickname != null ? !nickname.equals(that.nickname) : that.nickname != null) return false;
        return body != null ? body.equals(that.body) : that.body == null;

    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", body='" + body + '\'' +
                ", id=" + id +
                '}';
    }


    @Override
    public int compareTo(final ChatMessage o) {
        return comparator.compare(o, this);
    }
}
