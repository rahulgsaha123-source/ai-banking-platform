import axiosClient from '../api/axiosClient';

export const aiService = {
  askQuestion: async (question) => {
    
    // Grab the real logged-in user from the browser's memory!
    // If they aren't logged in, it gracefully falls back to 'unknown'.
    const currentUser = localStorage.getItem('username') || 'unknown'; 

    const response = await axiosClient.get('/api/ai/ask', {
      params: {
        question: question,
        username: currentUser // <--- Now it sends the REAL user to Java!
      }
    });
    
    return response.data;
  }
};