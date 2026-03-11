import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Loader, Center, Stack, Text } from '@mantine/core';
import { notifications } from '@mantine/notifications';
import { jwtDecode } from 'jwt-decode';

export default function OAuth2RedirectPage() {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const handleRedirect = async () => {
            try {
                const params = new URLSearchParams(location.search);
                const token = params.get('token');
                const error = params.get('error');

                console.log('OAuth2 - Token received:', !!token);

                if (error) {
                    notifications.show({
                        title: 'Error',
                        message: decodeURIComponent(error),
                        color: 'red',
                    });
                    navigate('/login', { replace: true });
                    return;
                }

                if (!token) {
                    notifications.show({
                        title: 'Error',
                        message: 'No token received',
                        color: 'red',
                    });
                    navigate('/login', { replace: true });
                    return;
                }

                const decodedToken = decodeURIComponent(token);
                // Save token in localStorage
                localStorage.setItem('accessToken', decodedToken);

                console.log('OAuth2 - Token saved, redirecting...');

                // You can also handle decoding here to check role if needed
                // const decoded = jwtDecode(decodedToken);
                // navigate(decoded.role === 'ADMIN' ? '/admin' : '/');
                
                // For now, simple redirect
                window.location.href = '/';

            } catch (error) {
                console.error('OAuth2 error:', error);
                notifications.show({
                    title: 'Error',
                    message: 'Authentication failed',
                    color: 'red',
                });
                navigate('/login', { replace: true });
            }
        };
        handleRedirect();
    }, [location.search, navigate]);

    return (
        <Center style={{ height: '100vh' }}>
            <Stack align="center">
                <Loader size="xl" color="teal" />
                <Text size="lg">Đang hoàn tất đăng nhập...</Text>
            </Stack>
        </Center>
    );
}
