import React, { useState, useEffect } from 'react';
import '../../style/chat.css';
import ApiService from '../../service/ApiService';

const Chat = () => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [isAdmin] = useState(ApiService.isAdmin());

    useEffect(() => {
        
        const initialMessages = [
            { id: 1, sender: 'support', text: 'Hello! How can we help you today?', timestamp: new Date() }
        ];
        setMessages(initialMessages);
    }, []);

    const handleSendMessage = () => {
        if (newMessage.trim() === '') return;

        const userMessage = {
            id: messages.length + 1,
            sender: isAdmin ? 'admin' : 'user',
            text: newMessage,
            timestamp: new Date()
        };

        setMessages([...messages, userMessage]);
        setNewMessage('');

        
        
        setTimeout(() => {
            const responseMessage = {
                id: messages.length + 2,
                sender: 'support',
                text: 'Thanks for your message! Our team will get back to you soon.',
                timestamp: new Date()
            };
            setMessages(prev => [...prev, responseMessage]);
        }, 1000);
    };

    return (
        <div className="chat-container">
            <h1>{isAdmin ? 'Admin Support Chat' : 'Customer Support Chat'}</h1>
            <div className="chat-messages">
                {messages.map((message) => (
                    <div key={message.id} className={`message ${message.sender}`}>
                        <div className="message-content">
                            <p>{message.text}</p>
                            <span className="timestamp">
                                {new Date(message.timestamp).toLocaleTimeString()}
                            </span>
                        </div>
                    </div>
                ))}
            </div>
            <div className="chat-input">
                <input
                    type="text"
                    placeholder="Type your message..."
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                />
                <button onClick={handleSendMessage}>Send</button>
            </div>
        </div>
    );
};

export default Chat;