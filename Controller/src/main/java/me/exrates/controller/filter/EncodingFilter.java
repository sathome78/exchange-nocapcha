package me.exrates.controller.filter;

import me.exrates.controller.OnlineRestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by ogolv on 16.08.2016.
 */
public class EncodingFilter extends CharacterEncodingFilter {
    private static final Logger LOGGER = LogManager.getLogger(OnlineRestController.class);






}
