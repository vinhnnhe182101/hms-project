// src/layouts/housekeeping/components/HousekeepingHeader.jsx
import { Group, Title, Avatar, Menu, Text, Badge, Indicator } from '@mantine/core';
import { IconBell, IconChecklist, IconUser, IconLogout } from '@tabler/icons-react';
import { useAuth } from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import { useHousekeepingTasks } from '../../hooks/useHousekeepingTasks';

export function HousekeepingHeader() {
    const navigate = useNavigate();
    const { user, logout } = useAuth();
    const { counts } = useHousekeepingTasks();

    return (
        <Group h="100%" px="sm" justify="space-between" wrap="nowrap">
            {/* Logo và Title */}
            <Group gap="xs" wrap="nowrap">
                <Title order={3} size="h4">🧹 HMS</Title>
                <Badge color="blue" size="sm" visibleFrom="xs">
                    Housekeeping
                </Badge>
            </Group>

            {/* Right Section */}
            <Group gap="xs" wrap="nowrap">
                {/* Notification Bell with Task Count */}
                <Indicator
                    label={counts.scheduled + counts.inProgress}
                    size={16}
                    color="red"
                    disabled={counts.scheduled + counts.inProgress === 0}
                >
                    <IconBell size={20} stroke={1.5} />
                </Indicator>

                {/* User Menu */}
                <Menu shadow="md" width={200} position="bottom-end">
                    <Menu.Target>
                        <Avatar
                            size="sm"
                            radius="xl"
                            color="blue"
                            style={{ cursor: 'pointer' }}
                        >
                            {user?.fullName?.charAt(0)}
                        </Avatar>
                    </Menu.Target>

                    <Menu.Dropdown>
                        <Menu.Label>
                            <Text size="sm" fw={500}>{user?.fullName}</Text>
                            <Text size="xs" c="dimmed">{user?.email}</Text>
                        </Menu.Label>

                        <Menu.Divider />

                        <Menu.Item
                            leftSection={<IconChecklist size={14} />}
                            onClick={() => navigate('/housekeeping/tasks')}
                        >
                            My Tasks ({counts.total})
                        </Menu.Item>

                        <Menu.Item
                            leftSection={<IconUser size={14} />}
                            onClick={() => navigate('/housekeeping/profile')}
                        >
                            Profile
                        </Menu.Item>

                        <Menu.Divider />

                        <Menu.Item
                            color="red"
                            leftSection={<IconLogout size={14} />}
                            onClick={logout}
                        >
                            Logout
                        </Menu.Item>
                    </Menu.Dropdown>
                </Menu>
            </Group>
        </Group>
    );
}