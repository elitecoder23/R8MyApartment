// WebSocket connection
let ws;

// DOM Elements
const connectBtn = document.getElementById('connectBtn');
const sendBtn = document.getElementById('sendBtn');
const msgInput = document.getElementById('msg');
const statusDiv = document.getElementById('status');
const logDiv = document.getElementById('log');

// this is ui based on connection status
function updateConnectionStatus(connected) {
    statusDiv.className = `status ${connected ? 'connected' : 'disconnected'}`;
    statusDiv.textContent = connected ? 'Connected' : 'Disconnected';
    msgInput.disabled = !connected;
    sendBtn.disabled = !connected;
    connectBtn.textContent = connected ? 'Disconnect' : 'Connect';
}

// formatting it
function formatMessage(message) {
    if (message.startsWith('User:') || message.includes('disconnected') || message.includes('Welcome')) {
        return `<div class="message system">${message}</div>`;
    } else if (message.includes('[DM from')) {
        return `<div class="message dm">${message}</div>`;
    }
    return `<div class="message">${message}</div>`;
}

// Connect/Disconnect function
function connect() {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.close();
        return;
    }

    const username = document.getElementById("username").value.trim();
    if (!username) {
        alert("Please enter a username");
        return;
    }

    const wsserver = document.getElementById("wsserver").value.trim();
    if (!wsserver.includes('/chat/1/') && !wsserver.includes('/chat/2/')) {
        alert("Please use either /chat/1/ or /chat/2/ in the server URL");
        return;
    }

    const url = wsserver + username;

    try {
        ws = new WebSocket(url);

        ws.onmessage = function(event) {
            console.log("Received:", event.data);
            logDiv.innerHTML += formatMessage(event.data);
            logDiv.scrollTop = logDiv.scrollHeight;
        };

        ws.onopen = function(event) {
            console.log("Connected to:", event.currentTarget.url);
            updateConnectionStatus(true);
            clearLog();
        };

        ws.onclose = function(event) {
            console.log("Disconnected");
            updateConnectionStatus(false);
        };

        ws.onerror = function(event) {
            console.error("WebSocket error:", event);
            updateConnectionStatus(false);
            logDiv.innerHTML += formatMessage("Error: Unable to connect to server");
        };

    } catch (error) {
        console.error("Connection error:", error);
        updateConnectionStatus(false);
        logDiv.innerHTML += formatMessage(`Error: ${error.message}`);
    }
}

// Clear chat log
function clearLog() {
    logDiv.innerHTML = '';
}

// Send message function
function send() {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        alert("Please connect first!");
        return;
    }

    const content = msgInput.value.trim();
    if (content) {
        ws.send(content);
        msgInput.value = "";
    }
}

// Handle Enter key in message input
function handleMessageKeyPress(event) {
    if (event.key === "Enter" && !event.shiftKey) {
        event.preventDefault();
        send();
    }
}

// Handle Enter key in username input
function handleUsernameKeyPress(event) {
    if (event.key === "Enter") {
        connect();
    }
}

// Initialize event listeners
document.addEventListener("DOMContentLoaded", function() {
    msgInput.addEventListener("keypress", handleMessageKeyPress);
    document.getElementById("username").addEventListener("keypress", handleUsernameKeyPress);

    // Auto-focus username field
    document.getElementById("username").focus();
});

// Helper function to format timestamps
function getTimestamp() {
    const now = new Date();
    return now.toLocaleTimeString();
}