import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import '../../style/addProduct.css';
import ApiService from "../../service/ApiService";

const AddProductPage = () => {
    const [image, setImage] = useState(null);
    const [categories, setCategories] = useState([]);
    const [categoryId, setCategoryId] = useState('');
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [message, setMessage] = useState('');
    const [price, setPrice] = useState('');
    const [imageUrl, setImageUrl] = useState(null);
    const [quantity, setQuantity] = useState(0);
    const navigate = useNavigate();
    
    useEffect(() => {
        
        ApiService.getAllCategory().then((res) => setCategories(res.categoryList));
    }, []);

    const handleImageChange = (e) => {
        setImage(e.target.files[0]);
        setImageUrl(URL.createObjectURL(e.target.files[0]));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const formData = new FormData();
            formData.append('categoryId', categoryId);
            formData.append('image', image);
            formData.append('name', name);
            formData.append('description', description);
            formData.append('price', price);
            formData.append('quantity', quantity);
            
            const response = await ApiService.createProduct(formData);
            
            if (response && response.status === 200) {
                setMessage(
                  <div className="toast-message success">
                    <svg className="toast-icon" viewBox="0 0 24 24">
                      <path fill="var(--success-color)" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                    </svg>
                    <div className="toast-content">
                      <h3>Success!</h3>
                      <p>Product created successfully</p>
                    </div>
                  </div>
                );
                setTimeout(() => {
                  navigate('/admin/products');
                }, 1500);
              } else {
                setMessage(
                  <div className="toast-message error">
                    <svg className="toast-icon" viewBox="0 0 24 24">
                      <path fill="var(--danger-color)" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                    </svg>
                    <div className="toast-content">
                      <h3>Error!</h3>
                      <p>Failed to create product</p>
                    </div>
                  </div>
                );
              }
        } catch (error) {
            console.error("Create error:", error);
            setMessage(error.response?.data?.message || error.message || 'Unable to create product');
        }
    };

    return (
        <form onSubmit={handleSubmit} className="product-form">
            <h2>Add Product</h2>
            {message && <div className="message">{message}</div>}
            <input type="file" onChange={handleImageChange} required />
            {imageUrl && <img src={imageUrl} alt="Preview" />}
            <select value={categoryId} onChange={(e) => setCategoryId(e.target.value)} required>
                <option value="">Select Category</option>
                {categories.map((cat) => (
                    <option value={cat.id} key={cat.id}>{cat.name}</option>
                ))}
            </select>

            <input
                type="text"
                placeholder="Product name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
            />

            <textarea
                placeholder="Description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                required
            />

            <input
                type="number"
                placeholder="Price"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                required
            />
            <input
                type="number"
                placeholder="Quantity"
                value={quantity}
                onChange={(e) => setQuantity(Number(e.target.value))}
                min="0"
                required
            />

            <button type="submit">Add Product</button>
        </form>
    );
};

export default AddProductPage;