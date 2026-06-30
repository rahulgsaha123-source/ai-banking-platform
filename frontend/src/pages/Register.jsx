import { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // 1. Import useNavigate
import axiosClient from '../api/axiosClient';

function Register() {
  const [formData, setFormData] = useState({ username: '', email: '', password: '' });
  const navigate = useNavigate(); // 2. Initialize navigate

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axiosClient.post('/api/auth/register', formData);
      alert('Registration successful!');
      
      // 3. Navigate to login page
      navigate('/login'); 
      
    } catch (err) {
      console.error(err);
      alert('Registration failed. Check console for details.');
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-50">
      <form onSubmit={handleSubmit} className="p-8 bg-white rounded shadow-md w-96">
        <h2 className="mb-6 text-2xl font-bold text-center">Create Account</h2>
        <input className="w-full p-2 mb-4 border rounded" type="text" placeholder="Username" onChange={(e) => setFormData({...formData, username: e.target.value})} />
        <input className="w-full p-2 mb-4 border rounded" type="email" placeholder="Email" onChange={(e) => setFormData({...formData, email: e.target.value})} />
        <input className="w-full p-2 mb-4 border rounded" type="password" placeholder="Password" onChange={(e) => setFormData({...formData, password: e.target.value})} />
        <button className="w-full p-2 text-white bg-blue-600 rounded hover:bg-blue-700 transition">Register</button>
        
        {/* Added a link back to Login if they already have an account */}
        <p className="mt-4 text-sm text-center text-gray-600">
          Already have an account? <a href="/login" className="text-blue-600 hover:underline">Login</a>
        </p>
      </form>
    </div>
  );
}

export default Register;