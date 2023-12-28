package org.tdl.vireo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.depositor.Depositor;

@Service
public class DepositorService {

    private final Map<String, Depositor> depositors;

    public DepositorService() {
        this.depositors = new HashMap<String, Depositor>();
    }

    public Depositor getDepositor(String depositorName) {
        return depositors.get(depositorName);
    }

    protected void addDepositor(Depositor depositor) {
        depositors.put(depositor.getName(), depositor);
    }

}
