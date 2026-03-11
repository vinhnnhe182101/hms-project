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
        { label: 'Phòng', icon: IconHotelService, to: '/admin/rooms' },
        { label: 'Đặt phòng', icon: IconCalendarStats, to: '/admin/reservations' },
        { label: 'Khách hàng', icon: IconUsers, to: '/admin/customers' },
        { label: 'Nhân viên', icon: IconUsers, to: '/admin/staff' },
        { label: 'Thanh toán', icon: IconCoin, to: '/admin/payments' },
        { label: 'Báo cáo', icon: IconReport, to: '/admin/reports' },
        { label: 'Cài đặt', icon: IconSettings, to: '/admin/settings' },
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
