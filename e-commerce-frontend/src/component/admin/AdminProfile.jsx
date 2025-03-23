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
            <h1>Admin Profile</h1>

            <div className="stats-section">
                <h2>Order Statistics</h2>
                <div>Total Orders: {orderStats.totalOrders}</div>
                <div>Pending Orders: {orderStats.pendingOrders}</div>
                <div>Confirmed Orders: {orderStats.confirmedOrders}</div>
                <div>Shipped Orders: {orderStats.shippedOrders}</div>
                <div>Cancelled Orders: {orderStats.cancelledOrders}</div>
            </div>

            <div className="chart-section">
                <h2>Order Statistics (Bar Chart)</h2>
                <BarChart
                    data={barChartData}
                    options={{
                        responsive: true,
                        plugins: {
                            legend: {
                                position: "top"
                            },
                            title: {
                                display: true,
                                text: "Order Status Distribution"
                            }
                        }
                    }}
                />
            </div>

            <div className="stats-section">
                <h2>Revenue Metrics</h2>
                <div>Total Revenue: LKR {revenueMetrics.totalRevenue.toLocaleString()}</div>
                <div>Average Order Value: LKR {revenueMetrics.averageOrderValue.toLocaleString()}</div>
            </div>

            <div className="chart-section">
                <h2>Revenue Distribution (Pie Chart)</h2>
                <PieChart
                    data={pieChartData}
                    options={{
                        responsive: true,
                        plugins: {
                            legend: {
                                position: "top"
                            },
                            title: {
                                display: true,
                                text: "Revenue Distribution by Status"
                            }
                        }
                    }}
                />
            </div>

            <div className="stats-section">
                <h2>Product Statistics</h2>
                <div>Total Products: {productStats.totalProducts}</div>
                <div>Top Selling Products:</div>
                <ul>
                    {productStats.topSellingProducts.map(product => (
                        <li key={product.id}>
                            {product.name} - LKR {product.price?.toLocaleString() || 'N/A'}
                        </li>
                    ))}
                </ul>
                <div>Out of Stock Products: {productStats.outOfStockProducts}</div>
            </div>

            <div className="stats-section">
                <h2>Recent Orders</h2>
                <table>
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
                                <td>LKR {(order.price * order.quantity).toLocaleString()}</td>
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