// frontend/src/helper/websocket.js
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

let stompClient = null;
let connectedCallback = null;
let errorCallback = null;
let subscriptions = {};

// Get user from localStorage
const getUser = () => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
        return JSON.parse(userStr);
    }
    return null;
};

// Connect to WebSocket
export const connect = (onConnected, onError) => {
    connectedCallback = onConnected;
    errorCallback = onError;

    const socket = new SockJS('http://localhost:8080/ws');
    stompClient = Stomp.over(socket);

    // Disable debug logs
    stompClient.debug = null;

    stompClient.connect({}, onConnectSuccess, onConnectError);
};

// Disconnect from WebSocket
export const disconnect = () => {
    if (stompClient) {
        // Unsubscribe from all topics
        Object.values(subscriptions).forEach(subscription => {
            if (subscription) {
                subscription.unsubscribe();
            }
        });

        // Reset subscriptions
        subscriptions = {};

        // Disconnect client
        stompClient.disconnect();
        console.log('WebSocket Disconnected');
    }
};

// Success callback when connected
const onConnectSuccess = () => {
    console.log('WebSocket Connected');

    // Subscribe to public channel
    subscribeToPublic();

    // Subscribe to user-specific channel if user is logged in
    const user = getUser();
    if (user) {
        subscribeToUser(user.id);

        // Subscribe to relevant topics based on user role
        if (user.role === 'TENANT') {
            subscribeToSpaces();
        } else if (user.role === 'OWNER') {
            subscribeToContracts();
        } else if (user.role === 'ADMIN') {
            subscribeToSpaces();
            subscribeToContracts();
        }
    }

    // Notify connection success
    if (connectedCallback) {
        connectedCallback();
    }
};

// Error callback when connection fails
const onConnectError = (error) => {
    console.error('WebSocket Connection Error:', error);

    // Notify connection error
    if (errorCallback) {
        errorCallback(error);
    }
};

// Subscribe to public topic
const subscribeToPublic = () => {
    if (stompClient && stompClient.connected) {
        subscriptions.public = stompClient.subscribe('/topic/public', onMessageReceived);
    }
};

// Subscribe to user-specific queue
const subscribeToUser = (userId) => {
    if (stompClient && stompClient.connected && userId) {
        subscriptions.user = stompClient.subscribe(`/queue/user.${userId}`, onMessageReceived);
    }
};

// Subscribe to spaces topic
const subscribeToSpaces = () => {
    if (stompClient && stompClient.connected) {
        subscriptions.spaces = stompClient.subscribe('/topic/spaces', onMessageReceived);
    }
};

// Subscribe to contracts topic
const subscribeToContracts = () => {
    if (stompClient && stompClient.connected) {
        subscriptions.contracts = stompClient.subscribe('/topic/contracts', onMessageReceived);
    }
};

// Handle received messages
const onMessageReceived = (payload) => {
    try {
        const notification = JSON.parse(payload.body);
        console.log('Received notification:', notification);

        // Dispatch notification event
        const event = new CustomEvent('notification', { detail: notification });
        window.dispatchEvent(event);

        // Show browser notification if supported
        showBrowserNotification(notification);
    } catch (error) {
        console.error('Error handling message:', error);
    }
};

// Show browser notification
const showBrowserNotification = (notification) => {
    if ('Notification' in window && Notification.permission === 'granted') {
        new Notification(notification.type, {
            body: notification.message,
            icon: '/logo192.png'
        });
    } else if ('Notification' in window && Notification.permission !== 'denied') {
        Notification.requestPermission().then(permission => {
            if (permission === 'granted') {
                new Notification(notification.type, {
                    body: notification.message,
                    icon: '/logo192.png'
                });
            }
        });
    }
};

// Send a message
export const sendMessage = (destination, message) => {
    if (stompClient && stompClient.connected) {
        stompClient.send(destination, {}, JSON.stringify(message));
    } else {
        console.error('Cannot send message: WebSocket not connected');
    }
};

// Request notification permission
export const requestNotificationPermission = () => {
    if ('Notification' in window && Notification.permission !== 'granted') {
        Notification.requestPermission();
    }
};

// frontend/src/components/NotificationCenter.js
import React, { useState, useEffect } from 'react';
import { connect, disconnect, requestNotificationPermission } from '../helper/websocket';
import './NotificationCenter.css';

function NotificationCenter() {
    const [notifications, setNotifications] = useState([]);
    const [connected, setConnected] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        // Connect to WebSocket when component mounts
        connect(
            // Connected callback
            () => {
                setConnected(true);
                setError(null);
                requestNotificationPermission();
            },
            // Error callback
            (err) => {
                setConnected(false);
                setError('Failed to connect to notification service');
                console.error(err);
            }
        );

        // Handle notifications
        const handleNotification = (event) => {
            const notification = event.detail;
            setNotifications(prev => [notification, ...prev].slice(0, 10)); // Keep last 10 notifications
        };

        // Add event listener
        window.addEventListener('notification', handleNotification);

        // Cleanup when component unmounts
        return () => {
            window.removeEventListener('notification', handleNotification);
            disconnect();
        };
    }, []);

    // Clear notifications
    const clearNotifications = () => {
        setNotifications([]);
    };

    // Dismiss a single notification
    const dismissNotification = (id) => {
        setNotifications(prev => prev.filter(notification => notification.id !== id));
    };

    return (
        <div className="notification-center">
            <div className="notification-header">
                <h3>Notifications {connected ?
                    <span className="status-indicator connected">●</span> :
                    <span className="status-indicator disconnected">●</span>}
                </h3>
                {notifications.length > 0 && (
                    <button className="clear-btn" onClick={clearNotifications}>Clear All</button>
                )}
            </div>

            {error && (
                <div className="notification-error">
                    {error}
                </div>
            )}

            <div className="notification-list">
                {notifications.length === 0 ? (
                    <div className="no-notifications">No notifications</div>
                ) : (
                    notifications.map(notification => (
                        <div key={notification.id} className={`notification-item ${notification.type.toLowerCase()}`}>
                            <div className="notification-content">
                                <div className="notification-type">{notification.type}</div>
                                <div className="notification-message">{notification.message}</div>
                                <div className="notification-time">
                                    {new Date(notification.timestamp).toLocaleString()}
                                </div>
                            </div>
                            <button
                                className="dismiss-btn"
                                onClick={() => dismissNotification(notification.id)}
                            >
                                ×
                            </button>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default NotificationCenter;

// frontend/src/components/NotificationCenter.css
.notification-center {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 350px;
    overflow: hidden;
}

.notification-header {
    background-color: #f8f9fa;
    padding: 12px 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #eee;
}

.notification-header h3 {
    margin: 0;
    font-size: 16px;
    color: #2c3e50;
}

.status-indicator {
    font-size: 10px;
    margin-left: 5px;
}

.status-indicator.connected {
    color: #27ae60;
}

.status-indicator.disconnected {
    color: #e74c3c;
}

.clear-btn {
    background: none;
    border: none;
    color: #3498db;
    cursor: pointer;
    font-size: 14px;
}

.clear-btn:hover {
    text-decoration: underline;
}

.notification-error {
    background-color: #ffe6e6;
    color: #e74c3c;
    padding: 10px 15px;
    font-size: 14px;
    border-bottom: 1px solid #ffcccc;
}

.notification-list {
    max-height: 350px;
    overflow-y: auto;
}

.no-notifications {
    padding: 20px;
    text-align: center;
    color: #95a5a6;
    font-size: 14px;
}

.notification-item {
    padding: 12px 15px;
    border-bottom: 1px solid #eee;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    transition: background-color 0.2s;
}

.notification-item:hover {
    background-color: #f8f9fa;
}

.notification-content {
    flex-grow: 1;
}

.notification-type {
    font-size: 12px;
    font-weight: 600;
    color: #3498db;
    margin-bottom: 4px;
}

.notification-item.new_space .notification-type,
.notification-item.space_status_change .notification-type {
    color: #27ae60;
}

.notification-item.new_contract .notification-type,
.notification-item.contract_update .notification-type {
    color: #f39c12;
}

.notification-item.test .notification-type {
    color: #9b59b6;
}

.notification-message {
    font-size: 14px;
    color: #2c3e50;
    margin-bottom: 4px;
}

.notification-time {
    font-size: 12px;
    color: #95a5a6;
}

.dismiss-btn {
    background: none;
    border: none;
    color: #bdc3c7;
    font-size: 18px;
    cursor: pointer;
    padding: 0 0 0 10px;
}

.dismiss-btn:hover {
    color: #7f8c8d;
}