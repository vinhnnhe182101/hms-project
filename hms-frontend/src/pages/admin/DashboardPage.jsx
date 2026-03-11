import { Title, Paper, Grid, Card, Text, SimpleGrid, Group } from '@mantine/core';
import {
    IconHotelService,
    IconUsers,
    IconCalendarStats,
    IconCurrencyDollar
} from '@tabler/icons-react';

export default function AdminDashboardPage() {
    const stats = [
        { title: 'Tổng số phòng', value: '150', icon: IconHotelService, color: 'blue' },
        { title: 'Phòng đang ở', value: '45', icon: IconHotelService, color: 'red' },
        { title: 'Check-in hôm nay', value: '12', icon: IconCalendarStats, color: 'green' },
        { title: 'Doanh thu hôm nay', value: '$5,230', icon: IconCurrencyDollar, color: 'violet' },
        { title: 'Khách hàng', value: '89', icon: IconUsers, color: 'cyan' },
        { title: 'Đặt phòng chờ', value: '8', icon: IconCalendarStats, color: 'orange' },
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
                        <Title order={3} mb="md">Đặt phòng gần đây</Title>
                        <Text c="dimmed" ta="center" py="xl">
                            Không có đặt phòng mới
                        </Text>
                    </Paper>
                </Grid.Col>

                <Grid.Col span={4}>
                    <Paper shadow="sm" p="md" withBorder>
                        <Title order={3} mb="md">Thao tác nhanh</Title>
                        <Text c="dimmed" ta="center" py="xl">
                            Các thao tác nhanh sẽ xuất hiện ở đây
                        </Text>
                    </Paper>
                </Grid.Col>
            </Grid>
        </div>
    );
}
