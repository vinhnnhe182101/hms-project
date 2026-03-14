// src/pages/housekeeping/ReportsPage.jsx
import { useState, useEffect } from 'react';
import {
    Container,
    Paper,
    Stack,
    Group,
    Text,
    Title,
    Card,
    ThemeIcon,
    Badge,
    Button,
    Select,
    SimpleGrid,
    Divider,
    Loader,
    Center,
    Progress,
    RingProgress,
    Table,
    Grid
} from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import {
    IconArrowLeft,
    IconChartBar,
    IconChecklist,
    IconClock,
    IconCheck,
    IconBottle,
    IconAlertCircle,
    IconCalendarStats,
    IconDownload,
    IconStar,
    IconTrendingUp
} from '@tabler/icons-react';
import { format, subDays, subWeeks, subMonths } from 'date-fns';

export default function ReportsPage() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [period, setPeriod] = useState('week');
    const [reportData, setReportData] = useState(null);

    useEffect(() => {
        loadReportData();
    }, [period]);

    const loadReportData = async () => {
        setLoading(true);
        try {
            // Mock data - replace with actual API call
            setTimeout(() => {
                setReportData({
                    summary: {
                        totalTasks: 45,
                        completedTasks: 38,
                        completionRate: 84.4,
                        totalHours: 32.5,
                        avgTimePerTask: 43, // minutes
                        rating: 4.7
                    },
                    tasksByType: {
                        cleaning: { total: 25, completed: 22 },
                        inspection: { total: 12, completed: 10 },
                        maintenance: { total: 8, completed: 6 }
                    },
                    tasksByStatus: {
                        scheduled: 5,
                        inProgress: 2,
                        completed: 38
                    },
                    dailyPerformance: [
                        { date: subDays(new Date(), 6), tasks: 6, completed: 6 },
                        { date: subDays(new Date(), 5), tasks: 7, completed: 6 },
                        { date: subDays(new Date(), 4), tasks: 5, completed: 5 },
                        { date: subDays(new Date(), 3), tasks: 8, completed: 7 },
                        { date: subDays(new Date(), 2), tasks: 6, completed: 5 },
                        { date: subDays(new Date(), 1), tasks: 7, completed: 5 },
                        { date: new Date(), tasks: 6, completed: 4 }
                    ],
                    minibar: {
                        totalReports: 12,
                        totalRevenue: 245.50,
                        items: [
                            { name: 'Coca Cola', quantity: 8, revenue: 20.00 },
                            { name: 'Beer', quantity: 6, revenue: 24.00 },
                            { name: 'Water', quantity: 15, revenue: 15.00 },
                            { name: 'Chocolate', quantity: 5, revenue: 10.00 },
                            { name: 'Chips', quantity: 7, revenue: 14.00 }
                        ]
                    },
                    damage: {
                        totalReports: 3,
                        totalPenalty: 185.00,
                        items: [
                            { item: 'Towel', quantity: 2, penalty: 30.00 },
                            { item: 'Glass', quantity: 1, penalty: 5.00 },
                            { item: 'Remote', quantity: 1, penalty: 150.00 }
                        ]
                    },
                    comparison: {
                        previousPeriod: {
                            tasks: 42,
                            completionRate: 80.2,
                            minibarRevenue: 210.00
                        },
                        change: {
                            tasks: +7.1,
                            completionRate: +4.2,
                            minibarRevenue: +16.9
                        }
                    }
                });
                setLoading(false);
            }, 500);
        } catch (error) {
            console.error('Error loading report data:', error);
            setLoading(false);
        }
    };

    const getPeriodLabel = () => {
        switch(period) {
            case 'week': return 'This Week';
            case 'month': return 'This Month';
            case 'quarter': return 'This Quarter';
            default: return 'This Week';
        }
    };

    const formatCurrency = (value) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(value);
    };

    const formatPercentage = (value) => {
        return `${value.toFixed(1)}%`;
    };

    const handleExport = () => {
        // TODO: Implement export functionality
        console.log('Exporting report...');
    };

    if (loading || !reportData) {
        return (
            <Center style={{ height: '60vh' }}>
                <Loader size="xl" />
            </Center>
        );
    }

    return (
        <Container size="lg" px={0}>
            <Stack gap="md">
                {/* Header */}
                <Group justify="space-between">
                    <Group>
                        <Button
                            variant="subtle"
                            leftSection={<IconArrowLeft size={16} />}
                            onClick={() => navigate('/housekeeping')}
                        >
                            Back
                        </Button>
                        <Title order={2}>My Reports</Title>
                    </Group>
                    <Group>
                        <Select
                            value={period}
                            onChange={setPeriod}
                            data={[
                                { value: 'week', label: 'This Week' },
                                { value: 'month', label: 'This Month' },
                                { value: 'quarter', label: 'This Quarter' }
                            ]}
                            w={150}
                        />
                        <Button
                            leftSection={<IconDownload size={16} />}
                            variant="light"
                            onClick={handleExport}
                        >
                            Export
                        </Button>
                    </Group>
                </Group>

                {/* Key Metrics */}
                <SimpleGrid cols={{ base: 2, md: 4 }} spacing="md">
                    <Paper withBorder p="lg" radius="lg">
                        <Group justify="space-between">
                            <div>
                                <Text size="xs" c="dimmed">Total Tasks</Text>
                                <Text fw={700} size="28px">{reportData.summary.totalTasks}</Text>
                                <Text size="xs" c={reportData.comparison.change.tasks > 0 ? 'green' : 'red'}>
                                    {reportData.comparison.change.tasks > 0 ? '↑' : '↓'}
                                    {Math.abs(reportData.comparison.change.tasks)}% vs last {period}
                                </Text>
                            </div>
                            <ThemeIcon size="xl" radius="md" color="blue" variant="light">
                                <IconChecklist size={24} />
                            </ThemeIcon>
                        </Group>
                    </Paper>

                    <Paper withBorder p="lg" radius="lg">
                        <Group justify="space-between">
                            <div>
                                <Text size="xs" c="dimmed">Completion Rate</Text>
                                <Text fw={700} size="28px">{formatPercentage(reportData.summary.completionRate)}</Text>
                                <Text size="xs" c={reportData.comparison.change.completionRate > 0 ? 'green' : 'red'}>
                                    {reportData.comparison.change.completionRate > 0 ? '↑' : '↓'}
                                    {Math.abs(reportData.comparison.change.completionRate)}%
                                </Text>
                            </div>
                            <RingProgress
                                size={70}
                                thickness={6}
                                roundCaps
                                sections={[{ value: reportData.summary.completionRate, color: 'green' }]}
                                label={
                                    <Text size="xs" ta="center">
                                        {Math.round(reportData.summary.completionRate)}%
                                    </Text>
                                }
                            />
                        </Group>
                    </Paper>

                    <Paper withBorder p="lg" radius="lg">
                        <Group justify="space-between">
                            <div>
                                <Text size="xs" c="dimmed">Avg Time/Task</Text>
                                <Text fw={700} size="28px">{reportData.summary.avgTimePerTask}m</Text>
                                <Text size="xs" c="dimmed">{reportData.summary.totalHours}h total</Text>
                            </div>
                            <ThemeIcon size="xl" radius="md" color="yellow" variant="light">
                                <IconClock size={24} />
                            </ThemeIcon>
                        </Group>
                    </Paper>

                    <Paper withBorder p="lg" radius="lg">
                        <Group justify="space-between">
                            <div>
                                <Text size="xs" c="dimmed">Rating</Text>
                                <Text fw={700} size="28px">{reportData.summary.rating}</Text>
                                <Text size="xs" c="dimmed">/ 5.0</Text>
                            </div>
                            <ThemeIcon size="xl" radius="md" color="violet" variant="light">
                                <IconStar size={24} />
                            </ThemeIcon>
                        </Group>
                    </Paper>
                </SimpleGrid>

                {/* Tasks Breakdown */}
                <Grid>
                    <Grid.Col span={{ base: 12, md: 6 }}>
                        <Paper withBorder p="lg" radius="lg">
                            <Title order={4} mb="md">Tasks by Type</Title>
                            <Stack>
                                <div>
                                    <Group justify="space-between" mb={5}>
                                        <Text size="sm">Cleaning</Text>
                                        <Text size="sm" fw={500}>
                                            {reportData.tasksByType.cleaning.completed}/{reportData.tasksByType.cleaning.total}
                                        </Text>
                                    </Group>
                                    <Progress
                                        value={(reportData.tasksByType.cleaning.completed / reportData.tasksByType.cleaning.total) * 100}
                                        color="blue"
                                        size="lg"
                                    />
                                </div>
                                <div>
                                    <Group justify="space-between" mb={5}>
                                        <Text size="sm">Inspection</Text>
                                        <Text size="sm" fw={500}>
                                            {reportData.tasksByType.inspection.completed}/{reportData.tasksByType.inspection.total}
                                        </Text>
                                    </Group>
                                    <Progress
                                        value={(reportData.tasksByType.inspection.completed / reportData.tasksByType.inspection.total) * 100}
                                        color="violet"
                                        size="lg"
                                    />
                                </div>
                                <div>
                                    <Group justify="space-between" mb={5}>
                                        <Text size="sm">Maintenance</Text>
                                        <Text size="sm" fw={500}>
                                            {reportData.tasksByType.maintenance.completed}/{reportData.tasksByType.maintenance.total}
                                        </Text>
                                    </Group>
                                    <Progress
                                        value={(reportData.tasksByType.maintenance.completed / reportData.tasksByType.maintenance.total) * 100}
                                        color="orange"
                                        size="lg"
                                    />
                                </div>
                            </Stack>
                        </Paper>
                    </Grid.Col>

                    <Grid.Col span={{ base: 12, md: 6 }}>
                        <Paper withBorder p="lg" radius="lg">
                            <Title order={4} mb="md">Tasks by Status</Title>
                            <Stack>
                                <Group justify="space-between">
                                    <Group>
                                        <Badge color="gray">Scheduled</Badge>
                                        <Text size="sm">{reportData.tasksByStatus.scheduled} tasks</Text>
                                    </Group>
                                    <Text fw={500}>{Math.round(reportData.tasksByStatus.scheduled / reportData.summary.totalTasks * 100)}%</Text>
                                </Group>
                                <Group justify="space-between">
                                    <Group>
                                        <Badge color="yellow">In Progress</Badge>
                                        <Text size="sm">{reportData.tasksByStatus.inProgress} tasks</Text>
                                    </Group>
                                    <Text fw={500}>{Math.round(reportData.tasksByStatus.inProgress / reportData.summary.totalTasks * 100)}%</Text>
                                </Group>
                                <Group justify="space-between">
                                    <Group>
                                        <Badge color="green">Completed</Badge>
                                        <Text size="sm">{reportData.tasksByStatus.completed} tasks</Text>
                                    </Group>
                                    <Text fw={500}>{Math.round(reportData.tasksByStatus.completed / reportData.summary.totalTasks * 100)}%</Text>
                                </Group>
                            </Stack>
                        </Paper>
                    </Grid.Col>
                </Grid>

                {/* Daily Performance */}
                <Paper withBorder p="lg" radius="lg">
                    <Group justify="space-between" mb="md">
                        <Group>
                            <ThemeIcon size="lg" color="blue" variant="light">
                                <IconCalendarStats size={20} />
                            </ThemeIcon>
                            <Title order={4}>Daily Performance</Title>
                        </Group>
                    </Group>

                    <Table>
                        <Table.Thead>
                            <Table.Tr>
                                <Table.Th>Date</Table.Th>
                                <Table.Th>Tasks</Table.Th>
                                <Table.Th>Completed</Table.Th>
                                <Table.Th>Rate</Table.Th>
                            </Table.Tr>
                        </Table.Thead>
                        <Table.Tbody>
                            {reportData.dailyPerformance.map((day, index) => (
                                <Table.Tr key={index}>
                                    <Table.Td>{format(new Date(day.date), 'EEEE, MMM d')}</Table.Td>
                                    <Table.Td>{day.tasks}</Table.Td>
                                    <Table.Td>{day.completed}</Table.Td>
                                    <Table.Td>
                                        <Badge color={day.completed === day.tasks ? 'green' : 'yellow'}>
                                            {Math.round(day.completed / day.tasks * 100)}%
                                        </Badge>
                                    </Table.Td>
                                </Table.Tr>
                            ))}
                        </Table.Tbody>
                    </Table>
                </Paper>

                {/* Minibar & Damage Reports */}
                <Grid>
                    <Grid.Col span={{ base: 12, md: 6 }}>
                        <Paper withBorder p="lg" radius="lg">
                            <Group justify="space-between" mb="md">
                                <Group>
                                    <ThemeIcon color="violet" variant="light">
                                        <IconBottle size={20} />
                                    </ThemeIcon>
                                    <Title order={4}>Minibar Reports</Title>
                                </Group>
                                <Badge size="lg" color="violet">{reportData.minibar.totalReports} reports</Badge>
                            </Group>

                            <Text size="xl" fw={700} mb="md">
                                {formatCurrency(reportData.minibar.totalRevenue)}
                            </Text>

                            <Divider mb="md" />

                            <Table>
                                <Table.Thead>
                                    <Table.Tr>
                                        <Table.Th>Item</Table.Th>
                                        <Table.Th>Qty</Table.Th>
                                        <Table.Th>Revenue</Table.Th>
                                    </Table.Tr>
                                </Table.Thead>
                                <Table.Tbody>
                                    {reportData.minibar.items.map((item, index) => (
                                        <Table.Tr key={index}>
                                            <Table.Td>{item.name}</Table.Td>
                                            <Table.Td>{item.quantity}</Table.Td>
                                            <Table.Td>{formatCurrency(item.revenue)}</Table.Td>
                                        </Table.Tr>
                                    ))}
                                </Table.Tbody>
                            </Table>
                        </Paper>
                    </Grid.Col>

                    <Grid.Col span={{ base: 12, md: 6 }}>
                        <Paper withBorder p="lg" radius="lg">
                            <Group justify="space-between" mb="md">
                                <Group>
                                    <ThemeIcon color="red" variant="light">
                                        <IconAlertCircle size={20} />
                                    </ThemeIcon>
                                    <Title order={4}>Damage Reports</Title>
                                </Group>
                                <Badge size="lg" color="red">{reportData.damage.totalReports} reports</Badge>
                            </Group>

                            <Text size="xl" fw={700} mb="md" c="red">
                                {formatCurrency(reportData.damage.totalPenalty)}
                            </Text>

                            <Divider mb="md" />

                            <Table>
                                <Table.Thead>
                                    <Table.Tr>
                                        <Table.Th>Item</Table.Th>
                                        <Table.Th>Qty</Table.Th>
                                        <Table.Th>Penalty</Table.Th>
                                    </Table.Tr>
                                </Table.Thead>
                                <Table.Tbody>
                                    {reportData.damage.items.map((item, index) => (
                                        <Table.Tr key={index}>
                                            <Table.Td>{item.item}</Table.Td>
                                            <Table.Td>{item.quantity}</Table.Td>
                                            <Table.Td>{formatCurrency(item.penalty)}</Table.Td>
                                        </Table.Tr>
                                    ))}
                                </Table.Tbody>
                            </Table>
                        </Paper>
                    </Grid.Col>
                </Grid>

                {/* Performance Comparison */}
                <Paper withBorder p="lg" radius="lg">
                    <Group justify="space-between" mb="md">
                        <Group>
                            <ThemeIcon size="lg" color="green" variant="light">
                                <IconTrendingUp size={20} />
                            </ThemeIcon>
                            <Title order={4}>Performance Comparison</Title>
                        </Group>
                    </Group>

                    <SimpleGrid cols={3}>
                        <Card withBorder>
                            <Text size="sm" c="dimmed">Tasks Completed</Text>
                            <Group justify="space-between">
                                <div>
                                    <Text size="xs">Previous: {reportData.comparison.previousPeriod.tasks}</Text>
                                    <Text size="xl" fw={700}>{reportData.summary.completedTasks}</Text>
                                </div>
                                <Badge color={reportData.comparison.change.tasks > 0 ? 'green' : 'red'} size="lg">
                                    {reportData.comparison.change.tasks > 0 ? '+' : ''}{reportData.comparison.change.tasks}%
                                </Badge>
                            </Group>
                        </Card>

                        <Card withBorder>
                            <Text size="sm" c="dimmed">Completion Rate</Text>
                            <Group justify="space-between">
                                <div>
                                    <Text size="xs">Previous: {formatPercentage(reportData.comparison.previousPeriod.completionRate)}</Text>
                                    <Text size="xl" fw={700}>{formatPercentage(reportData.summary.completionRate)}</Text>
                                </div>
                                <Badge color={reportData.comparison.change.completionRate > 0 ? 'green' : 'red'} size="lg">
                                    {reportData.comparison.change.completionRate > 0 ? '+' : ''}{reportData.comparison.change.completionRate}%
                                </Badge>
                            </Group>
                        </Card>

                        <Card withBorder>
                            <Text size="sm" c="dimmed">Minibar Revenue</Text>
                            <Group justify="space-between">
                                <div>
                                    <Text size="xs">Previous: {formatCurrency(reportData.comparison.previousPeriod.minibarRevenue)}</Text>
                                    <Text size="xl" fw={700}>{formatCurrency(reportData.minibar.totalRevenue)}</Text>
                                </div>
                                <Badge color={reportData.comparison.change.minibarRevenue > 0 ? 'green' : 'red'} size="lg">
                                    {reportData.comparison.change.minibarRevenue > 0 ? '+' : ''}{reportData.comparison.change.minibarRevenue}%
                                </Badge>
                            </Group>
                        </Card>
                    </SimpleGrid>
                </Paper>
            </Stack>
        </Container>
    );
}