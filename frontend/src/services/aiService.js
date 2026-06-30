// src/services/aiService.js
import axiosClient from '../api/axiosClient';

export const aiService = {
  askQuestion: async (question) => {
    // This routes through your API Gateway (port 8080) to the ai-service (port 8087)
    const response = await axiosClient.get(`/api/ai/ask?question=${encodeURIComponent(question)}`);
    return response.data;
  }
};