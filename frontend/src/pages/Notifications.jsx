import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { notificationService } from '../services/notificationService';

function Notifications() {
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState([]);

  // Load the notifications from Local Storage when the page opens
  useEffect(() => {
    setNotifications(notificationService.getNotifications());
    notificationService.resetCount(); // Clear the red badge
  }, []);

  const handleClear = () => {
    notificationService.clearAll();
    setNotifications([]);
  };

  return (
    <div className="max-w-4xl mx-auto mt-10 p-6 bg-white rounded-lg shadow-md">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Your Notifications</h2>
        <div className="space-x-4">
          <button onClick={handleClear} className="text-red-500 hover:underline text-sm font-medium">
            Clear All
          </button>
          <button onClick={() => navigate('/dashboard')} className="text-blue-600 hover:underline font-medium">
            &larr; Back to Dashboard
          </button>
        </div>
      </div>

      {notifications.length === 0 ? (
        <div className="p-4 border-l-4 border-blue-500 bg-blue-50 rounded shadow-sm mb-4">
          <p className="text-gray-700 font-medium">Welcome to your banking notifications!</p>
          <p className="text-sm text-gray-500 mt-1">Right now, you are all caught up.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {notifications.map((notif) => (
            <div key={notif.id} className="p-4 bg-green-50 border border-green-200 rounded-lg shadow-sm">
              <p className="text-gray-800 font-medium">{notif.message}</p>
              <p className="text-xs text-gray-500 mt-1">{notif.date}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Notifications;