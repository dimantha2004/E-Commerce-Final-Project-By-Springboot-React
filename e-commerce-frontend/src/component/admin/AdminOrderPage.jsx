import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import '../../style/adminOrderPage.css';
import Pagination from "../common/Pagination";
import ApiService from "../../service/ApiService";

const OrderStatus = ["PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED", "RETURNED"];

const AdminOrdersPage = () => {
    const [orders, setOrders] = useState([]);
    const [filteredOrders, setFilteredOrders] = useState([]);
    const [statusFilter, setStatusFilter] = useState('');
    const [searchStatus, setSearchStatus] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const itemsPerPage = 10;

    const navigate = useNavigate();

    useEffect(() => {
        fetchOrders();
    }, [searchStatus, currentPage]);

    const fetchOrders = async () => {
        setLoading(true);
        try {
            let response;
            if (searchStatus) {
                response = await ApiService.getAllOrderItemsByStatus(searchStatus);
            } else {
                response = await ApiService.getAllOrders();
            }
            
            if (response.error) {
                setError(response.message || "Failed to fetch orders");
                setOrders([]);
                setFilteredOrders([]);
                setTotalPages(0);
            } else {
                const orderList = response.orderItemList || [];
                setOrders(orderList);
                
                // Apply pagination
                const startIndex = (currentPage - 1) * itemsPerPage;
                const endIndex = startIndex + itemsPerPage;
                setFilteredOrders(orderList.slice(startIndex, endIndex));
                
                // Calculate total pages
                setTotalPages(Math.ceil(orderList.length / itemsPerPage) || 1);
                setError(null);
            }
        } catch (error) {
            setError("An unexpected error occurred. Please try again later.");
            console.error("Error fetching orders:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleFilterChange = (e) => {
        const filterValue = e.target.value;
        setStatusFilter(filterValue);
        setCurrentPage(1);

        if (filterValue) {
            const filtered = orders.filter(order => order.status === filterValue);
            const startIndex = 0; // Reset to first page
            const endIndex = startIndex + itemsPerPage;
            setFilteredOrders(filtered.slice(startIndex, endIndex));
            setTotalPages(Math.ceil(filtered.length / itemsPerPage) || 1);
        } else {
            const startIndex = 0; // Reset to first page
            const endIndex = startIndex + itemsPerPage;
            setFilteredOrders(orders.slice(startIndex, endIndex));
            setTotalPages(Math.ceil(orders.length / itemsPerPage) || 1);
        }
    };

    const handleSearchStatusChange = (e) => {
        setSearchStatus(e.target.value);
        setCurrentPage(1);
    };

    const handleOrderDetails = (id) => {
        navigate(`/admin/order-details/${id}`);
    };

    const handlePageChange = (page) => {
        setCurrentPage(page);
        
        // Update filtered orders based on current filter and page
        const startIndex = (page - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        
        let dataToSlice = orders;
        if (statusFilter) {
            dataToSlice = orders.filter(order => order.status === statusFilter);
        }
        
        setFilteredOrders(dataToSlice.slice(startIndex, endIndex));
    };

    const handleRetry = () => {
        fetchOrders();
    };

    if (loading && orders.length === 0) {
        return <div className="admin-orders-page"><p>Loading orders...</p></div>;
    }

    return (
        <div className="admin-orders-page">
            <h2>Orders</h2>
            
            {error && (
                <div className="error-container">
                    <p className="error-message">{error}</p>
                    <button onClick={handleRetry}>Retry</button>
                </div>
            )}
            
            <div className="filter-container">
                <div className="statusFilter">
                    <label>Filter By Status</label>
                    <select value={statusFilter} onChange={handleFilterChange}>
                        <option value="">All</option>
                        {OrderStatus.map(status => (
                            <option key={status} value={status}>{status}</option>
                        ))}
                    </select>
                </div>
                <div className="searchStatus">
                    <label>Search By Status</label>
                    <select value={searchStatus} onChange={handleSearchStatusChange}>
                        <option value="">All</option>
                        {OrderStatus.map(status => (
                            <option key={status} value={status}>{status}</option>
                        ))}
                    </select>
                </div>
            </div>

            {filteredOrders.length > 0 ? (
                <table className="orders-table">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Customer</th>
                            <th>Status</th>
                            <th>Price</th>
                            <th>Date Ordered</th>
                            <th>Actions</th>
                        </tr>
                    </thead>

                    <tbody>
                        {filteredOrders.map(order => (
                            <tr key={order.id}>
                                <td>{order.id}</td>
                                <td>{order.user ? order.user.name : 'N/A'}</td>
                                <td>{order.status}</td>
                                <td>${order.price ? order.price.toFixed(2) : '0.00'}</td>
                                <td>{order.createdAt ? new Date(order.createdAt).toLocaleDateString() : 'N/A'}</td>
                                <td>
                                    <button onClick={() => handleOrderDetails(order.id)}>Details</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p className="no-orders">No orders found</p>
            )}

            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
            />
        </div>
    );
};

export default AdminOrdersPage;