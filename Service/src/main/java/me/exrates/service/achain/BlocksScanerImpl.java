package me.exrates.service.achain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Maks on 14.06.2018.
 */
@Service
public class BlocksScanerImpl implements BlocksScaner {

    @Autowired
    private NodeService nodeService;

    @Override
    public void scan() {}
}
