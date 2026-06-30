import axios from 'axios';

// const axiosClient = axios.create({
//   baseURL: 'http://18.61.44.190:8080', 
// });

// // Automatically attach the JWT token to every request!
// axiosClient.interceptors.request.use((config) => {
//   const token = localStorage.getItem('token');
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`;
//   }
//   return config;
// });


const axiosClient = axios.create({
  // Change this to your local Gateway URL (usually port 8080)
  baseURL: 'http://localhost:8080', 
  headers: {
    'Content-Type': 'application/json',
  },
});

export default axiosClient;