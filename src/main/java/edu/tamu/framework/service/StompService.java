/* 
 * StompService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.weaver.response.ApiResponse;

/**
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Service
public class StompService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.stomp.retries:5}")
    private int MAX_RETRIES;

    private static Map<String, ReliableResponse> reliableMessages = new ConcurrentHashMap<String, ReliableResponse>();

    private static int totalActiveConnections = 0;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public synchronized void incrementActiveConnections() {
        totalActiveConnections++;
    }

    public synchronized void decrementActiveConnections() {
        totalActiveConnections--;
    }

    public synchronized int getActiveConnections() {
        return totalActiveConnections;
    }

    public void sendReliableMessage(String destination, String requestId, ApiResponse response) {
        reliableMessages.put(destination + "-" + requestId, new ReliableResponse(response));
        simpMessagingTemplate.convertAndSend(destination, response);
    }

    public void ackReliableMessage(String destination, String requestId) {
        logger.info("Reliable message acknowledged: " + destination);
        reliableMessages.remove(destination + "-" + requestId);
    }

    @Scheduled(fixedDelayString = "${app.stomp.resend.interval:2500}")
    public synchronized void resendUnacknowledgedMessages() {

        for (Map.Entry<String, ReliableResponse> entry : reliableMessages.entrySet()) {
            String destination = entry.getKey();
            ReliableResponse reliableResponse = entry.getValue();
            if (reliableResponse.getRetry() > MAX_RETRIES) {
                logger.info("Unable to receive acknowledgement after " + MAX_RETRIES + " attempts: " + destination);
                reliableMessages.remove(destination);
            } else {
                reliableResponse.incrementRetry();
                simpMessagingTemplate.convertAndSend(destination.substring(0, destination.lastIndexOf("-")), entry.getValue().getApiReponse());
            }
        }

    }

    class ReliableResponse {
        private int retry;
        private ApiResponse apiReponse;

        public ReliableResponse(ApiResponse apiReponse) {
            setRetry(0);
            setApiReponse(apiReponse);
        }

        public int getRetry() {
            return retry;
        }

        public void setRetry(int retry) {
            this.retry = retry;
        }

        public void incrementRetry() {
            this.retry++;
        }

        public ApiResponse getApiReponse() {
            return apiReponse;
        }

        public void setApiReponse(ApiResponse apiReponse) {
            this.apiReponse = apiReponse;
        }
    }

}
