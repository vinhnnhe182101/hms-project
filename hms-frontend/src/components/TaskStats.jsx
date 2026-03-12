// src/pages/housekeeping/components/TaskStats.jsx
import React from 'react';
import { SimpleGrid, Paper, Text, Group } from '@mantine/core';
import { IconChecklist, IconClock, IconCircleCheck, IconAlertCircle } from '@tabler/icons-react';

export function TaskStats({ counts }) {
    const stats = [
        {
            label: 'Total',
            value: counts.total,
            icon: IconChecklist,
            color: 'blue'
        },
        {
            label: 'Scheduled',
            value: counts.scheduled,
            icon: IconClock,
            color: 'gray'
        },
        {
            label: 'In Progress',
            value: counts.inProgress,
            icon: IconAlertCircle,
            color: 'yellow'
        },
        {
            label: 'Completed',
            value: counts.completed,
            icon: IconCircleCheck,
            color: 'green'
        }
    ];

    return (
        <SimpleGrid cols={{ base: 2, sm: 4 }} spacing="md">
            {stats.map((stat) => (
                <Paper key={stat.label} withBorder p="md" radius="md">
                    <Group justify="space-between">
                        <div>
                            <Text size="xs" c="dimmed" tt="uppercase" fw={700}>
                                {stat.label}
                            </Text>
                            <Text fw={700} size="xl">
                                {stat.value}
                            </Text>
                        </div>
                        <stat.icon size={32} color={`var(--mantine-color-${stat.color}-6)`} />
                    </Group>
                </Paper>
            ))}
        </SimpleGrid>
    );
}