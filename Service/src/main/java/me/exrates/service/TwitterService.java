package me.exrates.service;

import org.springframework.social.twitter.api.Tweet;

import java.util.List;

public interface TwitterService {

    List<Tweet> getTweets();

    void updateTweets();
}
