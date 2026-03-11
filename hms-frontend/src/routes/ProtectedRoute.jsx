import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Center, Loader } from '@mantine/core';

export function ProtectedRoute({ children, requiredRole }) {
    const { user, loading, isAuthenticated } = useAuth();
    const location = useLocation();

    if (loading) {
        return (
            <Center style={{ height: '100vh' }}>
                <Loader size="xl" color="teal" />
            </Center>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/auth/login" state={{ from: location.pathname }} replace />;
    }

    if (requiredRole && user?.role !== requiredRole) {
        return <Navigate to="/unauthorized" replace />;
    }

    return children;
}
