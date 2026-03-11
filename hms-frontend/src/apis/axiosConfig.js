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

// Response interceptor
axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.config?.url?.includes('/auth/logout')) {
            return Promise.reject(error);
        }

        if (error.response) {
            switch (error.response.status) {
                case 401:
                    notifications.show({
                        title: 'Phiên làm việc hết hạn',
                        message: 'Vui lòng đăng nhập lại',
                        color: 'red',
                    });
                    break;
                case 403:
                    notifications.show({
                        title: 'Truy cập bị từ chối',
                        message: 'Bạn không có quyền thực hiện hành động này',
                        color: 'red',
                    });
                    break;
                case 500:
                    notifications.show({
                        title: 'Lỗi hệ thống',
                        message: 'Có lỗi xảy ra. Vui lòng thử lại sau.',
                        color: 'red',
                    });
                    break;
                default:
                    notifications.show({
                        title: 'Lỗi',
                        message: error.response.data?.message || 'Đã có lỗi xảy ra',
                        color: 'red',
                    });
            }
        } else if (error.request) {
            notifications.show({
                title: 'Lỗi mạng',
                message: 'Vui lòng kiểm tra kết nối internet của bạn',
                color: 'red',
            });
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
