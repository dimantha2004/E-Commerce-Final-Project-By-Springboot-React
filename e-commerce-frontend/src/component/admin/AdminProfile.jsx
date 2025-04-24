import React, { useEffect, useState } from "react";
import ApiService from "../../service/ApiService";
import "../../style/adminprofile.css";
import BarChart from "../common/barchart"; 
import PieChart from "../common/piechart"; 

const AdminProfile = () => {
    const [orderStats, setOrderStats] = useState({
        totalOrders: 0,
        pendingOrders: 0,
        confirmedOrders: 0,
        shippedOrders: 0,
        cancelledOrders: 0
    });

    const [revenueMetrics, setRevenueMetrics] = useState({
        totalRevenue: 0,
        averageOrderValue: 0
    });

    const [productStats, setProductStats] = useState({
        totalProducts: 0,
        topSellingProducts: [],
        outOfStockProducts: 0
    });

    const [recentOrders, setRecentOrders] = useState([]);

    const barChartData = {
        labels: ["Pending", "Confirmed", "Shipped", "Cancelled"],
        datasets: [
            {
                label: "Number of Orders",
                data: [
                    orderStats.pendingOrders,
                    orderStats.confirmedOrders,
                    orderStats.shippedOrders,
                    orderStats.cancelledOrders
                ],
                backgroundColor: [
                    "rgba(255, 99, 132, 0.6)",
                    "rgba(54, 162, 235, 0.6)",
                    "rgba(75, 192, 192, 0.6)",
                    "rgba(255, 206, 86, 0.6)"
                ],
                borderColor: [
                    "rgba(255, 99, 132, 1)",
                    "rgba(54, 162, 235, 1)",
                    "rgba(75, 192, 192, 1)",
                    "rgba(255, 206, 86, 1)"
                ],
                borderWidth: 1
            }
        ]
    };

    const pieChartData = {
        labels: ["Pending", "Confirmed", "Shipped", "Cancelled"],
        datasets: [
            {
                label: "Revenue Distribution",
                data: [
                    orderStats.pendingOrders * 100, 
                    orderStats.confirmedOrders * 200,
                    orderStats.shippedOrders * 150,
                    orderStats.cancelledOrders * 50
                ],
                backgroundColor: [
                    "rgba(255, 99, 132, 0.6)",
                    "rgba(54, 162, 235, 0.6)",
                    "rgba(75, 192, 192, 0.6)",
                    "rgba(255, 206, 86, 0.6)"
                ],
                borderColor: [
                    "rgba(255, 99, 132, 1)",
                    "rgba(54, 162, 235, 1)",
                    "rgba(75, 192, 192, 1)",
                    "rgba(255, 206, 86, 1)"
                ],
                borderWidth: 1
            }
        ]
    };


    const barChartOptions = {
        responsive: true,
        plugins: {
            legend: {
                position: "top"
            },
            title: {
                display: true,
                text: "Order Status Distribution"
            },
            tooltip: {
                callbacks: {
                    label: function(context) {
                        return `${context.dataset.label}: ${context.raw}`;
                    }
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    precision: 0
                }
            }
        }
    };

    const pieChartOptions = {
        responsive: true,
        plugins: {
            legend: {
                position: "top"
            },
            title: {
                display: true,
                text: "Revenue Distribution by Status"
            },
            tooltip: {
                callbacks: {
                    label: function(context) {
                        return `${context.label}: $
                         ${context.raw.toLocaleString()}`;
                    }
                }
            }
        }
    };

 

    useEffect(() => {
        const fetchData = async () => {
            try {
               
                const ordersResponse = await ApiService.getAllOrders();
                console.log("Raw orders response:", ordersResponse);
                const orders = ordersResponse?.orderItemList || [];
                console.log("Processed orders:", orders);

                const stats = {
                    totalOrders: orders.length,
                    pendingOrders: orders.filter(order => order.status === "PENDING").length,
                    confirmedOrders: orders.filter(order => order.status === "CONFIRMED").length,
                    shippedOrders: orders.filter(order => order.status === "SHIPPED").length,
                    cancelledOrders: orders.filter(order => order.status === "CANCELLED").length
                };
                setOrderStats(stats);

                const totalRevenue = orders.reduce((sum, order) => {
                    return sum + (Number(order.price) * Number(order.quantity));
                }, 0);
                
                const averageOrderValue = orders.length > 0 
                    ? totalRevenue / orders.length 
                    : 0;
                
                setRevenueMetrics({
                    totalRevenue,
                    averageOrderValue
                });

                const productsResponse = await ApiService.getAllProducts();
                console.log("Raw products response:", productsResponse);
                const products = productsResponse?.productList || [];
                console.log("Processed products:", products);

                const productStats = {
                    totalProducts: products.length,
                    topSellingProducts: products.slice(0, 3),
                    outOfStockProducts: products.filter(p => p.stock === 0).length
                };
                setProductStats(productStats);

                const recentOrders = orders
                    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
                    .slice(0, 5);
                setRecentOrders(recentOrders);

            } catch (error) {
                console.error("Error fetching data:", error);
            }
        };

        fetchData();
    }, []);

            return (
                <div className="admin-profile-page">
                <h1>Admin Dashboard</h1>
                
                <div className="dashboard-grid">
                    <div className="stats-card">
                    <h2>Order Statistics</h2>
                    <div className="stat-item">
                        <span className="stat-label">Total Orders:</span>
                        <span className="stat-value">{orderStats.totalOrders}</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Pending:</span>
                        <span className="stat-value warning">{orderStats.pendingOrders}</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Confirmed:</span>
                        <span className="stat-value success">{orderStats.confirmedOrders}</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Shipped:</span>
                        <span className="stat-value">{orderStats.shippedOrders}</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Cancelled:</span>
                        <span className="stat-value danger">{orderStats.cancelledOrders}</span>
                    </div>
                    </div>
            
                    <div className="stats-card">
                    <h2>Revenue Metrics</h2>
                    <div className="stat-item">
                        <span className="stat-label">Total Revenue:</span>
                        <span className="stat-value revenue">$ {revenueMetrics.totalRevenue.toLocaleString()}</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Avg Order Value:</span>
                        <span className="stat-value">$ {revenueMetrics.averageOrderValue.toLocaleString()}</span>
                    </div>
                    </div>
            
                    <div className="stats-card">
                    <h2>Product Statistics</h2>
                    <div className="stat-item">
                        <span className="stat-label">Total Products:</span>
                        <span className="stat-value">{productStats.totalProducts}</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-label">Out of Stock:</span>
                        <span className="stat-value danger">{productStats.outOfStockProducts}</span>
                    </div>
                    <h3>Top Selling Products</h3>
                    <ul className="top-products-list">
                        {productStats.topSellingProducts.map(product => (
                        <li key={product.id}>
                            <span>{product.name}</span>
                            <span>$ {product.price?.toLocaleString() || 'N/A'}</span>
                        </li>
                        ))}
                    </ul>
                    </div>
                </div>
            
                <div className="chart-container">
                    <h2>Order Status Distribution</h2>
                    <div className="chart-wrapper">
                    <BarChart data={barChartData} options={barChartOptions} />
                    </div>
                </div>
            
                <div className="chart-container">
                    <h2>Revenue Distribution by Status</h2>
                    <div className="chart-wrapper">
                    <PieChart data={pieChartData} options={pieChartOptions} />
                    </div>
                </div>
            
                <div className="chart-container">
                    <h2>Recent Orders</h2>
                    <table className="recent-orders-table">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Product</th>
                            <th>Qty</th>
                            <th>Total</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {recentOrders.map(order => (
                            <tr key={order.id}>
                                <td>{order.id}</td>
                                <td>{order.product?.name || 'N/A'}</td>
                                <td>{order.quantity}</td>
                                <td>$ {(order.price * order.quantity).toLocaleString()}</td>
                                <td>{order.status}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminProfile;