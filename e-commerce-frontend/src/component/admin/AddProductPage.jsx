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
                setMessage("Product created successfully");
                setTimeout(() => {
                    navigate('/admin/products');
                }, 1500);
            } else {
                setMessage("Failed to create product");
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