package me.exrates.service.impl;

import me.exrates.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwitterServiceImpl implements TwitterService {

    private static final int TIMELINE_DEFAULT_SIZE = 50;

    @Autowired
    private Twitter twitter;

    @Override
    public List<Tweet> getTimeLine() {
        return twitter.timelineOperations().getUserTimeline(TIMELINE_DEFAULT_SIZE);
    }

    @Override
    public List<Tweet> getTimeLine(Integer size) {
        return twitter.timelineOperations().getUserTimeline(size);
    }
}
