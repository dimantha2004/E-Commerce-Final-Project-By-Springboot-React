import React, { useState, useEffect } from "react";
import '../../style/navbar.css';
import { NavLink, useNavigate, useLocation } from "react-router-dom";
import ApiService from "../../service/ApiService";

const Navbar = () => {
    const [searchValue, setSearchValue] = useState("");
    const [showLogoutModal, setShowLogoutModal] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();

    const isAdmin = ApiService.isAdmin(); 
    const isAuthenticated = ApiService.isAuthenticated();

    useEffect(() => {
        console.log("Is Admin:", isAdmin);
        console.log("Is Authenticated:", isAuthenticated);
    }, [isAdmin, isAuthenticated]);

    useEffect(() => {
        if (isAuthenticated && isAdmin && location.pathname === "/login") {
            navigate("/admin-profile");
        }
    }, [isAuthenticated, isAdmin, navigate, location.pathname]);

    const handleSearchChange = (e) => {
        setSearchValue(e.target.value);
    };

    const handleSearchSubmit = async (e) => {
        e.preventDefault();
        navigate(`/?search=${searchValue}`);
    };

    const handleLogout = () => {
        setShowLogoutModal(true);
    };

    const confirmLogout = () => {
        ApiService.logout();
        setShowLogoutModal(false);
        setTimeout(() => {
            navigate(`/login`);
        }, 500);
    };

    const cancelLogout = () => {
        setShowLogoutModal(false);
    };

    return (
        <>
            <nav className="navbar">
                <div className="navbar-brand">
                    <NavLink to="/"><img src="./ecomlogo.png" alt="Online Shopping" /></NavLink>
                </div>

                {isAuthenticated && (
                    <form className="navbar-search" onSubmit={handleSearchSubmit}>
                        <input
                            type="text"
                            placeholder="Search products"
                            value={searchValue}
                            onChange={handleSearchChange}
                        />
                        <button type="submit">Search</button>
                    </form>
                )}

                <div className="navbar-link">
                    {isAuthenticated && !isAdmin && (
                        <>
                            <NavLink to="/">Home</NavLink>
                            <NavLink to="/categories">Categories</NavLink>
                            <NavLink to="/cart">Cart</NavLink>
                            <NavLink to="/chat">Chat</NavLink>
                            <NavLink to="/profile">Profile</NavLink>
                            <button className="logout-btn" onClick={handleLogout}>Logout</button>
                        </>
                    )}

                    {isAuthenticated && isAdmin && (
                        <>
                            <NavLink to="/admin-profile">Statistics</NavLink>
                            <NavLink to="/admin">Manage</NavLink>
                            <NavLink to="/chat">Chat</NavLink>
                            <button className="logout-btn" onClick={handleLogout}>Logout</button>
                        </>
                    )}

                    {!isAuthenticated && <NavLink to="/login">Login</NavLink>}
                </div>
            </nav>

            {showLogoutModal && (
                <div className="logout-modal-overlay">
                    <div className="logout-modal">
                        <div className="logout-modal-content">
                            <div className="logout-modal-header">
                                <svg className="logout-modal-icon" viewBox="0 0 24 24">
                                    <path fill="var(--primary-color)" d="M14.08 15.59L16.67 13H7v-2h9.67l-2.59-2.59L15.5 7l5 5-5 5-1.42-1.41zM19 3a2 2 0 0 1 2 2v4.67l-2-2V5H5v14h14v-2.67l2-2V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14z"/>
                                </svg>
                                <h3>Confirm Logout</h3>
                            </div>
                            <div className="logout-modal-body">
                                <p>Are you sure you want to log out? You'll need to sign in again to access your account.</p>
                            </div>
                            <div className="logout-modal-footer">
                                <button className="logout-modal-cancel" onClick={cancelLogout}>Cancel</button>
                                <button className="logout-modal-confirm" onClick={confirmLogout}>Log Out</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
};

export default Navbar;