// src/hooks/useAuth.jsx
import * as React from "react";
import {createContext, useContext, useEffect, useState} from "react";
import {authApi} from "../apis/authApi";
import {jwtDecode} from "jwt-decode";

/** @type {AuthContextType} */
const AuthContext = createContext(null);

export const AuthProvider = ({children}) => {
    /**
     * @type {[UserResponseDTO, React.Dispatch<UserResponseDTO>]}
     */
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Check token on load
    useEffect(() => {
        const token = localStorage.getItem("accessToken");

        if (!token) {
            setLoading(false);
            return;
        }

        try {
            /**
             * @type {JwtPayload}
             */
            const decoded = jwtDecode(token);

            if (decoded.exp * 1000 < Date.now()) {
                localStorage.removeItem("accessToken");
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
            localStorage.removeItem("accessToken");
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * Hàm đăng nhập người dùng
     *
     * @param {string} email - Email của người dùng
     * @param {string} password - Mật khẩu của người dùng
     * @return {Promise<{success: boolean, user?: UserResponseDTO, error?: string}>}
     */
    const login = async (email, password) => {
        try {
            const response = await authApi.login(email, password);
            const token = response?.data?.token;

            if (!token) {
                throw new Error("No token in response");
            }

            const decoded = jwtDecode(token);

            const userData = {
                id: decoded.id,
                email: decoded.sub || decoded.email,
                role: decoded.role,
                fullName: decoded.fullName,
                provider: decoded.provider,
            };

            localStorage.setItem("accessToken", token);
            setUser(userData);

            return {success: true, user: userData};
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || error.message || "Login failed",
            };
        }
    };

    /**
     * Hàm đăng ký người dùng
     *
     * @param {UserRequestDTO} userData - Dữ liệu đăng ký người dùng
     * @return {Promise<{success: boolean, message?: string, data?: UserResponseDTO, error?: string}>}
     */
    const register = async (userData) => {
        try {
            const response = await authApi.register(userData);
            return {
                success: true,
                message: response.data?.message || "Registration successful",
                data: response.data?.data,
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || "Registration failed",
            };
        }
    };

    /**
     * Hàm đăng xuất người dùng
     */
    const logout = () => {
        localStorage.removeItem("accessToken");
        setUser(null);
        window.location.href = "/";
    };

    /**
     * Hàm kiểm tra vai trò của người dùng
     *
     * @param {string|string[]} roles - Vai trò hoặc mảng vai trò cần kiểm tra
     * @return {boolean} - Trả về true nếu người dùng có vai trò phù hợp, ngược lại trả về false
     */
    const hasRole = (roles) => {
        if (!user) return false;
        return Array.isArray(roles)
                ? roles.includes(user.role)
                : user.role === roles;
    };

    const getDashboardPath = (targetUser) => {
        if (!targetUser) return "/";

        // Support both ADMIN and ROLE_ADMIN style payloads.
        const role = String(targetUser.role || "").replace(/^ROLE_/, "").toUpperCase();

        switch (role) {
            case "ADMIN":
                return "/admin";
            case "HOUSEKEEPING":
                return "/housekeeping";
            case "RECEPTIONIST":
                return "/receptionist";
            case "STAFF":
                return "/staff";
            case "CUSTOMER":
            default:
                return "/user";
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
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};
