package mk12;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import mk12.service.FriendRequestService;
import mk12.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
@ServerEndpoint("/ws/NewReq/username/{username}")
public class FriendRequestServer {

    private static final Logger LOGGER = Logger.getLogger(FriendRequestServer.class.getName());
    private static final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private static FriendRequestService friendRequestService;
    private static FriendService friendService;

    @Autowired
    public void setFriendRequestService(FriendRequestService friendRequestService) {
        FriendRequestServer.friendRequestService = friendRequestService;
    }

    @Autowired
    public void setFriendService(FriendService friendService) {
        FriendRequestServer.friendService = friendService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        LOGGER.info("User " + username + " is online");
    }

    @OnError
    public void onError(Throwable throwable, Session session) {
        LOGGER.severe("Error occurred for session " + session.getId() + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        String username = sessions.keySet().stream()
                .filter(key -> sessions.get(key) == session)
                .findFirst().orElse(null);
        if (username != null) {
            sessions.remove(username);
            LOGGER.info("User " + username + " has gone offline");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // message = "sender,receiver,action"
        String[] t = message.split(",");

        if (t.length != 3) {
            LOGGER.severe("Invalid message format: " + message);
            return;
        }

        String senderUsername = t[0];
        String receiverUsername = t[1];
        String action = t[2];

        if ("accept".equalsIgnoreCase(action)) {
            // Accept the friend request and store in the friends table
            friendService.acceptFriendRequest(senderUsername, receiverUsername);
        } else if ("deny".equalsIgnoreCase(action)) {
            // Deny the friend request
            friendRequestService.denyFriendRequest(senderUsername, receiverUsername);
        } else if ("pending".equalsIgnoreCase(action)) {
            // Mark the friend request as pending
            friendRequestService.markFriendRequestPending(senderUsername, receiverUsername);
        } else {
            // Handle other actions (e.g., send friend request)
            friendRequestService.sendFriendRequest(senderUsername, receiverUsername);
        }

        // Notify the receiver if they are online
        sendRequestToFriend(senderUsername, receiverUsername);
    }

    private void sendRequestToFriend(String fromUsername, String toUsername) {
        Session session = sessions.get(toUsername);

        if (session != null) {
            try {
                session.getBasicRemote().sendText("Friend request received from " + fromUsername);
                LOGGER.info("Request sent to " + toUsername);
            } catch (IOException e) {
                LOGGER.severe("Error sending request to " + toUsername + ": " + e.getMessage());
            }
        } else {
            LOGGER.info("User " + toUsername + " is offline");
        }
    }
}