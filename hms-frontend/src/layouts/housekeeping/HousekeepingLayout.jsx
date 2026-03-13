import { Outlet } from 'react-router-dom';
import { AppShell, Container } from '@mantine/core';
import { HousekeepingHeader } from './HousekeepingHeader';
import { useAuth } from '../../hooks/useAuth';
import { Navigate } from 'react-router-dom';
import {HousekeepingBottomNav} from "./HousekeepingBottomNav.jsx";

export function HousekeepingLayout() {
    const { user, isAuthenticated } = useAuth();

    console.log('HousekeepingLayout - user:', user);
    console.log('HousekeepingLayout - isAuthenticated:', isAuthenticated);

    if (!isAuthenticated) {
        console.log('HousekeepingLayout - Not authenticated, redirecting to login');
        return <Navigate to="/auth/login" replace />;
    }

    if (user?.role !== 'HOUSEKEEPING') {
        console.log(`HousekeepingLayout - Wrong role: ${user?.role}, redirecting to home`);
        return <Navigate to="/unauthorized" replace />;
    }

    return (
        <AppShell
            header={{ height: 60 }}
            footer={{ height: 70 }} // Thêm footer cho mobile navigation
            padding={0}
        >
            <AppShell.Header>
                <HousekeepingHeader />
            </AppShell.Header>

            <AppShell.Main style={{
                paddingBottom: '70px',
                minHeight: 'calc(100vh - 130px)'
            }}>
                <Container size="lg" px="sm" py="md">
                    <Outlet />
                </Container>
            </AppShell.Main>

            <AppShell.Footer>
                <HousekeepingBottomNav />
            </AppShell.Footer>
        </AppShell>
    );
}