package mk12;

import mk12.model.ChatMessage;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;

@ServerEndpoint("/chat/{username}")
@Component
public class ChatServer {
    private static final ConcurrentHashMap<Session, String> sessionUsernameMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Session> usernameSessionMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, List<ChatMessage>> messageHistory = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        ChatServer.objectMapper = objectMapper;
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        logger.info("[onOpen] " + username);

        if (usernameSessionMap.containsKey(username)) {
            try {
                session.getBasicRemote().sendText("Username already exists");
                session.close();
            } catch (IOException e) {
                logger.error("Error during session closure: " + e.getMessage());
            }
            return;
        }

        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        messageHistory.putIfAbsent(username, new ArrayList<>());
        sendMessageHistory(session, username);

        ChatMessage joinMessage = new ChatMessage("System", "User: " + username + " has Joined the Chat", ChatMessage.MessageType.JOIN);
        broadcast(joinMessage);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        String username = sessionUsernameMap.get(session);
        logger.info("[onMessage] " + username + ": " + message);

        ChatMessage chatMessage;
        if (message.startsWith("@")) {
            String[] parts = message.split(" ", 2);
            if (parts.length > 1) {
                String targetUser = parts[0].substring(1);
                String privateMessage = parts[1];
                chatMessage = new ChatMessage(username, privateMessage, targetUser);
                sendPrivateMessage(chatMessage);
            }
        } else if (message.startsWith("READ:")) {
            handleReadReceipt(username, message.substring(5));
        } else {
            chatMessage = new ChatMessage(username, message, (String)null);
            broadcast(chatMessage);
        }
    }

    private void handleReadReceipt(String username, String messageId) {
        messageHistory.values().forEach(messages -> {
            messages.stream()
                    .filter(msg -> msg.getMessageId().equals(messageId))
                    .forEach(msg -> {
                        msg.markAsReadBy(username);
                        logger.info("Message {} marked as read by {}", messageId, username);
                        logger.info("Current readers: {}", msg.getReadBy());
                        broadcastReadStatus(messageId, username, new HashSet<>(msg.getReadBy()));
                    });
        });
    }

    private void sendMessageHistory(Session session, String username) {
        try {
            for (List<ChatMessage> messages : messageHistory.values()) {
                for (ChatMessage msg : messages) {
                    if (msg.getRecipient() == null ||
                            msg.getRecipient().equals(username) ||
                            msg.getSender().equals(username)) {
                        String messageJson = formatMessageToJson(msg);
                        session.getBasicRemote().sendText(messageJson);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error sending message history: " + e.getMessage());
        }
    }

    private void sendPrivateMessage(ChatMessage message) {
        Session recipientSession = usernameSessionMap.get(message.getRecipient());
        if (recipientSession != null && recipientSession.isOpen()) {
            try {
                String messageJson = formatMessageToJson(message);
                recipientSession.getBasicRemote().sendText(messageJson);

                Session senderSession = usernameSessionMap.get(message.getSender());
                if (senderSession != null && senderSession.isOpen()) {
                    senderSession.getBasicRemote().sendText(messageJson);
                }

                messageHistory.get(message.getSender()).add(message);
                messageHistory.get(message.getRecipient()).add(message);
            } catch (IOException e) {
                logger.error("Error sending private message: " + e.getMessage());
            }
        }
    }

    private void broadcast(ChatMessage message) {
        String messageJson = formatMessageToJson(message);

        // Store message in history first
        sessionUsernameMap.values().forEach(username ->
                messageHistory.get(username).add(message)
        );

        // Then broadcast to all connected sessions
        sessionUsernameMap.keySet().stream()
                .filter(Session::isOpen)
                .forEach(session -> {
                    try {
                        session.getBasicRemote().sendText(messageJson);
                    } catch (IOException e) {
                        logger.error("Error broadcasting message: " + e.getMessage());
                    }
                });
    }

    private void broadcastReadStatus(String messageId, String readByUsername, HashSet<String> allReaders) {
        ChatMessage readReceipt = new ChatMessage();
        readReceipt.setType(ChatMessage.MessageType.READ_RECEIPT);
        readReceipt.setMessageId(messageId);
        readReceipt.setReadBy(allReaders);

        String readReceiptJson = formatMessageToJson(readReceipt);
        logger.info("Broadcasting read receipt: {}", readReceiptJson);

        sessionUsernameMap.keySet().stream()
                .filter(Session::isOpen)
                .forEach(session -> {
                    try {
                        session.getBasicRemote().sendText(readReceiptJson);
                    } catch (IOException e) {
                        logger.error("Error broadcasting read status: " + e.getMessage());
                    }
                });
    }

    private String formatMessageToJson(ChatMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            logger.error("Error formatting message to JSON: " + e.getMessage());
            return "";
        }
    }

    @OnClose
    public void onClose(Session session) {
        String username = sessionUsernameMap.get(session);
        logger.info("[onClose] " + username);

        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        ChatMessage leaveMessage = new ChatMessage("System", username + " disconnected", ChatMessage.MessageType.LEAVE);
        broadcast(leaveMessage);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String username = sessionUsernameMap.get(session);
        logger.error("[onError] " + username + ": " + throwable.getMessage());
    }
}