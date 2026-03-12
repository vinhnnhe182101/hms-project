import { Outlet } from 'react-router-dom';
import { Container} from '@mantine/core';

export function AuthLayout() {
    return (
            <Container size="sm" style={{ width: '100%', marginTop: '5rem' }}>
                    <Outlet />
            </Container>
    );
}