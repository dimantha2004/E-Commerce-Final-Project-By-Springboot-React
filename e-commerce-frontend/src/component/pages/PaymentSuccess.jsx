import { useEffect } from 'react';
import { useCart } from '../context/CartContext';
import { useNavigate } from 'react-router-dom';
import ApiService from '../../service/ApiService';
import '../../style/PaymentSuccess.css';

const PaymentSuccess = () => {
    const { cart, dispatch } = useCart();
    const navigate = useNavigate();
    
    useEffect(() => {
        const saveOrder = async () => {
            try {
                if (cart && cart.length > 0) {
                    const orderDetails = cart.map(item => ({
                        productId: item.id,
                        quantity: item.quantity,
                        price: item.price,
                    }));
                    
                    console.log("Sending order:", orderDetails);
                    
                    const response = await ApiService.createOrder({ items: orderDetails });
                    console.log("Order saved successfully", response);
                    
                    
                    dispatch({ type: 'CLEAR_CART' });
                    
                    
                    const timer = setTimeout(() => {
                        navigate('/');
                    }, 5000);
                    return () => clearTimeout(timer);
                } else {
                    console.error("Cart is empty, nothing to order");
                }
            } catch (error) {
                console.error("Failed to save order:", error);
            }
        };
        saveOrder();
    }, [cart, dispatch, navigate]);

    return (
        <div className="payment-success-container">
            <div className="success-card">
                <div className="success-icon">
                    <svg viewBox="0 0 24 24" className="checkmark">
                        <circle className="checkmark-circle" cx="12" cy="12" r="11" />
                        <path className="checkmark-check" d="M7 13l3 3 7-7" />
                    </svg>
                </div>
                <h1>Payment Successful!</h1>
                <p className="order-message">Your order has been placed successfully</p>
                <div className="order-details">
                    <p>You will receive an email confirmation shortly.</p>
                    <p className="redirect-message">Redirecting to homepage in <span className="countdown">5</span> seconds...</p>
                </div>
                <button className="continue-shopping" onClick={() => navigate('/')}>
                    Continue Shopping
                </button>
            </div>
        </div>
    );
};

export default PaymentSuccess;

