import { Outlet } from 'react-router-dom';
import { AppShell, Container, Box } from '@mantine/core';
import Header from './CustomerHeader.jsx';
import Footer from './CustomerFooter.jsx';
import { CustomerSidebar } from './CustomerSidebar.jsx';
import { useAuth } from '../../hooks/useAuth.jsx';

export default function CustomerLayout() {
    const { isAuthenticated } = useAuth();

    return (
        <AppShell
            header={{ height: 70 }}
            // navbar={isAuthenticated ? { width: 280, breakpoint: 'sm' } : undefined}
            padding={0}
        >
            <AppShell.Header>
                <Header />
            </AppShell.Header>

            {/* {isAuthenticated && (
                <AppShell.Navbar p="md">
                    <CustomerSidebar />
                </AppShell.Navbar>
            )} */}

            <AppShell.Main>
                <Box style={{ minHeight: 'calc(100vh - 70px)', display: 'flex', flexDirection: 'column' }}>
                    <Box style={{ flex: 1 }}>
                        <Outlet />
                    </Box>
                    <Footer />
                </Box>
            </AppShell.Main>
        </AppShell>
    );
}
