import { useState } from 'react';
import {
    Title,
    Paper,
    Table,
    Badge,
    Button,
    Group,
    TextInput,
    Select,
    ActionIcon,
    Modal,
    Stack,
    Textarea,
    Grid,
    Card,
} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { IconSearch, IconEdit, IconCheck, IconX, IconEye } from '@tabler/icons-react';

export default function TasksPage() {
    const [opened, { open, close }] = useDisclosure(false);
    const [selectedTask, setSelectedTask] = useState(null);
    const [search, setSearch] = useState('');
    const [filterStatus, setFilterStatus] = useState('all');

    const tasks = [
        {
            id: 1,
            room: '101',
            task: 'Deep Clean',
            priority: 'High',
            status: 'Pending',
            assignedTo: 'John Doe',
            scheduledTime: '09:00 AM',
            notes: 'Guest requested extra cleaning'
        },
        {
            id: 2,
            room: '102',
            task: 'Regular Clean',
            priority: 'Normal',
            status: 'In Progress',
            assignedTo: 'Jane Smith',
            scheduledTime: '10:30 AM',
            notes: 'Standard cleaning'
        },
        {
            id: 3,
            room: '103',
            task: 'Inspection',
            priority: 'Normal',
            status: 'Pending',
            assignedTo: 'Mike Johnson',
            scheduledTime: '11:00 AM',
            notes: 'Post-checkout inspection'
        },
        {
            id: 4,
            room: '201',
            task: 'Maintenance',
            priority: 'Low',
            status: 'Scheduled',
            assignedTo: 'John Doe',
            scheduledTime: '02:00 PM',
            notes: 'Fix AC issue'
        },
        {
            id: 5,
            room: '202',
            task: 'Deep Clean',
            priority: 'High',
            status: 'Completed',
            assignedTo: 'Jane Smith',
            scheduledTime: '03:30 PM',
            notes: 'VIP guest room'
        },
    ];

    const getPriorityColor = (priority) => {
        switch (priority) {
            case 'High': return 'red';
            case 'Normal': return 'yellow';
            case 'Low': return 'blue';
            default: return 'gray';
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'Completed': return 'green';
            case 'In Progress': return 'yellow';
            case 'Pending': return 'orange';
            case 'Scheduled': return 'blue';
            default: return 'gray';
        }
    };

    const filteredTasks = tasks.filter(task => {
        const matchesSearch = task.room.toLowerCase().includes(search.toLowerCase()) ||
            task.task.toLowerCase().includes(search.toLowerCase());
        const matchesStatus = filterStatus === 'all' || task.status === filterStatus;
        return matchesSearch && matchesStatus;
    });

    const handleViewTask = (task) => {
        setSelectedTask(task);
        open();
    };

    return (
        <div>
            <Group justify="space-between" mb="lg">
                <Title order={1}>Tasks Management</Title>
                <Button>Create New Task</Button>
            </Group>

            <Paper shadow="sm" p="md" withBorder mb="lg">
                <Grid>
                    <Grid.Col span={6}>
                        <TextInput
                            placeholder="Search by room or task..."
                            leftSection={<IconSearch size={16} />}
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </Grid.Col>
                    <Grid.Col span={6}>
                        <Select
                            placeholder="Filter by status"
                            data={[
                                { value: 'all', label: 'All Status' },
                                { value: 'Pending', label: 'Pending' },
                                { value: 'In Progress', label: 'In Progress' },
                                { value: 'Completed', label: 'Completed' },
                                { value: 'Scheduled', label: 'Scheduled' },
                            ]}
                            value={filterStatus}
                            onChange={setFilterStatus}
                        />
                    </Grid.Col>
                </Grid>
            </Paper>

            <Paper shadow="sm" p="md" withBorder>
                <Table striped highlightOnHover>
                    <Table.Thead>
                        <Table.Tr>
                            <Table.Th>Room</Table.Th>
                            <Table.Th>Task</Table.Th>
                            <Table.Th>Priority</Table.Th>
                            <Table.Th>Status</Table.Th>
                            <Table.Th>Assigned To</Table.Th>
                            <Table.Th>Scheduled Time</Table.Th>
                            <Table.Th>Actions</Table.Th>
                        </Table.Tr>
                    </Table.Thead>
                    <Table.Tbody>
                        {filteredTasks.map((task) => (
                            <Table.Tr key={task.id}>
                                <Table.Td>
                                    <Badge size="lg" variant="filled" color="blue">
                                        {task.room}
                                    </Badge>
                                </Table.Td>
                                <Table.Td>{task.task}</Table.Td>
                                <Table.Td>
                                    <Badge color={getPriorityColor(task.priority)}>
                                        {task.priority}
                                    </Badge>
                                </Table.Td>
                                <Table.Td>
                                    <Badge color={getStatusColor(task.status)}>
                                        {task.status}
                                    </Badge>
                                </Table.Td>
                                <Table.Td>{task.assignedTo}</Table.Td>
                                <Table.Td>{task.scheduledTime}</Table.Td>
                                <Table.Td>
                                    <Group gap="xs">
                                        <ActionIcon variant="light" color="blue" onClick={() => handleViewTask(task)}>
                                            <IconEye size={16} />
                                        </ActionIcon>
                                        <ActionIcon variant="light" color="green">
                                            <IconCheck size={16} />
                                        </ActionIcon>
                                        <ActionIcon variant="light" color="red">
                                            <IconX size={16} />
                                        </ActionIcon>
                                    </Group>
                                </Table.Td>
                            </Table.Tr>
                        ))}
                    </Table.Tbody>
                </Table>
            </Paper>

            <Modal opened={opened} onClose={close} title="Task Details" size="lg">
                {selectedTask && (
                    <Stack>
                        <Group grow>
                            <div>
                                <Text size="sm" c="dimmed">Room</Text>
                                <Badge size="lg" variant="filled" color="blue">
                                    {selectedTask.room}
                                </Badge>
                            </div>
                            <div>
                                <Text size="sm" c="dimmed">Task</Text>
                                <Text fw={500}>{selectedTask.task}</Text>
                            </div>
                        </Group>

                        <Group grow>
                            <div>
                                <Text size="sm" c="dimmed">Priority</Text>
                                <Badge color={getPriorityColor(selectedTask.priority)}>
                                    {selectedTask.priority}
                                </Badge>
                            </div>
                            <div>
                                <Text size="sm" c="dimmed">Status</Text>
                                <Badge color={getStatusColor(selectedTask.status)}>
                                    {selectedTask.status}
                                </Badge>
                            </div>
                        </Group>

                        <Group grow>
                            <div>
                                <Text size="sm" c="dimmed">Assigned To</Text>
                                <Text>{selectedTask.assignedTo}</Text>
                            </div>
                            <div>
                                <Text size="sm" c="dimmed">Scheduled Time</Text>
                                <Text>{selectedTask.scheduledTime}</Text>
                            </div>
                        </Group>

                        <div>
                            <Text size="sm" c="dimmed">Notes</Text>
                            <Paper withBorder p="sm" bg="gray.0">
                                <Text>{selectedTask.notes}</Text>
                            </Paper>
                        </div>

                        <Textarea
                            label="Update Notes"
                            placeholder="Add notes about this task..."
                            minRows={3}
                        />

                        <Group justify="flex-end" mt="md">
                            <Button variant="light" onClick={close}>Close</Button>
                            <Button color="green">Mark as Completed</Button>
                        </Group>
                    </Stack>
                )}
            </Modal>
        </div>
    );
}