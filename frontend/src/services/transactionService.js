import axiosClient from '../api/axiosClient';

export const transactionService = {
  // Uses the endpoint you tested: /api/transactions/account/{accountNumber}/statement?page=0&size=5
  getAccountStatement: (accountNumber, page = 0, size = 5) => 
    axiosClient.get(`/api/transactions/account/${accountNumber}/statement`, {
      params: { page, size }
    }),
};