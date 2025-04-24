import React from "react";
import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";
import '../../style/productList.css';

const ProductList = ({ products }) => {
    const { cart, dispatch } = useCart();

    const addToCart = (product) => {
        dispatch({ type: 'ADD_ITEM', payload: product });
    }

    const incrementItem = (product) => {
       
        if (product.quantity > 0) {
            dispatch({ type: 'INCREMENT_ITEM', payload: product });
        }
    }

    const decrementItem = (product) => {
        const cartItem = cart.find(item => item.id === product.id);
        if (cartItem && cartItem.quantity > 1) {
            dispatch({ type: 'DECREMENT_ITEM', payload: product });
        } else {
            dispatch({ type: 'REMOVE_ITEM', payload: product });
        }
    }

    return (
        <div className="product-list">
            {products.map((product, index) => {
                const cartItem = cart.find(item => item.id === product.id);
                const isOutOfStock = product.quantity <= 0;
                
                return (
                    <div className="product-item" key={index}>
                        <Link to={`/product/${product.id}`}>
                            <img src={product.imageUrl} alt={product.name} className="product-image" />
                            <h3>{product.name}</h3>
                            <p>{product.description}</p>
                            <span>RS :{product.price.toFixed(2)}</span>
                            <div className="product-stock">
                                Stock: {product.quantity ? product.quantity : "Out of Stock"}
                            </div>
                        </Link>
                        {cartItem ? (
                            <div className="quantity-controls">
                                <button onClick={() => decrementItem(product)}>-</button>
                                <span>{cartItem.quantity}</span>
                                <button 
                                    onClick={() => incrementItem(product)}
                                    disabled={isOutOfStock}
                                >
                                    +
                                </button>
                            </div>
                        ) : (
                            <button 
                                onClick={() => addToCart(product)}
                                disabled={isOutOfStock}
                            >
                                {isOutOfStock ? "Out of Stock" : "Add To Cart"}
                            </button>
                        )}
                    </div>
                )
            })}
        </div>
    )
};

export default ProductList;