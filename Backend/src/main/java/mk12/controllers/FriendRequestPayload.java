package mk12.controllers;

/**
 * Payload class for friend request data.
 * Contains the sender's and receiver's usernames.
 */
public class FriendRequestPayload {
    private String senderUsername;
    private String receiverUsername;

    /**
     * Gets the sender's username.
     *
     * @return the sender's username
     */
    public String getSenderUsername() {
        return senderUsername;
    }

    /**
     * Sets the sender's username.
     *
     * @param senderUsername the sender's username
     */
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    /**
     * Gets the receiver's username.
     *
     * @return the receiver's username
     */
    public String getReceiverUsername() {
        return receiverUsername;
    }

    /**
     * Sets the receiver's username.
     *
     * @param receiverUsername the receiver's username
     */
    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }
}