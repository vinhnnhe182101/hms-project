import { Group, Title, Burger, Avatar, Menu, Text } from '@mantine/core';
import { IconLogout, IconUser, IconClipboard, IconCalendar, IconBrush } from '@tabler/icons-react';
import { useAuth } from '../../hooks/useAuth.jsx';

export function HousekeepingHeader({ opened, toggle }) {
    const { user, logout } = useAuth();

    return (
        <Group h="100%" px="md" justify="space-between">
            <Group>
                <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
                <Title order={3}>HMS Housekeeping</Title>
            </Group>

            <Menu shadow="md" width={200}>
                <Menu.Target>
                    <Group style={{ cursor: 'pointer' }}>
                        <Avatar color="orange" radius="xl">
                            {user?.staff?.fullName?.charAt(0) || 'H'}
                        </Avatar>
                        <div style={{ flex: 1 }}>
                            <Text size="sm" fw={500}>
                                {user?.staff?.fullName || 'Staff'}
                            </Text>
                            <Text size="xs" c="dimmed">
                                {user?.email}
                            </Text>
                        </div>
                    </Group>
                </Menu.Target>

                <Menu.Dropdown>
                    <Menu.Item leftSection={<IconUser size={14} />}>
                        Profile
                    </Menu.Item>
                    <Menu.Item leftSection={<IconClipboard size={14} />}>
                        My Tasks
                    </Menu.Item>
                    <Menu.Item leftSection={<IconCalendar size={14} />}>
                        Schedule
                    </Menu.Item>
                    <Menu.Item leftSection={<IconBrush size={14} />}>
                        Cleaning Status
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
    );
}