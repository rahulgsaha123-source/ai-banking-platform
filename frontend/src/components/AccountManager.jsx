import { useState } from 'react';
import { accountService } from '../services/accountService';

function AccountManager({ username, onRefresh }) {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    address: ''
  });
  const [loading, setLoading] = useState(false);
  const [accountType, setAccountType] = useState('SAVINGS'); // Added state

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleInitialize = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      // 1. Create Profile
      await accountService.createProfile({
        username: username,
        ...formData
      });

      // 2. Create Account with Transformed Type
      // Mapping 'SALARY' UI label to backend 'SALARY_ACCOUNT'
      const backendAccountType = accountType === 'SALARY' ? 'SALARY_ACCOUNT' : accountType;

      await accountService.createAccount({
        username: username,
        accountType: backendAccountType 
      });

      alert("Profile and Account Created Successfully!");
      if (onRefresh) onRefresh();
    } catch (err) {
      console.error("Initialization failed", err);
      alert("Failed to initialize. Check console for details.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 bg-white rounded-lg shadow-md border border-gray-200">
      <h2 className="text-xl font-bold mb-4 text-gray-800">Complete Your Setup</h2>
      <form onSubmit={handleInitialize} className="space-y-4">
        <input name="firstName" required className="block w-full p-2 border rounded" 
               placeholder="First Name" onChange={handleInputChange} />
        <input name="lastName" required className="block w-full p-2 border rounded" 
               placeholder="Last Name" onChange={handleInputChange} />
        <input name="phoneNumber" required className="block w-full p-2 border rounded" 
               placeholder="Phone Number" onChange={handleInputChange} />
        <input name="address" required className="block w-full p-2 border rounded" 
               placeholder="Address" onChange={handleInputChange} />
        
        {/* Added Account Type Dropdown */}
        <div className="pt-2">
          <label className="block text-sm font-medium text-gray-700 mb-1">Account Type</label>
          <select 
            value={accountType} 
            onChange={(e) => setAccountType(e.target.value)}
            className="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
          >
            <option value="SAVINGS">Savings Account</option>
            <option value="SALARY">Salary Account</option>
            <option value="CURRENT">Current Account</option>
          </select>
        </div>
        
        <button type="submit" disabled={loading}
                className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-blue-300">
          {loading ? "Processing..." : "Create Profile & Account"}
        </button>
      </form>
    </div>
  );
}

export default AccountManager;