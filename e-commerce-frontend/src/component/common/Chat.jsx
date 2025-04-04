import React, { useState, useEffect } from 'react';
import '../../style/chat.css';
import ApiService from '../../service/ApiService';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClock, faEnvelope, faPhone } from '@fortawesome/free-solid-svg-icons';
import { faWhatsapp } from '@fortawesome/free-brands-svg-icons';

const Chat = () => {
    const [message, setMessage] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const [isTyping, setIsTyping] = useState(false);
    const [quickReplies, setQuickReplies] = useState([
        "Where's my order...?",
        "I need help with returns",
        "Product quality issue",
        "Payment problem"
    ]);
    const [isAdmin] = useState(ApiService.isAdmin());
    const [businessHours, setBusinessHours] = useState({
        open: 8,
        close: 12,
        days: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri']
    });

    const whatsappNumber = '94714148950'; 

    useEffect(() => {
        
        const now = new Date();
        const currentHour = now.getHours();
        const currentDay = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][now.getDay()];
        
        setIsOpen(
            businessHours.days.includes(currentDay) && 
            currentHour >= businessHours.open && 
            currentHour < businessHours.close
        );
    }, []);

    const handleSendMessage = () => {
        if (message.trim() === '') return;

        setIsTyping(true);
        
        setTimeout(() => {
            const encodedMessage = encodeURIComponent(message);
            const whatsappUrl = `https://wa.me/${whatsappNumber}?text=${encodedMessage}`;
            window.open(whatsappUrl, '_blank');
            setMessage('');
            setIsTyping(false);
        }, 1000);
    };

    const logMessageToBackend = async (msg) => {
        try {
            await ApiService.logWhatsAppMessage({
                userId: ApiService.getCurrentUser()?.id,
                message: msg,
                timestamp: new Date().toISOString()
            });
        } catch (error) {
            console.error("Error logging message:", error);
        }
    };

    const handleQuickReply = (reply) => {
        setMessage(reply);
    };

    return (
        <div className={`chat-container ${isOpen ? 'open' : 'closed'}`}>
            <div className="chat-header">
                <h1>
                    <FontAwesomeIcon icon={faWhatsapp} className="whatsapp-icon" />
                    {isAdmin ? 'Admin Support' : 'WhatsApp Support'}
                </h1>
                <div className={`availability-status ${isOpen ? 'online' : 'offline'}`}>
                    {isOpen ? 'Online now' : 'Currently offline'}
                </div>
            </div>

            <div className="chat-content">
                <div className="chat-info-card">
                    <p>For instant support, contact us via WhatsApp:</p>
                    <div className="contact-details">
                        <div className="contact-method">
                            <FontAwesomeIcon icon={faWhatsapp} />
                            <span>+{whatsappNumber}</span>
                        </div>
                        <div className="contact-method">
                            <FontAwesomeIcon icon={faEnvelope} />
                            <span>DimazShop@gmail.com</span>
                        </div>
                        <div className="contact-method">
                            <FontAwesomeIcon icon={faPhone} />
                            <span>+94 (666) 123456789</span>
                        </div>
                    </div>
                </div>

                {!isOpen && (
                    <div className="offline-message">
                        <p>Our support team is currently offline. You can still send a message and we'll respond when we're back.</p>
                        <div className="business-hours">
                            <FontAwesomeIcon icon={faClock} />
                            <span>Hours: {businessHours.open}AM - {businessHours.close}PM ({businessHours.days.join(', ')})</span>
                        </div>
                    </div>
                )}

                <div className="quick-replies">
                    <p>Quick questions:</p>
                    <div className="quick-reply-buttons">
                        {quickReplies.map((reply, index) => (
                            <button 
                                key={index} 
                                className="quick-reply"
                                onClick={() => handleQuickReply(reply)}
                            >
                                {reply}
                            </button>
                        ))}
                    </div>
                </div>

                <div className="chat-input-container">
                    <input
                        type="text"
                        placeholder="Type your message..."
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                        disabled={isTyping}
                    />
                    <button 
                        onClick={handleSendMessage}
                        disabled={isTyping || message.trim() === ''}
                    >
                        {isTyping ? 'Sending...' : (
                            <>
                                <FontAwesomeIcon icon={faWhatsapp} />
                                Send via WhatsApp
                            </>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Chat;