package org.tdl.vireo.mock;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;

public class MockMessageChannel implements SubscribableChannel {

    public List<Message<byte[]>> getMessages() {
        return new ArrayList<>();
    }

    @Override
    public boolean send(Message<?> message) {
        return true;
    }

    @Override
    public boolean send(Message<?> message, long timeout) {
        return true;
    }

    @Override
    public boolean subscribe(MessageHandler handler) {
        return true;
    }

    @Override
    public boolean unsubscribe(MessageHandler handler) {
        return true;
    }

}