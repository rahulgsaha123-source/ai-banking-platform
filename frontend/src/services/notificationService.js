
export const notificationService = {
  getUnreadCount: () => {
    return parseInt(localStorage.getItem('unreadCount') || '0');
  },
  
  getNotifications: () => {
    // Parse the saved array, or return an empty array if none exist
    return JSON.parse(localStorage.getItem('notifications') || '[]');
  },

  addNotification: (message) => {
    const notifications = notificationService.getNotifications();
    
    // Add the new message to the top of the list
    notifications.unshift({
      id: Date.now(),
      message: message,
      date: new Date().toLocaleString()
    });
    
    // Save the updated list back to the browser
    localStorage.setItem('notifications', JSON.stringify(notifications));
    
    // Increase the unread badge count
    const currentCount = notificationService.getUnreadCount();
    localStorage.setItem('unreadCount', (currentCount + 1).toString());
  },
  
  resetCount: () => {
    localStorage.setItem('unreadCount', '0');
  },

  clearAll: () => {
    localStorage.setItem('notifications', '[]');
    localStorage.setItem('unreadCount', '0');
  }
};