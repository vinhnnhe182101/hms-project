import { Title, Paper, Grid, Card, Text, Group, Badge, Progress, SimpleGrid, Button } from '@mantine/core';
import { IconBrush, IconChecklist, IconClock, IconAlertCircle } from '@tabler/icons-react';
import { useAuth } from '../../hooks/useAuth';

export default function HousekeepingDashboardPage() {
    const { user } = useAuth();

    const stats = [
        { title: 'Công việc hôm nay', value: '24', icon: IconChecklist, color: 'blue', progress: 45 },
        { title: 'Đã hoàn thành', value: '11', icon: IconBrush, color: 'green', progress: 100 },
        { title: 'Đang thực hiện', value: '8', icon: IconClock, color: 'yellow', progress: 60 },
        { title: 'Ưu tiên cao', value: '3', icon: IconAlertCircle, color: 'red', progress: 30 },
    ];

    const todayTasks = [
        { room: '101', task: 'Dọn dẹp sâu', priority: 'Cao', status: 'Chờ', time: '09:00 AM' },
        { room: '102', task: 'Dọn dẹp thường', priority: 'Thường', status: 'Đang làm', time: '10:30 AM' },
        { room: '103', task: 'Kiểm tra', priority: 'Thường', status: 'Chờ', time: '11:00 AM' },
        { room: '201', task: 'Bảo trì', priority: 'Thấp', status: 'Đã lên lịch', time: '02:00 PM' },
        { room: '202', task: 'Dọn dẹp sâu', priority: 'Cao', status: 'Chờ', time: '03:30 PM' },
    ];

    const roomStatus = [
        { floor: 'Tầng 1', clean: 8, dirty: 3, occupied: 5, total: 12 },
        { floor: 'Tầng 2', clean: 6, dirty: 4, occupied: 7, total: 12 },
        { floor: 'Tầng 3', clean: 9, dirty: 2, occupied: 4, total: 12 },
    ];

    return (
        <div>
            <Group mb="lg">
                <div>
                    <Title order={1}>Bảng điều khiển Buồng phòng</Title>
                    <Text c="dimmed">Chào mừng trở lại, {user?.fullName}</Text>
                </div>
            </Group>

            <SimpleGrid cols={{ base: 1, sm: 2, md: 4 }} spacing="md" mb="xl">
                {stats.map((stat) => (
                    <Card key={stat.title} shadow="sm" padding="lg" radius="md" withBorder>
                        <Group justify="space-between" mb="xs">
                            <Text size="xs" c="dimmed" tt="uppercase" fw={700}>
                                {stat.title}
                            </Text>
                            <stat.icon size={20} color={`var(--mantine-color-${stat.color}-6)`} />
                        </Group>
                        <Text size="xl" fw={700}>{stat.value}</Text>
                        <Progress value={stat.progress} size="sm" color={stat.color} mt="md" />
                    </Card>
                ))}
            </SimpleGrid>

            <Grid>
                <Grid.Col span={8}>
                    <Paper shadow="sm" p="md" withBorder>
                        <Group justify="space-between" mb="md">
                            <Title order={3}>Công việc hôm nay</Title>
                            <Badge color="blue" size="lg">{todayTasks.length} việc</Badge>
                        </Group>

                        {todayTasks.map((task) => (
                            <Card key={`${task.room}-${task.task}`} withBorder mb="sm" padding="sm">
                                <Group justify="space-between">
                                    <Group>
                                        <Badge size="xl" variant="filled" color="blue" radius="sm">
                                            {task.room}
                                        </Badge>
                                        <div>
                                            <Text fw={500}>{task.task}</Text>
                                            <Text size="xs" c="dimmed">{task.time}</Text>
                                        </div>
                                    </Group>
                                    <Group>
                                        <Badge
                                            color={
                                                task.priority === 'Cao' ? 'red' :
                                                    task.priority === 'Thường' ? 'yellow' : 'blue'
                                            }
                                        >
                                            {task.priority}
                                        </Badge>
                                        <Badge
                                            color={
                                                task.status === 'Hoàn thành' ? 'green' :
                                                    task.status === 'Đang làm' ? 'yellow' : 'gray'
                                            }
                                        >
                                            {task.status}
                                        </Badge>
                                    </Group>
                                </Group>
                            </Card>
                        ))}
                    </Paper>
                </Grid.Col>

                <Grid.Col span={4}>
                    <Paper shadow="sm" p="md" withBorder>
                        <Title order={3} mb="md">Trạng thái phòng</Title>

                        {roomStatus.map((floor) => (
                            <Card key={floor.floor} withBorder mb="sm">
                                <Text fw={500} mb="xs">{floor.floor}</Text>
                                <Group grow mb="xs">
                                    <div>
                                        <Text size="xs" c="dimmed">Sạch</Text>
                                        <Text fw={700} c="green">{floor.clean}</Text>
                                    </div>
                                    <div>
                                        <Text size="xs" c="dimmed">Bẩn</Text>
                                        <Text fw={700} c="red">{floor.dirty}</Text>
                                    </div>
                                    <div>
                                        <Text size="xs" c="dimmed">Đang ở</Text>
                                        <Text fw={700} c="blue">{floor.occupied}</Text>
                                    </div>
                                </Group>
                                <Progress.Root size="lg" mt="xs">
                                    <Progress.Section value={(floor.clean / floor.total) * 100} color="green" />
                                    <Progress.Section value={(floor.dirty / floor.total) * 100} color="red" />
                                    <Progress.Section value={(floor.occupied / floor.total) * 100} color="blue" />
                                </Progress.Root>
                            </Card>
                        ))}

                        <Button fullWidth mt="md" variant="light">
                            Xem tất cả phòng
                        </Button>
                    </Paper>
                </Grid.Col>
            </Grid>
        </div>
    );
}
