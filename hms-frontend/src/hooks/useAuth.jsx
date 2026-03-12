// src/hooks/useAuth.jsx
import { useState, useEffect, createContext, useContext } from 'react';
import { authApi } from '../apis/authApi';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Check token on load
    useEffect(() => {
        const token = localStorage.getItem('accessToken');

        if (!token) {
            setLoading(false);
            return;
        }

        try {
            const decoded = jwtDecode(token);

            if (decoded.exp * 1000 < Date.now()) {
                localStorage.removeItem('accessToken');
                setLoading(false);
                return;
            }

            setUser({
                id: decoded.id,
                email: decoded.sub || decoded.email,
                role: decoded.role,
                fullName: decoded.fullName,
                provider: decoded.provider,
            });
        } catch (error) {
            localStorage.removeItem('accessToken');
        } finally {
            setLoading(false);
        }
    }, []);

    const login = async (email, password) => {
        try {
            const response = await authApi.login(email, password);
            const token = response?.data?.token;

            if (!token) {
                throw new Error('No token in response');
            }

            const decoded = jwtDecode(token);

            const userData = {
                id: decoded.id,
                email: decoded.sub || decoded.email,
                role: decoded.role,
                fullName: decoded.fullName,
                provider: decoded.provider,
            };

            localStorage.setItem('accessToken', token);
            setUser(userData);

            return { success: true, user: userData };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || error.message || 'Login failed',
            };
        }
    };

    const register = async (userData) => {
        try {
            const response = await authApi.register(userData);
            return {
                success: true,
                message: response.data?.message || 'Registration successful',
                data: response.data?.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Registration failed',
            };
        }
    };

    const logout = () => {
        localStorage.removeItem('accessToken');
        setUser(null);
        window.location.href = '/';
    };

    const hasRole = (roles) => {
        if (!user) return false;
        return Array.isArray(roles) ? roles.includes(user.role) : user.role === roles;
    };

    const getDashboardPath = (user) => {
        if (!user) return '/';

        switch (user.role) {
            case 'ADMIN':
                return '/admin';
            case 'HOUSEKEEPING':
                return '/housekeeping';
            case 'CUSTOMER':
                return '/';
            case 'RECEPTIONIST':
                return '/receptionist';
            default:
                return '/';
        }
    };

    const value = {
        user,
        loading,
        isAuthenticated: !!user,
        login,
        register,
        logout,
        getDashboardPath,
        hasRole,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error('useAuth must be used within AuthProvider');
    return context;
};