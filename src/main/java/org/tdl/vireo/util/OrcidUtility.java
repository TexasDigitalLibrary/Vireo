package org.tdl.vireo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.framework.util.HttpUtility;

@Service
public class OrcidUtility {
    
    @Autowired
    private HttpUtility httpUtility;
    
    public Boolean verifyOrcid(String orcid) {
        System.err.println(orcid);
        return true;
    }
    
}
