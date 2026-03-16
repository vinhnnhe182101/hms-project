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
        { label: 'Dashboard', icon: IconDashboard, to: '/user' },
        { label: 'Book a Room', icon: IconCalendarPlus, to: '/user/booking' },
        { label: 'My Reservations', icon: IconHistory, to: '/user/history' },
        { label: 'Profile', icon: IconUser, to: '/user/profile' },
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