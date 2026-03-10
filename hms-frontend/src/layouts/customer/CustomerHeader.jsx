import { useState } from 'react';
import {
    Container, Group, Title, Button, Menu, Avatar,
    Text, Divider, Burger, Drawer, Stack
} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import {
    IconUser, IconLogout, IconDashboard, IconCalendarPlus,
    IconHotelService, IconHome, IconPhone, IconInfoCircle
} from '@tabler/icons-react';
import { useAuth } from '../../hooks/useAuth';
import { useNavigate, useLocation } from 'react-router-dom';

export function CustomerHeader() {
    const navigate = useNavigate();
    const location = useLocation();
    const { isAuthenticated, user, logout } = useAuth();
    const [drawerOpened, { toggle: toggleDrawer, close: closeDrawer }] = useDisclosure(false);

    const handleLogout = async () => {
        console.log('Logout button clicked');
        logout();
    };

    const navItems = [
        { label: 'Home', icon: IconHome, path: '/' },
        { label: 'Rooms', icon: IconHotelService, path: '/rooms' },
        { label: 'About', icon: IconInfoCircle, path: '/about' },
        { label: 'Contact', icon: IconPhone, path: '/contact' },
    ];

    const isActivePath = (path) => {
        if (path === '/') return location.pathname === '/';
        return location.pathname.startsWith(path);
    };

    return (
        <>
            {/* Desktop Header */}
            <Container size="lg" style={{ height: '100%' }}>
                <Group justify="space-between" style={{ height: '100%' }}>
                    {/* Logo */}
                    <Group>
                        <Title
                            order={2}
                            style={{ cursor: 'pointer' }}
                            onClick={() => navigate('/')}
                        >
                            HMS Hotel
                        </Title>
                    </Group>

                    {/* Desktop Navigation */}
                    <Group visibleFrom="sm" gap="xl">
                        {navItems.map((item) => (
                            <Button
                                key={item.path}
                                variant={isActivePath(item.path) ? "light" : "subtle"}
                                leftSection={<item.icon size={18} />}
                                onClick={() => {
                                    navigate(item.path);
                                    closeDrawer();
                                }}
                            >
                                {item.label}
                            </Button>
                        ))}
                    </Group>

                    {/* Auth Buttons / User Menu */}
                    <Group visibleFrom="sm">
                        {isAuthenticated ? (
                            <Menu shadow="md" width={240}>
                                <Menu.Target>
                                    <Group style={{ cursor: 'pointer' }}>
                                        <Avatar color="blue" radius="xl">
                                            {user?.fullName?.charAt(0)}
                                        </Avatar>
                                        <div style={{ flex: 1 }}>
                                            <Text size="sm" fw={500}>
                                                {user?.fullName}
                                            </Text>
                                            <Text size="xs" c="dimmed">
                                                {user?.role}
                                            </Text>
                                        </div>
                                    </Group>
                                </Menu.Target>

                                <Menu.Dropdown>
                                    <Menu.Label>Navigation</Menu.Label>
                                    <Menu.Item
                                        leftSection={<IconDashboard size={14} />}
                                        onClick={() => navigate('/customer')}
                                    >
                                        Dashboard
                                    </Menu.Item>
                                    <Menu.Item
                                        leftSection={<IconCalendarPlus size={14} />}
                                        onClick={() => navigate('/booking')}
                                    >
                                        Book a Room
                                    </Menu.Item>

                                    <Divider />

                                    <Menu.Label>Account</Menu.Label>
                                    <Menu.Item
                                        leftSection={<IconUser size={14} />}
                                        onClick={() => navigate('/customer/profile')}
                                    >
                                        Profile
                                    </Menu.Item>
                                    <Menu.Item
                                        color="red"
                                        leftSection={<IconLogout size={14} />}
                                        onClick={handleLogout}
                                    >
                                        Logout
                                    </Menu.Item>
                                </Menu.Dropdown>
                            </Menu>
                        ) : (
                            <Group>
                                <Button variant="light" onClick={() => navigate('/auth/login')}>
                                    Login
                                </Button>
                                <Button onClick={() => navigate('/auth/register')}>
                                    Sign Up
                                </Button>
                            </Group>
                        )}
                    </Group>

                    {/* Mobile Burger */}
                    <Burger
                        opened={drawerOpened}
                        onClick={toggleDrawer}
                        hiddenFrom="sm"
                    />
                </Group>
            </Container>

            {/* Mobile Drawer */}
            <Drawer
                opened={drawerOpened}
                onClose={closeDrawer}
                size="100%"
                padding="md"
                title="Menu"
                hiddenFrom="sm"
                zIndex={1000000}
            >
                <Stack>
                    {navItems.map((item) => (
                        <Button
                            key={item.path}
                            variant={isActivePath(item.path) ? "light" : "subtle"}
                            leftSection={<item.icon size={18} />}
                            onClick={() => {
                                navigate(item.path);
                                closeDrawer();
                            }}
                            fullWidth
                            justify="start"
                        >
                            {item.label}
                        </Button>
                    ))}

                    <Divider />

                    {isAuthenticated ? (
                        <>
                            <Button
                                variant="light"
                                leftSection={<IconDashboard size={18} />}
                                onClick={() => {
                                    navigate('/customer');
                                    closeDrawer();
                                }}
                                fullWidth
                                justify="start"
                            >
                                Dashboard
                            </Button>
                            <Button
                                variant="light"
                                leftSection={<IconUser size={18} />}
                                onClick={() => {
                                    navigate('/customer/profile');
                                    closeDrawer();
                                }}
                                fullWidth
                                justify="start"
                            >
                                Profile
                            </Button>
                            <Button
                                color="red"
                                leftSection={<IconLogout size={18} />}
                                onClick={() => {
                                    handleLogout();
                                    closeDrawer();
                                }}
                                fullWidth
                                justify="start"
                            >
                                Logout
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button
                                variant="light"
                                onClick={() => {
                                    navigate('/login');
                                    closeDrawer();
                                }}
                                fullWidth
                            >
                                Login
                            </Button>
                            <Button
                                onClick={() => {
                                    navigate('/register');
                                    closeDrawer();
                                }}
                                fullWidth
                            >
                                Sign Up
                            </Button>
                        </>
                    )}
                </Stack>
            </Drawer>
        </>
    );
}