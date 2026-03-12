import { useState } from 'react';
import {
    Title,
    Paper,
    Grid,
    Card,
    Text,
    Group,
    Badge,
    Button,
    Select,
    Timeline,
} from '@mantine/core';
import { IconCalendar, IconClock, IconUser, IconBrush } from '@tabler/icons-react';

export default function SchedulePage() {
    const [selectedDate, setSelectedDate] = useState('today');

    const schedule = [
        {
            time: '08:00 - 09:00',
            tasks: [
                { id: 1, room: '101', task: 'Deep Clean', assignedTo: 'John Doe', priority: 'High' },
                { id: 2, room: '102', task: 'Regular Clean', assignedTo: 'Jane Smith', priority: 'Normal' },
            ]
        },
        {
            time: '09:00 - 10:00',
            tasks: [
                { id: 3, room: '103', task: 'Inspection', assignedTo: 'Mike Johnson', priority: 'Normal' },
                { id: 4, room: '104', task: 'Regular Clean', assignedTo: 'John Doe', priority: 'Normal' },
            ]
        },
        {
            time: '10:00 - 11:00',
            tasks: [
                { id: 5, room: '201', task: 'Maintenance', assignedTo: 'Jane Smith', priority: 'Low' },
                { id: 6, room: '202', task: 'Deep Clean', assignedTo: 'Mike Johnson', priority: 'High' },
            ]
        },
        {
            time: '13:00 - 14:00',
            tasks: [
                { id: 7, room: '203', task: 'Regular Clean', assignedTo: 'John Doe', priority: 'Normal' },
            ]
        },
        {
            time: '14:00 - 15:00',
            tasks: [
                { id: 8, room: '204', task: 'Inspection', assignedTo: 'Jane Smith', priority: 'Normal' },
                { id: 9, room: '205', task: 'Regular Clean', assignedTo: 'Mike Johnson', priority: 'Normal' },
            ]
        },
    ];

    const staffAvailability = [
        { name: 'John Doe', shift: 'Morning (8AM - 4PM)', tasks: 4, status: 'On Duty' },
        { name: 'Jane Smith', shift: 'Morning (8AM - 4PM)', tasks: 3, status: 'On Duty' },
        { name: 'Mike Johnson', shift: 'Afternoon (12PM - 8PM)', tasks: 3, status: 'On Duty' },
        { name: 'Sarah Wilson', shift: 'Morning (8AM - 4PM)', tasks: 0, status: 'Day Off' },
    ];

    return (
        <div>
            <Group justify="space-between" mb="lg">
                <Title order={1}>Work Schedule</Title>
                <Group>
                    <Select
                        placeholder="Select date"
                        data={[
                            { value: 'today', label: 'Today' },
                            { value: 'tomorrow', label: 'Tomorrow' },
                            { value: 'week', label: 'This Week' },
                        ]}
                        value={selectedDate}
                        onChange={setSelectedDate}
                    />
                    <Button>Manage Schedule</Button>
                </Group>
            </Group>

            <Grid>
                <Grid.Col span={8}>
                    <Paper shadow="sm" p="md" withBorder>
                        <Group mb="md">
                            <IconCalendar size={20} />
                            <Title order={3}>Daily Schedule - March 10, 2026</Title>
                        </Group>

                        <Timeline active={1} bulletSize={24} lineWidth={2}>
                            {schedule.map((slot, index) => (
                                <Timeline.Item
                                    key={index}
                                    bullet={<IconClock size={12} />}
                                    title={slot.time}
                                >
                                    <Text size="sm" mt={4}>
                                        {slot.tasks.map(task => (
                                            <Card key={task.id} withBorder mb="xs" padding="xs">
                                                <Group justify="space-between">
                                                    <Group>
                                                        <Badge color="blue" size="lg">{task.room}</Badge>
                                                        <div>
                                                            <Text size="sm" fw={500}>{task.task}</Text>
                                                            <Group gap="xs">
                                                                <IconUser size={12} />
                                                                <Text size="xs" c="dimmed">{task.assignedTo}</Text>
                                                            </Group>
                                                        </div>
                                                    </Group>
                                                    <Badge
                                                        color={task.priority === 'High' ? 'red' :
                                                            task.priority === 'Normal' ? 'yellow' : 'blue'}
                                                        size="sm"
                                                    >
                                                        {task.priority}
                                                    </Badge>
                                                </Group>
                                            </Card>
                                        ))}
                                    </Text>
                                </Timeline.Item>
                            ))}
                        </Timeline>
                    </Paper>
                </Grid.Col>

                <Grid.Col span={4}>
                    <Paper shadow="sm" p="md" withBorder mb="md">
                        <Group mb="md">
                            <IconBrush size={20} />
                            <Title order={3}>Staff Availability</Title>
                        </Group>

                        {staffAvailability.map((staff) => (
                            <Card key={staff.name} withBorder mb="sm">
                                <Group justify="space-between" mb="xs">
                                    <Text fw={500}>{staff.name}</Text>
                                    <Badge color={staff.status === 'On Duty' ? 'green' : 'gray'}>
                                        {staff.status}
                                    </Badge>
                                </Group>
                                <Text size="sm" c="dimmed">{staff.shift}</Text>
                                <Group justify="space-between" mt="xs">
                                    <Text size="sm">Tasks: {staff.tasks}</Text>
                                    <Button size="xs" variant="light">View</Button>
                                </Group>
                            </Card>
                        ))}
                    </Paper>

                    <Paper shadow="sm" p="md" withBorder>
                        <Title order={3} mb="md">Quick Stats</Title>
                        <Grid>
                            <Grid.Col span={6}>
                                <Card withBorder padding="xs">
                                    <Text size="xs" c="dimmed">Total Tasks</Text>
                                    <Text size="xl" fw={700}>24</Text>
                                </Card>
                            </Grid.Col>
                            <Grid.Col span={6}>
                                <Card withBorder padding="xs">
                                    <Text size="xs" c="dimmed">Completed</Text>
                                    <Text size="xl" fw={700} c="green">18</Text>
                                </Card>
                            </Grid.Col>
                            <Grid.Col span={6}>
                                <Card withBorder padding="xs">
                                    <Text size="xs" c="dimmed">In Progress</Text>
                                    <Text size="xl" fw={700} c="yellow">4</Text>
                                </Card>
                            </Grid.Col>
                            <Grid.Col span={6}>
                                <Card withBorder padding="xs">
                                    <Text size="xs" c="dimmed">Pending</Text>
                                    <Text size="xl" fw={700} c="orange">2</Text>
                                </Card>
                            </Grid.Col>
                        </Grid>
                    </Paper>
                </Grid.Col>
            </Grid>
        </div>
    );
}