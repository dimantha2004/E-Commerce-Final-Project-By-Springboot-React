import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import '../../style/adminOrderDetails.css';
import ApiService from "../../service/ApiService";

const OrderStatus = ["PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED", "RETURNED"];

const AdminOrderDetailsPage = () => {
    const { itemId } = useParams();
    const [orderItems, setOrderItems] = useState([]);
    const [message, setMessage] = useState('');
    const [selectedStatus, setSelectedStatus] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchOrderDetails(itemId);
    }, [itemId]);

    const fetchOrderDetails = async (itemId) => {
        setLoading(true);
        try {
            const response = await ApiService.getOrderItemById(itemId);
            if (response.error) {
                setError(response.message || "Failed to fetch order details");
                setOrderItems([]);
            } else {
                setOrderItems(response.orderItemList || []);
                
                const statusObj = {};
                if (response.orderItemList && response.orderItemList.length > 0) {
                    response.orderItemList.forEach(item => {
                        statusObj[item.id] = item.status;
                    });
                    setSelectedStatus(statusObj);
                }
                setError('');
            }
        } catch (error) {
            setError("An unexpected error occurred. Please try again later.");
            console.error("Error fetching order details:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleStatusChange = (orderItemId, newStatus) => {
        setSelectedStatus({ ...selectedStatus, [orderItemId]: newStatus });
    };

    const handleSubmitStatusChange = async (orderItemId) => {
        try {
            
            const response = await ApiService.updateOrderItemsByStatus(orderItemId, selectedStatus[orderItemId]);
            
            if (response.error) {
                setMessage(response.message || "Failed to update status");
            } else {
                
                const currentOrderItem = orderItems.find(item => item.id === orderItemId);
                
                if (currentOrderItem && currentOrderItem.user) {
                    
                    const emailResponse = await ApiService.sendStatusUpdateEmail(
                        orderItemId,
                        selectedStatus[orderItemId],
                        currentOrderItem.user,
                        currentOrderItem.product
                    );
                    
                    if (emailResponse.error) {
                        setMessage('Order status updated, but failed to send notification email');
                        console.error("Error sending email:", emailResponse.message);
                    } else {
                        setMessage('Order status updated and notification email sent');
                    }
                } else {
                    setMessage('Order status updated, but email notification not sent (missing user data)');
                }
                
                
                fetchOrderDetails(itemId);
            }
            
            
            setTimeout(() => {
                setMessage('');
            }, 3000);
        } catch (error) {
            setMessage("An unexpected error occurred. Please try again later.");
            console.error("Error updating order status:", error);
        }
    };

    if (loading) {
        return <div className="order-details-page"><p>Loading order details...</p></div>;
    }

    if (error) {
        return (
            <div className="order-details-page">
                <div className="error-message">{error}</div>
                <button onClick={() => fetchOrderDetails(itemId)}>Retry</button>
            </div>
        );
    }

    return (
        <div className="order-details-page">
            {message && <div className="message">{message}</div>}
            <h2>Order Details</h2>
            {orderItems.length ? (
                orderItems.map((orderItem) => (
                    <div key={orderItem.id} className="order-item-details">
                        <div className="info">
                            <h3>Order Information</h3>
                            <p><strong>Order Item ID: </strong>{orderItem.id}</p>
                            <p><strong>Quantity: </strong>{orderItem.quantity}</p>
                            <p><strong>Total Price: </strong>${orderItem.price ? orderItem.price.toFixed(2) : '0.00'}</p>
                            <p><strong>Order Status: </strong>{orderItem.status}</p>
                            <p><strong>Date Ordered: </strong>{orderItem.createdAt ? new Date(orderItem.createdAt).toLocaleDateString() : 'N/A'}</p>
                        </div>
                        <div className="info">
                            <h3>User Information</h3>
                            {orderItem.user ? (
                                <>
                                    <p><strong>Name: </strong>{orderItem.user.name}</p>
                                    <p><strong>Email: </strong>{orderItem.user.email}</p>
                                    <p><strong>Phone: </strong>{orderItem.user.phoneNumber || 'N/A'}</p>
                                    <p><strong>Role: </strong>{orderItem.user.role}</p>

                                    <div className="info">
                                        <h3>Delivery Address</h3>
                                        {orderItem.user.address ? (
                                            <>
                                                <p><strong>Country: </strong>{orderItem.user.address.country}</p>
                                                <p><strong>State: </strong>{orderItem.user.address.state}</p>
                                                <p><strong>City: </strong>{orderItem.user.address.city}</p>
                                                <p><strong>Street: </strong>{orderItem.user.address.street}</p>
                                                <p><strong>Zip Code: </strong>{orderItem.user.address.zipcode}</p>
                                            </>
                                        ) : (
                                            <p>No address information available</p>
                                        )}
                                    </div>
                                </>
                            ) : (
                                <p>No user information available</p>
                            )}
                        </div>
                        <div>
                            <h3>Product Information</h3>
                            {orderItem.product ? (
                                <>
                                    {orderItem.product.imageUrl && (
                                        <img src={orderItem.product.imageUrl} alt={orderItem.product.name} />
                                    )}
                                    <p><strong>Name: </strong>{orderItem.product.name}</p>
                                    <p><strong>Description: </strong>{orderItem.product.description}</p>
                                    <p><strong>Price: </strong>${orderItem.product.price ? orderItem.product.price.toFixed(2) : '0.00'}</p>
                                </>
                            ) : (
                                <p>No product information available</p>
                            )}
                        </div>
                        <div className="status-change">
                            <h4>Change Status</h4>
                            <select
                                className="status-option"
                                value={selectedStatus[orderItem.id] || orderItem.status}
                                onChange={(e) => handleStatusChange(orderItem.id, e.target.value)}>

                                {OrderStatus.map(status => (
                                    <option key={status} value={status}>{status}</option>
                                ))}
                            </select>
                            <button className="update-status-button" onClick={() => handleSubmitStatusChange(orderItem.id)}>Update Status</button>
                        </div>
                    </div>
                ))
            ) : (
                <p>No order items found.</p>
            )}
        </div>
    );
};

export default AdminOrderDetailsPage;