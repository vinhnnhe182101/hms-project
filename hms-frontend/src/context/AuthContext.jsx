import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [customer, setCustomer] = useState(() => {
        try {
            const stored = localStorage.getItem('hms_customer');
            return stored ? JSON.parse(stored) : null;
        } catch {
            return null;
        }
    });

    const saveCustomer = (customerData) => {
        setCustomer(customerData);
        localStorage.setItem('hms_customer', JSON.stringify(customerData));
    };

    const logout = () => {
        setCustomer(null);
        localStorage.removeItem('hms_customer');
    };

    return (
        <AuthContext.Provider value={{ customer, saveCustomer, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
