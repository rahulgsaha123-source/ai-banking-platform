import axiosClient from '../api/axiosClient';

export const paymentService = {
  transferFunds: (transferData) => axiosClient.post('/api/payments/transfer', transferData),
};