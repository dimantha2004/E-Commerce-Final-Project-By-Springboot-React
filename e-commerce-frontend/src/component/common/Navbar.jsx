import React, { useState, useEffect } from "react";
import '../../style/navbar.css';
import { NavLink, useNavigate, useLocation } from "react-router-dom";
import ApiService from "../../service/ApiService";

const Navbar = () => {
    const [searchValue, setSearchValue] = useState("");
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
        const confirm = window.confirm("Do you want to log out...?");
        if (confirm) {
            ApiService.logout();
            setTimeout(() => {
                navigate(`/login`);
            }, 500);
        }
    };

    return (
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
                        <NavLink to="/profile">My Account</NavLink>
                        <NavLink onClick={handleLogout}>Logout</NavLink>
                    </>
                )}

                {isAuthenticated && isAdmin && (
                    <>
                        <NavLink to="/admin-profile">Admin Profile</NavLink>
                        <NavLink to="/admin">Admin</NavLink>
                        <NavLink to="/chat">Chat</NavLink>
                        <NavLink onClick={handleLogout}>Logout</NavLink>
                    </>
                )}

                {!isAuthenticated && <NavLink to="/login">Login</NavLink>}
            </div>
        </nav>
    );
};

export default Navbar;