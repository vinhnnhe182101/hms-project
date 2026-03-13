import { NavLink } from 'react-router-dom';
import { Stack, NavLink as MantineNavLink } from '@mantine/core';
import {
    IconDashboard,
    IconHotelService,
    IconUsers,
    IconCalendarStats,
    IconSettings,
    IconReport,
    IconCoin
} from '@tabler/icons-react';

export function AdminSidebar() {
    const navItems = [
        { label: 'Dashboard', icon: IconDashboard, to: '/admin' },
        { label: 'Rooms', icon: IconHotelService, to: '/admin/rooms' },
        { label: 'Reservations', icon: IconCalendarStats, to: '/admin/reservations' },
        { label: 'Customers', icon: IconUsers, to: '/admin/customers' },
        { label: 'Staff', icon: IconUsers, to: '/admin/staff' },
        { label: 'Payments', icon: IconCoin, to: '/admin/payments' },
        { label: 'Reports', icon: IconReport, to: '/admin/reports' },
        { label: 'Settings', icon: IconSettings, to: '/admin/settings' },
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