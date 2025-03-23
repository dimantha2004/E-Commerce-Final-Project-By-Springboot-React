import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loadStripe } from "@stripe/stripe-js";
import ApiService from "../../service/ApiService";
import { useCart } from "../context/CartContext";
import '../../style/cart.css';

const stripePromise = loadStripe(process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY);

const CartPage = () => {
    const { cart, dispatch } = useCart();
    const [message, setMessage] = useState(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const incrementItem = (product) => {
        dispatch({ type: 'INCREMENT_ITEM', payload: product });
    };

    const decrementItem = (product) => {
        const cartItem = cart.find(item => item.id === product.id);
        if (cartItem?.quantity > 1) {
            dispatch({ type: 'DECREMENT_ITEM', payload: product });
        } else {
            dispatch({ type: 'REMOVE_ITEM', payload: product });
        }
    };

    const totalCents = cart.reduce((total, item) => {
        return total + Math.round(parseFloat(item.price) * 100) * item.quantity;
    }, 0);

    const totalDollars = (totalCents / 100).toFixed(2);

    const handleCheckout = async () => {
        if (!ApiService.isAuthenticated()) {
            setMessage("You need to login first before you can place an order");
            setTimeout(() => navigate("/login"), 3000);
            return;
        }

        if (totalCents < 50) {
            setMessage("Minimum order amount is $0.50");
            return;
        }

        setLoading(true);
        try {
            
            const orderItems = cart.map(item => ({
                productId: item.id,
                quantity: item.quantity
            }));

            const orderRequest = {
                totalPrice: totalDollars,
                items: orderItems,
            };

            const orderResponse = await ApiService.createOrder(orderRequest);
            setMessage(orderResponse.message);

            dispatch({ type: 'CLEAR_CART' });

            const lineItems = cart.map(item => ({
                productId: String(item.id),
                price: Math.round(parseFloat(item.price) * 100),
                quantity: item.quantity
            }));

            const checkoutResponse = await ApiService.createCheckoutSession({
                items: lineItems,
                successUrl: `${window.location.origin}/checkout/success`,
                cancelUrl: `${window.location.origin}/cart`
            });

            const stripe = await stripePromise;
            const { error } = await stripe.redirectToCheckout({
                sessionId: checkoutResponse.sessionId
            });

            if (error) throw error;

        } catch (error) {
            console.error("Checkout error:", error);
            setMessage(error.response?.data?.message || error.message || 'Failed to place order');
            setTimeout(() => setMessage(''), 1000);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="cart-page">
            <h1>Shopping Cart</h1>
            {message && <div className="alert">{message}</div>}

            {cart.length === 0 ? (
                <p>Your cart is empty</p>
            ) : (
                <div className="cart-content">
                    <ul className="cart-items">
                        {cart.map(item => (
                            <li key={item.id} className="cart-item">
                                <img 
                                    src={item.imageUrl} 
                                    alt={item.name} 
                                    className="product-image"
                                />
                                <div className="item-details">
                                    <h3>{item.name}</h3>
                                    <p className="description">{item.description}</p>
                                    <div className="quantity-controls">
                                        <button 
                                            onClick={() => decrementItem(item)}
                                            aria-label="Decrease quantity"
                                        >
                                            −
                                        </button>
                                        <span>{item.quantity}</span>
                                        <button 
                                            onClick={() => incrementItem(item)}
                                            aria-label="Increase quantity"
                                        >
                                            +
                                        </button>
                                    </div>
                                    <p className="price">
                                        ${(item.price * item.quantity).toFixed(2)}
                                    </p>
                                </div>
                            </li>
                        ))}
                    </ul>
                    
                    <div className="checkout-section">
                        <h2>Total: ${totalDollars}</h2>
                        <button 
                            onClick={handleCheckout}
                            disabled={loading || totalCents < 50}
                            className="checkout-button"
                        >
                            {loading ? 'Processing...' : 'Proceed to Checkout'}
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CartPage;