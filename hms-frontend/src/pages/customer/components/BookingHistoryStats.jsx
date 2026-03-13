// src/pages/customer/components/BookingHistoryStats.jsx
import { SimpleGrid, Paper, Group, Text, ThemeIcon } from '@mantine/core';
import {
    IconCalendarStats,
    IconCheck,
    IconX,
    IconClock
} from '@tabler/icons-react';

export function BookingHistoryStats({ bookings }) {
    const stats = {
        total: bookings.length,
        confirmed: bookings.filter(b => b.status?.toLowerCase() === 'confirmed').length,
        completed: bookings.filter(b => b.status?.toLowerCase() === 'completed').length,
        cancelled: bookings.filter(b => b.status?.toLowerCase() === 'cancelled').length,
        totalSpent: bookings.reduce((sum, b) => sum + (b.totalPrice || 0), 0)
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND',
            notation: 'compact',
            compactDisplay: 'short'
        }).format(amount);
    };

    const statCards = [
        {
            label: 'Total Bookings',
            value: stats.total,
            icon: IconCalendarStats,
            color: 'blue'
        },
        {
            label: 'Confirmed',
            value: stats.confirmed,
            icon: IconCheck,
            color: 'green'
        },
        {
            label: 'Completed',
            value: stats.completed,
            icon: IconClock,
            color: 'teal'
        },
        {
            label: 'Cancelled',
            value: stats.cancelled,
            icon: IconX,
            color: 'red'
        }
    ];

    return (
        <SimpleGrid cols={{ base: 2, sm: 4 }} spacing="md">
            {statCards.map((stat) => (
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
                        <ThemeIcon color={stat.color} variant="light" size="xl" radius="md">
                            <stat.icon size={24} />
                        </ThemeIcon>
                    </Group>
                </Paper>
            ))}
        </SimpleGrid>
    );
}