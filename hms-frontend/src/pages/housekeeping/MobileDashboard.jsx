// src/pages/housekeeping/MobileDashboard.jsx
import {Stack, Paper, Text, Group, SimpleGrid, Progress, ThemeIcon, Button} from '@mantine/core';
import {
    IconChecklist,
    IconClock,
    IconCircleCheck,
    IconAlertCircle,
    IconDoor,
    IconArrowRight
} from '@tabler/icons-react';
import { useHousekeepingTasks } from '../../hooks/useHousekeepingTasks';
import { useNavigate } from 'react-router-dom';

export default function MobileDashboard() {
    const navigate = useNavigate();
    const { counts, todayTasks } = useHousekeepingTasks();

    const stats = [
        {
            label: 'Total',
            value: counts.total,
            icon: IconChecklist,
            color: 'blue',
            bg: 'blue.0'
        },
        {
            label: 'Pending',
            value: counts.scheduled + counts.inProgress,
            icon: IconClock,
            color: 'yellow',
            bg: 'yellow.0'
        },
        {
            label: 'In Progress',
            value: counts.inProgress,
            icon: IconAlertCircle,
            color: 'orange',
            bg: 'orange.0'
        },
        {
            label: 'Completed',
            value: counts.completed,
            icon: IconCircleCheck,
            color: 'green',
            bg: 'green.0'
        }
    ];

    return (
        <Stack style={{ paddingBottom: 80 }}>
            {/* Welcome Message */}
            <Paper p="md" withBorder>
                <Text size="xl" fw={700}>Good morning! 👋</Text>
                <Text size="sm" c="dimmed">Here's your task summary for today</Text>
            </Paper>

            {/* Stats Grid */}
            <SimpleGrid cols={2} spacing="sm">
                {stats.map((stat) => (
                    <Paper
                        key={stat.label}
                        p="md"
                        withBorder
                        style={{ backgroundColor: stat.bg }}
                    >
                        <Group justify="space-between" mb="xs">
                            <Text size="sm" c="dimmed">{stat.label}</Text>
                            <ThemeIcon color={stat.color} size="sm" radius="xl">
                                <stat.icon size={12} />
                            </ThemeIcon>
                        </Group>
                        <Text fw={700} size="xl">{stat.value}</Text>
                        <Progress
                            value={(stat.value / counts.total) * 100}
                            color={stat.color}
                            size="sm"
                            mt="xs"
                        />
                    </Paper>
                ))}
            </SimpleGrid>

            {/* Today's Tasks Preview */}
            <Paper p="md" withBorder>
                <Group justify="space-between" mb="md">
                    <Text fw={600}>Today's Tasks</Text>
                    <Text size="sm" c="dimmed">{todayTasks.length} tasks</Text>
                </Group>

                <Stack gap="xs">
                    {todayTasks.slice(0, 3).map((task) => (
                        <Group key={task.id} justify="space-between">
                            <Group>
                                <IconDoor size={16} />
                                <Text size="sm">Room {task.roomNumber}</Text>
                            </Group>
                            <Text size="sm" c="dimmed">{task.taskTypeDisplay}</Text>
                        </Group>
                    ))}
                </Stack>

                {todayTasks.length > 3 && (
                    <Button
                        variant="subtle"
                        fullWidth
                        mt="md"
                        rightSection={<IconArrowRight size={16} />}
                        onClick={() => navigate('/housekeeping/tasks')}
                    >
                        View all tasks
                    </Button>
                )}
            </Paper>

            {/* Quick Actions */}
            <SimpleGrid cols={2} spacing="sm">
                <Button
                    variant="light"
                    h={80}
                    onClick={() => navigate('/housekeeping/tasks')}
                    styles={{
                        label: {
                            flexDirection: 'column',
                            gap: 8
                        }
                    }}
                >
                    <IconChecklist size={24} />
                    <Text size="sm">My Tasks</Text>
                </Button>

                <Button
                    variant="light"
                    h={80}
                    onClick={() => navigate('/housekeeping/schedule')}
                    styles={{
                        label: {
                            flexDirection: 'column',
                            gap: 8
                        }
                    }}
                >
                    <IconClock size={24} />
                    <Text size="sm">Schedule</Text>
                </Button>
            </SimpleGrid>
        </Stack>
    );
}