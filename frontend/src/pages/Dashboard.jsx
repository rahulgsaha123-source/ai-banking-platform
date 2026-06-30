import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosClient from '../api/axiosClient';
import { accountService } from '../services/accountService';
import { transactionService } from '../services/transactionService';
import AccountManager from '../components/AccountManager';
import { notificationService } from '../services/notificationService';
import { BellIcon } from '@heroicons/react/24/outline';

const CustomTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    const data = payload[0].payload;
    return (
      <div className="bg-white p-3 border border-gray-200 shadow-lg rounded-lg">
        <p className="text-sm font-semibold text-gray-700">{new Date(data.timestamp || data.transactionDate).toLocaleDateString()}</p>
        <p className="text-sm text-blue-600 font-bold">Balance: ₹{data.displayBalance}</p>
        <p className="text-xs text-gray-500">Type: {data.transactionType} (₹{data.amount})</p>
      </div>
    );
  }
  return null;
};

function Dashboard() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [accountData, setAccountData] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [username, setUsername] = useState('');
  const [filter, setFilter] = useState('ALL');
  const [unreadCount, setUnreadCount] = useState(notificationService.getUnreadCount());


  const downloadMiniStatement = () => {
    let content = `MINI STATEMENT - Account: ${accountData?.accountNumber}\nDate: ${new Date().toLocaleString()}\n------------------------------------------\n\n`;
    transactions.forEach(tx => {
      content += `Date: ${new Date(tx.timestamp || tx.transactionDate).toLocaleString()}\nType: ${tx.transactionType}\nAmount: ₹${tx.amount}\nRef: ${tx.referenceNumber || 'N/A'}\n--------------------------\n`;
    });
    const blob = new Blob([content], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `mini-statement-${accountData?.accountNumber || 'account'}.txt`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleCredit = async (accountNumber) => {
    if (!accountNumber) return alert("Account not found");
    const amount = prompt("Enter amount to add:");
    if (amount && !isNaN(amount)) {
      try {
        await accountService.creditAccount(accountNumber, parseFloat(amount));
        alert("Credit successful!");
        window.location.reload();
      } catch (err) {
        alert("Failed to add funds.");
      }
    }
  };

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {

        const token = localStorage.getItem('token');
        if (!token) { navigate('/login'); return; }
        const profileResponse = await axiosClient.get('/api/auth/profile');
        setProfile(profileResponse.data);
        const payload = JSON.parse(atob(token.split('.')[1]));
        setUsername(payload.sub);

        try {
          const accountResponse = await axiosClient.get(`/api/accounts/${payload.sub}`);
          const customerResponse = await axiosClient.get(`/api/customers/${payload.sub}`);

          if (accountResponse.data?.length > 0) {
            const acc = accountResponse.data[0];
            setAccountData({ ...acc, customer: customerResponse.data });

            const txResponse = await transactionService.getAccountStatement(acc.accountNumber, 0, 10);
            const rawTxs = txResponse.data.content || txResponse.data || [];

            // 1. ADD THE HELPER FUNCTION HERE (inside the fetch function)
            const calculateRunningBalance = (txs) => {
              let balance = acc.balance; // Use the account balance from the API response
              return [...txs].reverse().map((tx) => {
                const amount = parseFloat(tx.amount);
                const prevBalance = tx.transactionType === 'TRANSFER_OUT' ? balance + amount : balance - amount;
                balance = prevBalance;
                return { ...tx, displayBalance: prevBalance };
              }).reverse();
            };

            // 2. CALL THE FUNCTION AND SET STATE
            const processedTxs = calculateRunningBalance(rawTxs);
            setTransactions(processedTxs);

          } else {
            setError("No bank account found.");
          }
        } catch (accErr) { setError("Data load failed."); }
        setLoading(false);
      } catch (err) { setError('Failed to load dashboard.'); setLoading(false); }
    };
    fetchDashboardData();
  }, [navigate]);
  useEffect(() => {
    setUnreadCount(notificationService.getUnreadCount());
  }, []);
  return (
    <div className="min-h-screen p-8 bg-gray-100">
      <div className="max-w-4xl p-6 mx-auto bg-white rounded-lg shadow-md">
        <button
          onClick={() => {
            notificationService.resetCount();
            setUnreadCount(0);
            navigate('/notifications');
          }}
          className="relative p-2 text-gray-600 hover:text-blue-600 transition-all focus:outline-none"
        >
          {/* Replaced BellIcon with a standard SVG */}
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-7 h-7">
            <path strokeLinecap="round" strokeLinejoin="round" d="M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0" />
          </svg>

          {unreadCount > 0 && (
            <span className="absolute top-1 right-1 w-5 h-5 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center animate-pulse">
              {unreadCount > 9 ? '9+' : unreadCount}
            </span>
          )}
        </button>

        {loading ? <p>Loading...</p> : error ? (
          <div className="space-y-4">
            <div className="p-4 text-orange-700 bg-orange-100 rounded">{error}</div>
            <AccountManager username={username} onRefresh={() => window.location.reload()} />
          </div>
        ) : (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="p-6 bg-blue-50 border rounded-lg">
                <h2 className="text-xl font-semibold text-blue-800">Balance</h2>
                <p className="text-4xl font-bold">₹{accountData?.balance || '0.00'}</p>
                <div className="flex flex-wrap gap-2 mt-4">
                  <button onClick={() => handleCredit(accountData?.accountNumber)} className="px-4 py-2 bg-green-600 text-white rounded">Add Funds</button>
                  <button onClick={() => navigate('/transfer')} className="px-4 py-2 bg-blue-600 text-white rounded">Transfer</button>
                  <button onClick={downloadMiniStatement} className="px-4 py-2 bg-gray-600 text-white rounded">Download .txt</button>
                  <button
                    onClick={() => navigate('/ai-assistant')}
                    className="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700 transition"
                  >
                    Ask AI Assistant ✨
                  </button>
                </div>
              </div>
              <div className="p-6 bg-green-50 border border-green-200 rounded-lg shadow-sm">
                <h2 className="text-xl font-semibold text-green-800 mb-4 border-b pb-2">Account Profile</h2>
                <div className="text-gray-700 space-y-1">
                  <p><strong>Name:</strong> {accountData?.customer?.firstName} {accountData?.customer?.lastName}</p>
                  <p><strong>Account Type:</strong> <span className="uppercase font-bold">{accountData?.accountType?.replace('_', ' ')}</span></p>
                  <p><strong>Account No:</strong> {accountData?.accountNumber}</p>
                  <p><strong>Phone:</strong> {accountData?.customer?.phoneNumber}</p>
                </div>
              </div>
            </div>
            <div className="mt-8">
              <h2 className="text-2xl font-bold mb-4">Recent Transactions</h2>
              <div className="mb-4 flex gap-2">
                {['ALL', 'TRANSFER_IN', 'TRANSFER_OUT'].map(type => (
                  <button key={type} onClick={() => setFilter(type)} className={`px-3 py-1 rounded text-sm ${filter === type ? 'bg-blue-600 text-white' : 'bg-gray-200'}`}>
                    {type}
                  </button>
                ))}
              </div>
              <table className="w-full text-left bg-white border rounded-lg">
                <thead className="bg-gray-50 border-b">
                  <tr><th className="p-4 text-sm font-semibold text-gray-600">Date</th><th className="p-4 text-sm font-semibold text-gray-600">Type</th><th className="p-4 text-sm font-semibold text-gray-600">Amount</th></tr>
                </thead>
                <tbody>
                  {transactions.filter(tx => filter === 'ALL' ? true : tx.transactionType === filter).map((tx, index) => (
                    <tr key={index} className="border-b">
                      <td className="p-4 text-sm">{new Date(tx.timestamp || tx.transactionDate).toLocaleString()}</td>
                      <td className="p-4 text-sm"><span className={`px-2 py-1 rounded-full text-xs ${tx.transactionType === 'TRANSFER_IN' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>{tx.transactionType}</span></td>
                      <td className="p-4 text-sm font-semibold">₹{tx.amount}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default Dashboard;