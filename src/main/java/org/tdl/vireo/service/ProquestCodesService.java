package org.tdl.vireo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ProquestCodesService {

    private Map<String, Map<String, String>> codes;

    public ProquestCodesService() {
        codes = new HashMap<String, Map<String, String>>();
    }

    public void setCodes(String key, Map<String, String> codes) {
        checkCreateCodes(key);
        this.codes.put(key, codes);
    }

    public Map<String, String> getCodes(String key) {
        checkCreateCodes(key);
        return codes.get(key);
    }

    private void checkCreateCodes(String key) {
        if (this.codes.get(key) == null) {
            this.codes.put(key, new HashMap<String, String>());
        }
    }

}
