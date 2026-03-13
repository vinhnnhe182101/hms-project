import axios from 'axios';
import { notifications } from '@mantine/notifications';

const BASE_URL = import.meta.env.VITE_BE_API_URL || 'http://localhost:8080/api';

const axiosInstance = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor - SỬA LẠI
axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        // Không xử lý gì nếu là logout request
        if (error.config?.url?.includes('/auth/logout')) {
            return Promise.reject(error);
        }

        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // Chỉ thông báo, không tự động xóa token và redirect
                    notifications.show({
                        title: 'Session Expired',
                        message: 'Please login again',
                        color: 'red',
                    });
                    break;
                case 403:
                    notifications.show({
                        title: 'Access Denied',
                        message: 'You do not have permission to perform this action',
                        color: 'red',
                    });
                    break;
                case 500:
                    notifications.show({
                        title: 'Server Error',
                        message: 'Something went wrong. Please try again later.',
                        color: 'red',
                    });
                    break;
                default:
                    notifications.show({
                        title: 'Error',
                        message: error.response.data?.message || 'An error occurred',
                        color: 'red',
                    });
            }
        } else if (error.request) {
            notifications.show({
                title: 'Network Error',
                message: 'Please check your internet connection',
                color: 'red',
            });
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;