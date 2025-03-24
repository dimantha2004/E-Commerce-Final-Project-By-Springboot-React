import React from "react";
import { useNavigate } from "react-router-dom";
import '../../style/adminPage.css'

const AdminPage = () => {
    const navigate = useNavigate();

    return(
        <div className="admin-page">
            <h1>Welcome Admin</h1>
            <div className="admin-dashboard">
                <div className="dashboard-card">
                    <i>📋</i>
                    <h2>Categories</h2>
                    <p>Manage product categories and organization</p>
                    <button onClick={()=> navigate("/admin/categories")}>Manage Categories</button>
                </div>
                <div className="dashboard-card">
                    <i>🛍️</i>
                    <h2>Products</h2>
                    <p>Add, edit or remove products from your store</p>
                    <button onClick={()=> navigate("/admin/products")}>Manage Products</button>
                </div>
                <div className="dashboard-card">
                    <i>📦</i>
                    <h2>Orders</h2>
                    <p>View and process customer orders</p>
                    <button onClick={()=> navigate("/admin/orders")}>Manage Orders</button>
                </div>
            </div>
        </div>
    )
}
export default AdminPage;