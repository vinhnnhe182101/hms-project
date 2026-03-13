// src/pages/housekeeping/components/MobileTaskCard.jsx
import { useState } from 'react';
import { Card, Group, Text, Badge, Stack, Progress, ActionIcon, Menu, Modal, Button } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import {
    IconDoor,
    IconClock,
    IconFlag,
    IconCheck,
    IconPlayerPlay,
    IconDotsVertical,
    IconAlertCircle,
    IconCircleCheck
} from '@tabler/icons-react';
import { TASK_TYPES, TASK_STATUS } from '../constants/housekeeping';

export function MobileTaskCard({ task, onStart, onComplete, onReport, loading }) {
    const [opened, { open, close }] = useDisclosure(false);
    const [actionType, setActionType] = useState(null);

    const taskType = TASK_TYPES[task.taskType] || { label: task.taskType, color: 'gray' };
    const taskStatus = TASK_STATUS[task.status] || { label: task.status, color: 'gray' };

    const formatTime = (timestamp) => {
        if (!timestamp) return '';
        return new Date(timestamp).toLocaleTimeString('en-US', {
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const formatDate = (timestamp) => {
        if (!timestamp) return '';
        return new Date(timestamp).toLocaleDateString('en-US', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    // Tính thời gian đã làm
    const getElapsedTime = () => {
        if (task.status !== 'IN_PROGRESS' || !task.assignedAt) return null;

        const start = new Date(task.assignedAt);
        const now = new Date();
        const diffMinutes = Math.floor((now - start) / 60000);

        if (diffMinutes < 60) return `${diffMinutes} min`;
        const hours = Math.floor(diffMinutes / 60);
        const mins = diffMinutes % 60;
        return `${hours}h ${mins > 0 ? `${mins}min` : ''}`;
    };

    const handleConfirmAction = () => {
        if (actionType === 'start') {
            onStart(task.id);
        } else if (actionType === 'complete') {
            onComplete(task.id);
        }
        close();
    };

    const elapsedTime = getElapsedTime();

    return (
        <>
            <Card
                shadow="sm"
                withBorder
                radius="md"
                p="md"
                style={{
                    borderLeft: `4px solid var(--mantine-color-${taskStatus.color}-6)`,
                    touchAction: 'manipulation',
                    opacity: loading ? 0.7 : 1,
                    pointerEvents: loading ? 'none' : 'auto'
                }}
            >
                <Stack gap="xs">
                    {/* Header */}
                    <Group justify="space-between" wrap="nowrap">
                        <Group gap="xs" wrap="nowrap">
                            <IconDoor size={20} style={{ flexShrink: 0 }} />
                            <Text fw={700} size="lg">Room {task.roomNumber}</Text>
                        </Group>

                        {/* Action Menu */}
                        <Menu shadow="md" width={200} position="bottom-end">
                            <Menu.Target>
                                <ActionIcon variant="subtle" size="lg">
                                    <IconDotsVertical size={20} />
                                </ActionIcon>
                            </Menu.Target>

                            <Menu.Dropdown>
                                {task.status === 'SCHEDULED' && (
                                    <Menu.Item
                                        leftSection={<IconPlayerPlay size={14} />}
                                        onClick={() => {
                                            setActionType('start');
                                            open();
                                        }}
                                    >
                                        Start Task
                                    </Menu.Item>
                                )}

                                {task.status === 'IN_PROGRESS' && (
                                    <Menu.Item
                                        leftSection={<IconCheck size={14} />}
                                        onClick={() => {
                                            setActionType('complete');
                                            open();
                                        }}
                                    >
                                        Complete Task
                                    </Menu.Item>
                                )}

                                <Menu.Item
                                    leftSection={<IconAlertCircle size={14} />}
                                    onClick={() => onReport(task.id)}
                                    color="red"
                                >
                                    Report Issue
                                </Menu.Item>
                            </Menu.Dropdown>
                        </Menu>
                    </Group>

                    {/* Task Info */}
                    <Group gap="xs" wrap="wrap">
                        <Badge color={taskType.color} variant="light" size="lg">
                            {taskType.label}
                        </Badge>

                        <Badge color={taskStatus.color} size="lg">
                            {taskStatus.label}
                        </Badge>

                        {elapsedTime && (
                            <Badge color="blue" variant="outline">
                                ⏱️ {elapsedTime}
                            </Badge>
                        )}
                    </Group>

                    {/* Room Status */}
                    <Group gap="xs">
                        <Text size="sm" c="dimmed">Room:</Text>
                        <Badge color={task.roomStatusColor} variant="dot">
                            {task.roomStatusDisplay}
                        </Badge>
                    </Group>

                    {/* Time Info */}
                    <Group gap="md">
                        {task.assignedAt && (
                            <Group gap={4}>
                                <IconClock size={14} />
                                <Text size="sm" c="dimmed">
                                    Start: {formatTime(task.assignedAt)}
                                </Text>
                            </Group>
                        )}

                        {task.completedAt && (
                            <Group gap={4}>
                                <IconCircleCheck size={14} />
                                <Text size="sm" c="dimmed">
                                    Done: {formatTime(task.completedAt)} {formatDate(task.completedAt)}
                                </Text>
                            </Group>
                        )}
                    </Group>

                    {/* Progress bar cho task đang làm */}
                    {task.status === 'IN_PROGRESS' && (
                        <Progress
                            value={45}
                            size="sm"
                            striped
                            animated
                            color="blue"
                            mt="xs"
                        />
                    )}

                    {/* Quick Actions */}
                    <Group grow mt="xs">
                        {task.status === 'SCHEDULED' && (
                            <Button
                                size="md"
                                color="blue"
                                leftSection={<IconPlayerPlay size={18} />}
                                onClick={() => {
                                    setActionType('start');
                                    open();
                                }}
                                loading={loading}
                                fullWidth
                                styles={{
                                    root: {
                                        height: 44,
                                        fontSize: 16
                                    }
                                }}
                            >
                                Start
                            </Button>
                        )}

                        {task.status === 'IN_PROGRESS' && (
                            <Button
                                size="md"
                                color="green"
                                leftSection={<IconCheck size={18} />}
                                onClick={() => {
                                    setActionType('complete');
                                    open();
                                }}
                                loading={loading}
                                fullWidth
                                styles={{
                                    root: {
                                        height: 44,
                                        fontSize: 16
                                    }
                                }}
                            >
                                Complete
                            </Button>
                        )}
                    </Group>
                </Stack>
            </Card>

            {/* Confirmation Modal */}
            <Modal
                opened={opened}
                onClose={close}
                title="Confirm Action"
                size="sm"
                centered
            >
                <Stack>
                    <Text>
                        Are you sure you want to {actionType === 'start' ? 'start' : 'complete'}
                        task for room {task?.roomNumber}?
                    </Text>
                    <Group grow>
                        <Button variant="light" onClick={close}>
                            Cancel
                        </Button>
                        <Button
                            color={actionType === 'start' ? 'blue' : 'green'}
                            onClick={handleConfirmAction}
                            loading={loading}
                        >
                            Confirm
                        </Button>
                    </Group>
                </Stack>
            </Modal>
        </>
    );
}