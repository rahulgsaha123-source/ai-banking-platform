import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosClient from '../api/axiosClient';
import { paymentService } from '../services/paymentService';
import { notificationService } from '../services/notificationService';

function Transfer() {
  const navigate = useNavigate();
  const [fromAccount, setFromAccount] = useState('');
  const [toAccount, setToAccount] = useState('');
  const [amount, setAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  // Auto-fetch the logged-in user's account number
  useEffect(() => {
    const fetchMyAccount = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) return navigate('/login');

        const payload = JSON.parse(atob(token.split('.')[1]));
        const username = payload.sub;

        const response = await axiosClient.get(`/api/accounts/${username}`);
        if (response.data && response.data.length > 0) {
          setFromAccount(response.data[0].accountNumber);
        }
      } catch (err) {
        console.error("Could not fetch account details for transfer", err);
      }
    };
    fetchMyAccount();
  }, [navigate]);

  // THIS IS THE IMPORTANT PART: 'async' must be right here before (e) =>
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      const response = await paymentService.transferFunds({
        fromAccount: fromAccount, 
        toAccount: toAccount,     
        amount: parseFloat(amount)
      });
      
      // Look at the actual status returned from the backend
      if (response.data.status === 'FAILED') {
        setMessage({ type: 'error', text: 'Transfer failed on the server. Please check the recipient account number or your balance.' });
        // Optional: You could add a failure notification here if you wanted!
        // notificationService.addNotification(`Transfer of ₹${amount} failed.`);
      } else {
        // ✅ ONLY FIRE THE SUCCESS NOTIFICATION HERE
        notificationService.addNotification(`Successfully transferred ₹${amount} to account ${toAccount}!`);
        
        setMessage({ type: 'success', text: `Transfer completed successfully! Ref: ${response.data.referenceNumber}` });
        setToAccount('');
        setAmount('');
      }

    } catch (err) {
      console.error(err);
      setMessage({ type: 'error', text: 'Transfer failed. Check network or server logs.' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen p-8 bg-gray-100 flex items-center justify-center">
      <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-md">
        <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">Transfer Funds</h2>
        
        {message.text && (
          <div className={`p-4 mb-4 rounded ${message.type === 'success' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
            {message.text}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block mb-1 text-sm font-medium text-gray-700">From Account</label>
            <input 
              type="text" 
              value={fromAccount} 
              disabled 
              className="w-full p-2 bg-gray-100 border border-gray-300 rounded cursor-not-allowed" 
            />
          </div>

          <div>
            <label className="block mb-1 text-sm font-medium text-gray-700">To Account (Recipient)</label>
            <input 
              type="text" 
              required
              value={toAccount}
              onChange={(e) => setToAccount(e.target.value)}
              placeholder="Enter recipient account number"
              className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500" 
            />
          </div>

          <div>
            <label className="block mb-1 text-sm font-medium text-gray-700">Amount (₹)</label>
            <input 
              type="number" 
              required
              min="1"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="0.00"
              className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500" 
            />
          </div>

          <button 
            type="submit" 
            disabled={loading || !fromAccount}
            className="w-full py-2 px-4 text-white bg-blue-600 rounded hover:bg-blue-700 transition disabled:bg-blue-300 mt-4"
          >
            {loading ? 'Processing...' : 'Send Money'}
          </button>
        </form>
        
        <div className="mt-4 text-center">
          <button onClick={() => navigate('/dashboard')} className="text-sm text-blue-600 hover:underline">
            &larr; Back to Dashboard
          </button>
        </div>
      </div>
    </div>
  );
}

export default Transfer;