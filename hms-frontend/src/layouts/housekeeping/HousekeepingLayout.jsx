import { Outlet } from 'react-router-dom';
import { AppShell, Container } from '@mantine/core';
import { HousekeepingHeader } from './HousekeepingHeader';
import { HousekeepingSidebar } from './HousekeepingSidebar';
import { useAuth } from '../../hooks/useAuth';
import { Navigate } from 'react-router-dom';

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
            header={{ height: 70 }}
            navbar={{ width: 300, breakpoint: 'sm' }}
            padding="md"
        >
            <AppShell.Header>
                <HousekeepingHeader />
            </AppShell.Header>
            <AppShell.Navbar p="md">
                <HousekeepingSidebar />
            </AppShell.Navbar>
            <AppShell.Main>
                <Container size="lg" py="xl">
                    <Outlet />
                </Container>
            </AppShell.Main>
        </AppShell>
    );
}