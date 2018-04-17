package me.exrates.service;

import org.springframework.social.twitter.api.Tweet;

import java.util.List;

public interface TwitterService {

    List<Tweet> getTimeLine();

    List<Tweet> getTimeLine(Integer size);
}
