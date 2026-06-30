import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosClient from '../api/axiosClient';

function Login() {
  // 1. CHANGED 'username' to 'email' here
  const [formData, setFormData] = useState({ email: '', password: '' });
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Hits http://18.61.44.190:8080/api/auth/login via the Gateway
      const response = await axiosClient.post('/api/auth/login', formData);
      
      localStorage.setItem('token', response.data.token); 
      alert('Login successful!');
      navigate('/dashboard'); 
    } catch (err) {
      console.error(err);
      alert('Login failed. Please check your credentials.');
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-50">
      <form onSubmit={handleSubmit} className="p-8 bg-white rounded shadow-md w-96">
        <h2 className="mb-6 text-2xl font-bold text-center">Login to Banking</h2>
        {/* 2. CHANGED placeholder and state update to use 'email' */}
        <input 
          className="w-full p-2 mb-4 border rounded" 
          type="email" 
          placeholder="Email address" 
          onChange={(e) => setFormData({...formData, email: e.target.value})} 
        />
        <input 
          className="w-full p-2 mb-6 border rounded" 
          type="password" 
          placeholder="Password" 
          onChange={(e) => setFormData({...formData, password: e.target.value})} 
        />
        <button className="w-full p-2 text-white bg-green-600 rounded hover:bg-green-700 transition">
          Login
        </button>
        <p className="mt-4 text-sm text-center text-gray-600">
          Don't have an account? <a href="/register" className="text-blue-600 hover:underline">Register</a>
        </p>
      </form>
    </div>
  );
}

export default Login;