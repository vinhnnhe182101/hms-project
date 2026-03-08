import { Group, Button, Text, Box, Menu, Avatar } from '@mantine/core';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Header() {
    const navigate = useNavigate();
    const location = useLocation();
    const { customer, logout } = useAuth();

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
            backgroundColor: '#fff',
            borderBottom: '1px solid #e9ecef',
            padding: '24px 0'
        }}>
            <Box style={{
                padding: '0 20px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
            }}>
                <Text
                    fw={900}
                    style={{
                        cursor: 'pointer',
                        fontSize: '24px',
                        letterSpacing: '0.5px'
                    }}
                    onClick={() => navigate('/')}
                >
                    ROYAL HOTEL
                </Text>

                <Group gap={50}>
                    {menuItems.map((item) => (
                        <Text
                            key={item.path}
                            fw={isActive(item.path) ? 700 : 500}
                            style={{
                                cursor: 'pointer',
                                color: isActive(item.path) ? '#D4A574' : '#666',
                                fontSize: '16px',
                                transition: 'all 0.2s ease'
                            }}
                            onClick={() => navigate(item.path)}
                            onMouseEnter={(e) => {
                                if (!isActive(item.path)) {
                                    e.currentTarget.style.color = '#D4A574';
                                }
                            }}
                            onMouseLeave={(e) => {
                                if (!isActive(item.path)) {
                                    e.currentTarget.style.color = '#666';
                                }
                            }}
                        >
                            {item.label}
                        </Text>
                    ))}
                </Group>

                {customer ? (
                    <Menu shadow="md" width={180} position="bottom-end">
                        <Menu.Target>
                            <Group gap={10} style={{ cursor: 'pointer' }}>
                                <Avatar
                                    color="orange"
                                    radius="xl"
                                    size="sm"
                                    style={{ backgroundColor: '#D4A574' }}
                                >
                                    {customer.fullName?.charAt(0)?.toUpperCase() || 'K'}
                                </Avatar>
                                <Text fw={600} size="sm" style={{ color: '#333', maxWidth: 120 }} lineClamp={1}>
                                    {customer.fullName}
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
                        style={{
                            backgroundColor: '#D4A574',
                            fontSize: '15px',
                            padding: '8px 20px',
                            height: 'auto',
                            fontWeight: 500
                        }}
                        radius="md"
                        onClick={() => navigate('/login')}
                    >
                        Đăng nhập
                    </Button>
                )}
            </Box>
        </header>
    );
}
