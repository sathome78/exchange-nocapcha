package me.exrates.service.tron;


import lombok.extern.log4j.Log4j2;
import me.exrates.service.achain.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class TronReceiveServiceImpl {

    @Autowired
    private NodeService nodeService;
    @Autowired
    private TronServiceImpl tronService;
}
