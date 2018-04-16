package me.exrates.service.impl;

import me.exrates.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TwitterServiceImpl implements TwitterService {

    private static final int TWEET_CACHE_SIZE = 50;

    private static List<Tweet> tweetCache;

    @Autowired
    private Twitter twitter;

    @PostConstruct
    private void prepareTweets() {
        this.updateTweets();
    }

    @Override
    public List<Tweet> getTweets() {
        return tweetCache;
    }

    @Override
    public void updateTweets() {
        tweetCache = twitter.timelineOperations().getUserTimeline(TWEET_CACHE_SIZE);
    }
}
