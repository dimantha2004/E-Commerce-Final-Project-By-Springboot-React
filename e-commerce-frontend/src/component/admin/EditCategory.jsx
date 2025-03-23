import React, { useState, useEffect } from "react";
import ApiService from "../../service/ApiService";
import { useNavigate, useParams } from "react-router-dom";
import '../../style/addCategory.css';

const EditCategory = () => {
    const { categoryId } = useParams();
    const [name, setName] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchCategory(categoryId);
    }, [categoryId]);

    const fetchCategory = async (id) => {
        setLoading(true);
        setError(null);
        try {
            const response = await ApiService.getCategoryById(id);
            console.log("API Response:", response);
            
            if (!response.error && response.category) {
                setName(response.category.name);
            } else {
                setError(response.message || "Category not found");
            }
        } catch (error) {
            console.error("Full error details:", error);
            setError("Failed to load category. Check console for details.");
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!name.trim()) {
            setMessage("Category name cannot be empty");
            return;
        }
        
        try {
            const response = await ApiService.updateCategory(categoryId, { name });
            
            if (!response.error) {
                setMessage("Category updated successfully");
                setError(null);
                setTimeout(() => {
                    navigate("/admin/categories");
                }, 2000);
            } else {
                setMessage(response.message || "Failed to update category");
            }
        } catch (error) {
            setMessage("An unexpected error occurred. Please try again later.");
            console.error("Error updating category:", error);
        }
    };

    if (loading) {
        return <div className="add-category-page"><p>Loading category data...</p></div>;
    }

    return (
        <div className="add-category-page">
            {error && (
                <div className="error-container">
                    <p className="error-message">{error}</p>
                    <button onClick={() => fetchCategory(categoryId)}>Retry</button>
                </div>
            )}
            
            {message && (
                <div className="success-container">
                    <p className="success-message">{message}</p>
                </div>
            )}

            <form onSubmit={handleSubmit} className="category-form">
                <h2>Edit Category</h2>
                <input 
                    type="text"
                    placeholder="Category Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required 
                />
                <div className="button-group">
                    <button 
                        type="submit" 
                        className="btn-primary"
                        disabled={loading || !name.trim()}
                    >
                        {loading ? "Updating..." : "Update"}
                    </button>
                    <button 
                        type="button" 
                        className="btn-secondary"
                        onClick={() => navigate("/admin/categories")}
                    >
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditCategory;