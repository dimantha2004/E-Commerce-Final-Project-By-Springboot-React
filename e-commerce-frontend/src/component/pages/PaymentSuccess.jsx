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
                    
                    await ApiService.createOrder({ items: orderDetails });
                    console.log("Order saved successfully");
                    
                    
                    dispatch({ type: 'CLEAR_CART' });
                    
                    const timer = setTimeout(() => {
                        navigate('/');
                    }, 1000);

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
        <div className="checkout-success">
            <h1>Payment Successful...!</h1>
            <p>Thank you for your purchase. You'll be redirected to the homepage shortly.</p>
        </div>
    );
};

export default PaymentSuccess;