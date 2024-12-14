package mk12.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a chat message in the system.
 * Contains details such as message ID, sender, content, recipient, timestamp, read status, and message type.
 */
public class ChatMessage {
    // Getters
    @Getter
    @JsonProperty("messageId")
    private String messageId;

    @Getter
    @JsonProperty("sender")
    private String sender;

    @JsonProperty("content")
    private String content;

    @JsonProperty("recipient")
    private String recipient;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("readBy")
    private Set<String> readBy;

    @JsonProperty("type")
    private MessageType type;

    /**
     * Enum representing the type of the message.
     */
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        PRIVATE,
        READ_RECEIPT  // Added READ_RECEIPT type
    }

    // Default constructor
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
        this.readBy = new HashSet<>();
        this.messageId = java.util.UUID.randomUUID().toString();
        this.type = MessageType.CHAT;
    }

    // Constructor for regular and private messages
    public ChatMessage(String sender, String content, String recipient) {
        this();
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
        this.type = (recipient != null) ? MessageType.PRIVATE : MessageType.CHAT;
    }

    // Constructor for system messages
    public ChatMessage(String sender, String content, MessageType type) {
        this();
        this.sender = sender;
        this.content = content;
        this.recipient = null;
        this.type = type;
    }

    // Constructor for read receipts
    public static ChatMessage createReadReceipt(String messageId, String readByUsername) {
        ChatMessage readReceipt = new ChatMessage();
        readReceipt.setType(MessageType.READ_RECEIPT);
        readReceipt.setMessageId(messageId);
        readReceipt.getReadBy().add(readByUsername);
        return readReceipt;
    }

    public String getContent() {
        return content;
    }

    public String getRecipient() {
        return recipient;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Set<String> getReadBy() {
        return new HashSet<>(readBy);
    }

    public MessageType getType() {
        return type;
    }

    // Setters
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setReadBy(Set<String> readBy) {
        this.readBy = new HashSet<>(readBy);
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    // Utility methods
    public void markAsReadBy(String username) {
        if (username != null && !username.isEmpty()) {
            this.readBy.add(username);
        }
    }

    public boolean isReadBy(String username) {
        return username != null && this.readBy.contains(username);
    }

    public int getReadCount() {
        return this.readBy.size();
    }

    public boolean isPrivate() {
        return this.type == MessageType.PRIVATE;
    }

    public boolean isSystemMessage() {
        return this.type == MessageType.JOIN || this.type == MessageType.LEAVE;
    }

    public boolean isReadReceipt() {
        return this.type == MessageType.READ_RECEIPT;
    }

    @Override
    public String toString() {
        return String.format("ChatMessage{messageId='%s', type=%s, sender='%s', recipient='%s', content='%s', timestamp=%s, readBy=%d users}",
                messageId, type, sender, recipient, content, timestamp, readBy.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return messageId.equals(that.messageId);
    }

    @Override
    public int hashCode() {
        return messageId.hashCode();
    }
}