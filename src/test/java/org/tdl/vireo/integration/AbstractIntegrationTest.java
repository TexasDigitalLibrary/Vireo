package org.tdl.vireo.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.tdl.vireo.Application;
import org.tdl.vireo.mock.MockData;
import org.tdl.vireo.mock.interceptor.MockChannelInterceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
public abstract class AbstractIntegrationTest extends MockData {

    protected static final String jwtString = "ToY1Y3O6o-iESvV_c3NR3XHvi1vqfYtLIB83HC-d80_qqS98EzfWfPxkP2faL-tEIu9b9wm-q0-T3aNzBWxphQ7ZVxnJSYvtBLzEAh7WVYbezyA3Dgj-nMbsUypCujnuEa0fDwnXXSDr2DRjw4JakMACdPZifN6hcz5-oYFqgWFOPvDE3Gr28ko4XpExXcRaZxYv0p4KW7ISquenB9clu6aKjoKrNwbXqHQvXyk_uFD7GmNuG7RB5Je3jrS0a4q5GSdRyegcoxbGGsNGTKx1f7-f6U4pAHD6fYUmoheZag975Py1Bk8PtUmI8rFWbh8YYPhnnOjGIdJ9qMBFC4-hmVi5hVGhqRdIX9VFeQjxP9gHGqQf5uihmJ8WUY9jh1IP";

    protected static final byte[] payload = new byte[] {};

    @Autowired
    protected AbstractSubscribableChannel clientInboundChannel;

    @Autowired
    protected AbstractSubscribableChannel brokerChannel;

    protected MockChannelInterceptor brokerChannelInterceptor;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @Before
    public abstract void setup();

    @After
    public abstract void cleanup();

    protected void StompConnect() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.CONNECT);

        headers.setSubscriptionId("0");
        headers.setDestination("/connect");

        headers.setNativeHeader("id", "0");
        headers.setNativeHeader("jwt", jwtString);

        Message<byte[]> message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());

        clientInboundChannel.send(message);
    }

    public String StompRequest(String destination, Object data) throws InterruptedException {
        return StompRequest(destination, objectMapper.convertValue(data, JsonNode.class).toString());
    }

    public String StompRequest(String destination, Map<String, Object> data) throws InterruptedException {
        return StompRequest(destination, objectMapper.convertValue(data, JsonNode.class).toString());
    }

    public String StompRequest(String destination, String jsonNodeString) throws InterruptedException {
        String root = destination.split("/")[1];

        String sessionId = String.valueOf(Math.round(Math.random() * 100000));
        String id = String.valueOf(Math.round(Math.random() * 100000));

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);

        headers.setDestination("/ws" + destination);
        headers.setSessionId(sessionId);

        headers.setNativeHeader("id", id);
        headers.setNativeHeader("jwt", jwtString);

        if (jsonNodeString != null && !jsonNodeString.isEmpty()) {
            headers.setNativeHeader("data", jsonNodeString);
        }

        headers.setSessionAttributes(new HashMap<String, Object>());

        Message<byte[]> message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());

        brokerChannelInterceptor.setIncludedDestinations("/queue/" + root + "/**");

        boolean sent = clientInboundChannel.send(message);

        assertEquals(true, sent);

        Message<?> reply = brokerChannelInterceptor.awaitMessage();

        assertNotNull(reply);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);

        assertEquals("/queue" + destination + "-user" + sessionId, replyHeaders.getDestination());

        Thread.sleep(100); // H2 needs time to commit/persist any sent messages

        return new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
    }

}
