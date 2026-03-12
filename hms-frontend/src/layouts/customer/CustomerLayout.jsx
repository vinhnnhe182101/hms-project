import { Outlet, useNavigate } from 'react-router-dom';
import { AppShell, Container } from '@mantine/core';
import { CustomerHeader } from './CustomerHeader';
import { CustomerFooter } from './CustomerFooter';
import { useAuth } from '../../hooks/useAuth';

export function CustomerLayout() {
    const { isAuthenticated } = useAuth();

    return (
        <AppShell
            header={{ height: 70 }}
            footer={{ height: 60 }}
            padding={0}
        >
            <AppShell.Header>
                <CustomerHeader />
            </AppShell.Header>

            <AppShell.Main>
                <Container size="lg" py="xl">
                    <Outlet />
                </Container>
            </AppShell.Main>

            <AppShell.Footer>
                <CustomerFooter />
            </AppShell.Footer>
        </AppShell>
    );
}