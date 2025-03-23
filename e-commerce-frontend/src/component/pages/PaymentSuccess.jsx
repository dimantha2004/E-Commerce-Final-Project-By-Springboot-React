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
                // Prepare the order details
                const orderDetails = cart.map(item => ({
                    productId: item.id,
                    quantity: item.quantity,
                    price: item.price,
                }));

                // Send the order details to the backend to save in the database
                await ApiService.saveOrder({ items: orderDetails });

                // Clear the cart after the order is saved
                dispatch({ type: 'CLEAR_CART' });

                // Redirect to the homepage after a short delay
                const timer = setTimeout(() => {
                    navigate('/');
                }, 1000);

                return () => clearTimeout(timer);
            } catch (error) {
                console.error("Failed to save order:", error);
                // Handle the error as needed
            }
        };

        saveOrder();
    }, [cart, dispatch, navigate]);

    return (
        <div className="checkout-success">
            <h1>Payment Successful...! 🎉</h1>
            <p>Thank you for your purchase. You'll be redirected to the homepage shortly.</p>
        </div>
    );
};

export default PaymentSuccess;