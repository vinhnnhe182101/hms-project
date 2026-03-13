// src/pages/housekeeping/MobileTasksPage.jsx
import { useState } from 'react';
import {
    Stack,
    Text,
    Loader,
    Center,
    Group,
    SegmentedControl,
    ScrollArea,
    TextInput,
    ActionIcon, Button
} from '@mantine/core';
import { IconFilter, IconSearch } from '@tabler/icons-react';
import { useHousekeepingTasks } from '../../hooks/useHousekeepingTasks';
import { MobileTaskCard } from '../../components/MobileTaskCard';

export default function MobileTasksPage() {
    const {
        tasks,
        todayTasks,
        counts,
        loading,
        actionLoading,
        startTask,
        completeTask
    } = useHousekeepingTasks();

    const [filter, setFilter] = useState('all');
    const [searchQuery, setSearchQuery] = useState('');

    // Filter tasks
    const getFilteredTasks = () => {
        let filtered = [];

        switch(filter) {
            case 'all':
                filtered = tasks;
                break;
            case 'today':
                filtered = todayTasks;
                break;
            case 'scheduled':
                filtered = tasks.filter(t => t.status === 'SCHEDULED');
                break;
            case 'in_progress':
                filtered = tasks.filter(t => t.status === 'IN_PROGRESS');
                break;
            default:
                filtered = tasks;
        }

        // Apply search
        if (searchQuery) {
            filtered = filtered.filter(t =>
                t.roomNumber.toLowerCase().includes(searchQuery.toLowerCase())
            );
        }

        return filtered;
    };

    const filteredTasks = getFilteredTasks();

    const handleStartTask = async (taskId) => {
        await startTask(taskId);
    };

    const handleCompleteTask = async (taskId) => {
        await completeTask(taskId);
    };

    const handleReportIssue = (taskId) => {
        // TODO: Implement report issue
        console.log('Report issue for task:', taskId);
    };

    if (loading) {
        return (
            <Center style={{ height: 'calc(100vh - 130px)' }}>
                <Loader size="xl" />
            </Center>
        );
    }

    return (
        <Stack
            style={{
                height: 'calc(100vh - 130px)',
                overflow: 'hidden'
            }}
        >
            {/* Header Stats */}
            <Group justify="space-between" px="xs">
                <div>
                    <Text size="sm" c="dimmed">Total</Text>
                    <Text fw={700} size="xl">{counts.total}</Text>
                </div>
                <div>
                    <Text size="sm" c="dimmed">Pending</Text>
                    <Text fw={700} size="xl" c="yellow">
                        {counts.scheduled + counts.inProgress}
                    </Text>
                </div>
                <div>
                    <Text size="sm" c="dimmed">Done</Text>
                    <Text fw={700} size="xl" c="green">{counts.completed}</Text>
                </div>
            </Group>

            {/* Filter Controls */}
            <Group px="xs">
                <SegmentedControl
                    value={filter}
                    onChange={setFilter}
                    data={[
                        { label: 'All', value: 'all' },
                        { label: 'Today', value: 'today' },
                        { label: 'Scheduled', value: 'scheduled' },
                        { label: 'In Progress', value: 'in_progress' },
                    ]}
                    fullWidth
                    size="sm"
                />
            </Group>

            {/* Search Bar */}
            <Group px="xs">
                <TextInput
                    placeholder="Search room number..."
                    leftSection={<IconSearch size={16} />}
                    size="md"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    styles={{
                        root: { flex: 1 }
                    }}
                />
                <ActionIcon size="lg" variant="light">
                    <IconFilter size={20} />
                </ActionIcon>
            </Group>

            {/* Task List */}
            <ScrollArea
                style={{ flex: 1 }}
                offsetScrollbars
                scrollbarSize={4}
            >
                <Stack
                    px="xs"
                    gap="sm"
                    style={{
                        paddingBottom: 80,
                        minHeight: '100%'
                    }}
                >
                    {filteredTasks.length === 0 ? (
                        <Center style={{ height: 200 }}>
                            <Stack align="center" gap="xs">
                                <Text size="xl">📋</Text>
                                <Text c="dimmed">No tasks found</Text>
                                {searchQuery && (
                                    <Button
                                        variant="light"
                                        size="xs"
                                        onClick={() => setSearchQuery('')}
                                    >
                                        Clear search
                                    </Button>
                                )}
                            </Stack>
                        </Center>
                    ) : (
                        filteredTasks.map((task) => (
                            <MobileTaskCard
                                key={task.id}
                                task={task}
                                onStart={handleStartTask}
                                onComplete={handleCompleteTask}
                                onReport={handleReportIssue}
                                loading={actionLoading}
                            />
                        ))
                    )}
                </Stack>
            </ScrollArea>
        </Stack>
    );
}