import {Title, Paper, Grid, Card, Text, SimpleGrid, Group} from '@mantine/core';
import {
    IconHotelService,
    IconUsers,
    IconCalendarStats,
    IconCurrencyDollar
} from '@tabler/icons-react';

export default function AdminDashboardPage() {
    const stats = [
        { title: 'Total Rooms', value: '150', icon: IconHotelService, color: 'blue' },
        { title: 'Occupied Rooms', value: '45', icon: IconHotelService, color: 'red' },
        { title: 'Check-ins Today', value: '12', icon: IconCalendarStats, color: 'green' },
        { title: 'Revenue Today', value: '$5,230', icon: IconCurrencyDollar, color: 'violet' },
        { title: 'Total Guests', value: '89', icon: IconUsers, color: 'cyan' },
        { title: 'Pending Reservations', value: '8', icon: IconCalendarStats, color: 'orange' },
    ];

    return (
        <div>
            <Title order={1} mb="lg">Admin Dashboard</Title>

            <SimpleGrid cols={{ base: 1, sm: 2, md: 3 }} spacing="md">
                {stats.map((stat) => (
                    <Card key={stat.title} shadow="sm" padding="lg" radius="md" withBorder>
                        <Group justify="space-between" mb="xs">
                            <Text size="xs" c="dimmed" tt="uppercase" fw={700}>
                                {stat.title}
                            </Text>
                            <stat.icon size={20} color={`var(--mantine-color-${stat.color}-6)`} />
                        </Group>
                        <Text size="xl" fw={700}>
                            {stat.value}
                        </Text>
                    </Card>
                ))}
            </SimpleGrid>

            <Grid mt="lg">
                <Grid.Col span={8}>
                    <Paper shadow="sm" p="md" withBorder>
                        <Title order={3} mb="md">Recent Bookings</Title>
                        {/* Add bookings table here */}
                        <Text c="dimmed" ta="center" py="xl">
                            No recent bookings to display
                        </Text>
                    </Paper>
                </Grid.Col>

                <Grid.Col span={4}>
                    <Paper shadow="sm" p="md" withBorder>
                        <Title order={3} mb="md">Quick Actions</Title>
                        {/* Add quick actions here */}
                        <Text c="dimmed" ta="center" py="xl">
                            Quick actions will appear here
                        </Text>
                    </Paper>
                </Grid.Col>
            </Grid>
        </div>
    );
}