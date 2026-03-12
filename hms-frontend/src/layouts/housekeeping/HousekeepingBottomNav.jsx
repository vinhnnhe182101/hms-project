// src/layouts/housekeeping/components/HousekeepingBottomNav.jsx
import { useNavigate, useLocation } from 'react-router-dom';
import { Paper, Group, ActionIcon, Badge, Text } from '@mantine/core';
import {
    IconHome,
    IconChecklist,
    IconCalendar,
    IconClipboardList,
    IconUser
} from '@tabler/icons-react';
import { useHousekeepingTasks } from '../../hooks/useHousekeepingTasks';

export function HousekeepingBottomNav() {
    const navigate = useNavigate();
    const location = useLocation();
    const { counts } = useHousekeepingTasks();

    const navItems = [
        {
            path: '/housekeeping',
            icon: IconHome,
            label: 'Home',
            badge: null
        },
        {
            path: '/housekeeping/tasks',
            icon: IconChecklist,
            label: 'Tasks',
            badge: counts.scheduled + counts.inProgress
        },
        {
            path: '/housekeeping/schedule',
            icon: IconCalendar,
            label: 'Schedule',
            badge: null
        },
        {
            path: '/housekeeping/reports',
            icon: IconClipboardList,
            label: 'Reports',
            badge: null
        },
        {
            path: '/housekeeping/profile',
            icon: IconUser,
            label: 'Profile',
            badge: null
        },
    ];

    const isActive = (path) => {
        if (path === '/housekeeping') {
            return location.pathname === path;
        }
        return location.pathname.startsWith(path);
    };

    return (
        <Paper
            withBorder
            radius={0}
            style={{
                position: 'fixed',
                bottom: 0,
                left: 0,
                right: 0,
                zIndex: 100,
                backgroundColor: 'white'
            }}
        >
            <Group justify="space-around" h={70} gap={0}>
                {navItems.map((item) => (
                    <ActionIcon
                        key={item.path}
                        variant="transparent"
                        size="xl"
                        radius="md"
                        style={{
                            flex: 1,
                            height: '100%',
                            display: 'flex',
                            flexDirection: 'column',
                            gap: 4,
                            color: isActive(item.path)
                                ? 'var(--mantine-color-blue-6)'
                                : 'var(--mantine-color-gray-6)'
                        }}
                        onClick={() => navigate(item.path)}
                    >
                        <div style={{ position: 'relative' }}>
                            <item.icon size={24} stroke={1.5} />
                            {item.badge > 0 && (
                                <Badge
                                    size="xs"
                                    color="red"
                                    style={{
                                        position: 'absolute',
                                        top: -8,
                                        right: -8,
                                        minWidth: 18,
                                        height: 18,
                                        padding: 0
                                    }}
                                >
                                    {item.badge}
                                </Badge>
                            )}
                        </div>
                        <Text size="xs" fw={500}>
                            {item.label}
                        </Text>
                    </ActionIcon>
                ))}
            </Group>
        </Paper>
    );
}