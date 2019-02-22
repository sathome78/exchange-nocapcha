package me.exrates.service.bitshares.aunit;

import me.exrates.service.bitshares.BitsharesServiceImpl;
import org.springframework.stereotype.Service;

import javax.websocket.ClientEndpoint;

@Service("aunitServiceImpl")
@ClientEndpoint
public class AunitServiceImpl extends BitsharesServiceImpl {

    private final static String name = "AUNIT";

    public AunitServiceImpl() {
        super(name, name, "merchants/aunit.properties", 7);
    }
}
