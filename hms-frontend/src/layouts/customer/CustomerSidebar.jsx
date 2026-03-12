import { NavLink } from 'react-router-dom';
import { Stack, NavLink as MantineNavLink } from '@mantine/core';
import {
    IconDashboard,
    IconCalendarPlus,
    IconHistory,
    IconUser,
    IconHeart,
    IconStar,
    IconMessage
} from '@tabler/icons-react';

export function CustomerSidebar() {
    const navItems = [
        { label: 'Dashboard', icon: IconDashboard, to: '/customer' },
        { label: 'Book a Room', icon: IconCalendarPlus, to: '/customer/book' },
        { label: 'My Reservations', icon: IconHistory, to: '/customer/reservations' },
        { label: 'Favorites', icon: IconHeart, to: '/customer/favorites' },
        { label: 'Reviews', icon: IconStar, to: '/customer/reviews' },
        { label: 'Messages', icon: IconMessage, to: '/customer/messages' },
        { label: 'Profile', icon: IconUser, to: '/customer/profile' },
    ];

    return (
        <Stack>
            {navItems.map((item) => (
                <MantineNavLink
                    key={item.to}
                    component={NavLink}
                    to={item.to}
                    label={item.label}
                    leftSection={<item.icon size={16} />}
                    variant="light"
                    active={(location) => location.pathname === item.to}
                />
            ))}
        </Stack>
    );
}