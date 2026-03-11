import { Group, Button, Text, Box, Menu, Avatar } from '@mantine/core';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export default function Header() {
    const navigate = useNavigate();
    const location = useLocation();
    const { user, logout } = useAuth();

    const isActive = (path) => {
        return location.pathname === path;
    };

    const menuItems = [
        { label: 'Trang chủ', path: '/' },
        { label: 'Phòng & Suites', path: '/rooms' },
        { label: 'Dịch vụ', path: '/services' },
        { label: 'Lịch sử', path: '/history' }
    ];

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <header style={{
            backgroundColor: 'var(--mantine-color-white)',
            borderBottom: '1px solid var(--mantine-color-gray-2)',
            padding: 'var(--mantine-spacing-md) 0'
        }}>
            <Box style={{
                padding: '0 var(--mantine-spacing-xl)',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
            }}>
                <Text
                    fw={900}
                    style={{
                        cursor: 'pointer',
                        fontSize: '24px',
                        letterSpacing: '1px',
                        color: 'var(--mantine-color-teal-6)'
                    }}
                    onClick={() => navigate('/')}
                >
                    ROYAL HOTEL
                </Text>

                <Group gap={40}>
                    {menuItems.map((item) => (
                        <Text
                            key={item.path}
                            fw={isActive(item.path) ? 700 : 500}
                            style={{
                                cursor: 'pointer',
                                color: isActive(item.path) ? 'var(--mantine-color-teal-6)' : 'var(--mantine-color-gray-7)',
                                fontSize: 'var(--mantine-font-size-md)',
                                transition: 'all 0.2s ease',
                                borderBottom: isActive(item.path) ? '2px solid var(--mantine-color-teal-6)' : '2px solid transparent',
                                paddingBottom: '4px'
                            }}
                            onClick={() => navigate(item.path)}
                            onMouseEnter={(e) => {
                                if (!isActive(item.path)) {
                                    e.currentTarget.style.color = 'var(--mantine-color-teal-6)';
                                }
                            }}
                            onMouseLeave={(e) => {
                                if (!isActive(item.path)) {
                                    e.currentTarget.style.color = 'var(--mantine-color-gray-7)';
                                }
                            }}
                        >
                            {item.label}
                        </Text>
                    ))}
                </Group>

                {user ? (
                    <Menu shadow="md" width={180} position="bottom-end">
                        <Menu.Target>
                            <Group gap={10} style={{ cursor: 'pointer' }}>
                                <Avatar
                                    color="teal"
                                    radius="xl"
                                    size="sm"
                                >
                                    {user.fullName?.charAt(0)?.toUpperCase() || 'K'}
                                </Avatar>
                                <Text fw={600} size="sm" style={{ color: 'var(--mantine-color-gray-8)', maxWidth: 120 }} lineClamp={1}>
                                    {user.fullName}
                                </Text>
                            </Group>
                        </Menu.Target>
                        <Menu.Dropdown>
                            <Menu.Label>Tài khoản</Menu.Label>
                            <Menu.Item onClick={handleLogout} color="red">
                                Đăng xuất
                            </Menu.Item>
                        </Menu.Dropdown>
                    </Menu>
                ) : (
                    <Button
                        id="header-login-btn"
                        color="teal"
                        size="md"
                        onClick={() => navigate('/login')}
                    >
                        Đăng nhập
                    </Button>
                )}
            </Box>
        </header>
    );
}
