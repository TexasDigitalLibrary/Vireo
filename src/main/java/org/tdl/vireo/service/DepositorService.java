package org.tdl.vireo.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.depositor.Depositor;
import org.tdl.vireo.model.depositor.SWORDv1Depositor;

@Service
public class DepositorService {

    private final Logger logger = LoggerFactory.getLogger(DepositorService.class);

    private final Map<String, Depositor> depositors;

    @PostConstruct
    void init() {
        logger.info("Loading SWORDv1 depositor");
        addDepositor(new SWORDv1Depositor());
    }

    DepositorService() {
        this.depositors = new HashMap<String, Depositor>();
    }

    public Depositor getDepositor(String depositorName) {
        return depositors.get(depositorName);
    }

    private void addDepositor(Depositor depositor) {
        depositors.put(depositor.getName(), depositor);
    }

}
