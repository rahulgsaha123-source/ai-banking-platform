import axiosClient from '../api/axiosClient';

export const accountService = {
  createProfile: (data) => axiosClient.post('/api/customers', data),
  // The 'data' parameter here will be the payload object { username, accountType }
  createAccount: (data) => axiosClient.post('/api/accounts', data),
  creditAccount: (accountNumber, amount) => 
    axiosClient.put(`/api/accounts/${accountNumber}/credit`, { amount }),
};