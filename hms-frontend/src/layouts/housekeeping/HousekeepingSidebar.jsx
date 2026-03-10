import { NavLink as RouterNavLink, useLocation } from 'react-router-dom';
import { Stack, NavLink as MantineNavLink } from '@mantine/core';
import {
    IconDashboard,
    IconClipboardList,
    IconCalendarStats,
    IconReport,
    IconBrush,
    IconAlertCircle,
    IconAsset
} from '@tabler/icons-react';

export function HousekeepingSidebar() {
    const location = useLocation();

    const navItems = [
        { label: 'Dashboard', icon: IconDashboard, path: '/housekeeping' },
        { label: 'Tasks', icon: IconClipboardList, path: '/housekeeping/tasks' },
        { label: 'Schedule', icon: IconCalendarStats, path: '/housekeeping/schedule' },
        { label: 'Cleaning Status', icon: IconBrush, path: '/housekeeping/cleaning' },
        { label: 'Maintenance', icon: IconAlertCircle, path: '/housekeeping/maintenance' },
        { label: 'Inventory', icon: IconAsset, path: '/housekeeping/inventory' },
        { label: 'Reports', icon: IconReport, path: '/housekeeping/reports' },
    ];

    // Cách 1: Dùng useLocation để xác định active
    const isActive = (path) => {
        if (path === '/housekeeping') {
            return location.pathname === path;
        }
        return location.pathname.startsWith(path);
    };

    return (
        <Stack>
            {navItems.map((item) => (
                <MantineNavLink
                    key={item.path}
                    component={RouterNavLink}
                    to={item.path}
                    label={item.label}
                    leftSection={<item.icon size={16} />}
                    variant="light"
                    active={isActive(item.path)} // Dùng useLocation thay vì data-active
                />
            ))}
        </Stack>
    );
}