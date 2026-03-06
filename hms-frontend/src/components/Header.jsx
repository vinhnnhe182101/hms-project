import { Group, Button, Text, Box } from '@mantine/core';
import { useNavigate, useLocation } from 'react-router-dom';

export default function Header() {
    const navigate = useNavigate();
    const location = useLocation();

    const isActive = (path) => {
        return location.pathname === path;
    };

    const menuItems = [
        { label: 'Trang chủ', path: '/' },
        { label: 'Phòng & Suites', path: '/rooms' },
        { label: 'Dịch vụ', path: '/services' },
        { label: 'Lịch sử', path: '/history' }
    ];

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

                <Button
                    style={{
                        backgroundColor: '#D4A574',
                        fontSize: '15px',
                        padding: '8px 20px',
                        height: 'auto',
                        fontWeight: 500
                    }}
                    radius="md"
                >
                    Đăng nhập
                </Button>
            </Box>
        </header>
    );
}
