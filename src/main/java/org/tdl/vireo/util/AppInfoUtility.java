package org.tdl.vireo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppInfoUtility {

    @Value(value = "${app.host}")
    private String appHost;

    @Value(value = "${server.port:#{null}}")
    private String serverPort;

    @Value(value = "${server.contextPath:#{null}}")
    private String contextPath;

    public AppInfoUtility() {
    };

    public String getRunningAddress() {

        StringBuilder runningAddress = new StringBuilder(); // Using default 16 character size

        runningAddress.append(appHost);

        if (serverPort != null) {
            runningAddress.append(":");
            runningAddress.append(serverPort);
        }

        if (contextPath != null) {
            runningAddress.append(contextPath);
        }

        return runningAddress.toString();
    }

}
