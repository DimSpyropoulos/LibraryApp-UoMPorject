import axios from 'axios';
import useAuthStore from '../stores/authStore';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8089',
});

axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token'); // Retrieve token from localStorage
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            useAuthStore.getState().clearToken();
            window.location.href = '/signin'; // Redirect to sign-in page on token expiration
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
