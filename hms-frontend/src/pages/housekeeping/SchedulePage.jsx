// src/pages/housekeeping/SchedulePage.jsx
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
    SimpleGrid,
    Timeline,
    Divider,
    Loader,
    Center,
    Alert,
    Skeleton
} from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import {
    IconCalendar,
    IconClock,
    IconArrowLeft,
    IconCalendarStats,
    IconCheck,
    IconX,
    IconChevronLeft,
    IconChevronRight
} from '@tabler/icons-react';
import { format, addDays, subDays, startOfWeek, endOfWeek, eachDayOfInterval, isSameDay, parseISO, isValid } from 'date-fns';
import { useHousekeepingTasks } from '../../hooks/useHousekeepingTasks';

export default function SchedulePage() {
    const navigate = useNavigate();
    const {
        schedule,
        todaySchedule,
        scheduleSummary,
        loading,
        fetchMySchedule,
        fetchTodaySchedule,
        fetchScheduleSummary,
        refreshAll
    } = useHousekeepingTasks();

    const [selectedDate, setSelectedDate] = useState(new Date());
    const [weekDays, setWeekDays] = useState([]);
    const [loadingWeek, setLoadingWeek] = useState(false);

    // Safe date formatting function
    const safeFormat = (date, formatStr) => {
        try {
            if (!date) return '';
            const dateObj = typeof date === 'string' ? parseISO(date) : date;
            if (!isValid(dateObj)) return '';
            return format(dateObj, formatStr);
        } catch (error) {
            console.error('Date formatting error:', error);
            return '';
        }
    };

    useEffect(() => {
        // Calculate week days based on selected date
        const start = startOfWeek(selectedDate, { weekStartsOn: 1 }); // Monday
        const end = endOfWeek(selectedDate, { weekStartsOn: 1 }); // Sunday
        const days = eachDayOfInterval({ start, end });
        setWeekDays(days);

        // Load schedule for the week
        loadWeekSchedule(start, end);
    }, [selectedDate]);

    // Initial load
    useEffect(() => {
        loadInitialData();
    }, []);

    const loadInitialData = async () => {
        setLoadingWeek(true);
        try {
            await Promise.all([
                fetchTodaySchedule(),
                fetchScheduleSummary()
            ]);
        } catch (error) {
            console.error('Error loading initial schedule data:', error);
        } finally {
            setLoadingWeek(false);
        }
    };

    const loadWeekSchedule = async (startDate, endDate) => {
        setLoadingWeek(true);
        try {
            await fetchMySchedule(
                format(startDate, 'yyyy-MM-dd'),
                format(endDate, 'yyyy-MM-dd')
            );
        } catch (error) {
            console.error('Error loading week schedule:', error);
        } finally {
            setLoadingWeek(false);
        }
    };

    const goToPreviousWeek = () => {
        setSelectedDate(prev => subDays(prev, 7));
    };

    const goToNextWeek = () => {
        setSelectedDate(prev => addDays(prev, 7));
    };

    const goToToday = () => {
        setSelectedDate(new Date());
        loadInitialData();
    };

    const getShiftForDate = (date) => {
        if (!schedule || !Array.isArray(schedule)) return null;
        return schedule.find(shift => {
            try {
                if (!shift.date) return false;
                const shiftDate = typeof shift.date === 'string' ? parseISO(shift.date) : new Date(shift.date);
                return isValid(shiftDate) && isSameDay(shiftDate, date);
            } catch (error) {
                return false;
            }
        });
    };

    const getStatusColor = (status) => {
        switch(status) {
            case 'COMPLETED': return 'green';
            case 'IN_PROGRESS': return 'yellow';
            case 'SCHEDULED': return 'blue';
            case 'OFF': return 'gray';
            default: return 'gray';
        }
    };

    const getStatusIcon = (status) => {
        switch(status) {
            case 'COMPLETED': return <IconCheck size={14} />;
            case 'IN_PROGRESS': return <IconClock size={14} />;
            case 'SCHEDULED': return <IconCalendar size={14} />;
            case 'OFF': return <IconX size={14} />;
            default: return null;
        }
    };

    if (loading && loadingWeek) {
        return (
            <Container size="lg" px={0}>
                <Stack gap="md">
                    <Skeleton height={50} radius="md" />
                    <Skeleton height={200} radius="md" />
                    <Skeleton height={150} radius="md" />
                </Stack>
            </Container>
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
                        <Title order={2}>My Schedule</Title>
                    </Group>
                    <Button variant="light" onClick={goToToday}>
                        Today
                    </Button>
                </Group>

                {/* Week Navigation */}
                <Paper withBorder radius="lg" p="md">
                    <Group justify="space-between">
                        <Button variant="subtle" onClick={goToPreviousWeek}>
                            <IconChevronLeft size={18} />
                        </Button>
                        <Text fw={600}>
                            {weekDays.length > 0 ?
                                `${safeFormat(weekDays[0], 'MMM d')} - ${safeFormat(weekDays[6], 'MMM d, yyyy')}`
                                : 'Loading...'}
                        </Text>
                        <Button variant="subtle" onClick={goToNextWeek}>
                            <IconChevronRight size={18} />
                        </Button>
                    </Group>
                </Paper>

                {/* Week Calendar */}
                {weekDays.length > 0 && (
                    <SimpleGrid cols={7} spacing="xs">
                        {weekDays.map((day, index) => {
                            const shift = getShiftForDate(day);
                            return (
                                <Paper
                                    key={index}
                                    withBorder
                                    p="xs"
                                    style={{
                                        textAlign: 'center',
                                        backgroundColor: isSameDay(day, new Date()) ? 'var(--mantine-color-blue-0)' : 'white',
                                        cursor: 'pointer',
                                        transition: 'all 0.2s',
                                        border: isSameDay(day, selectedDate) ? '2px solid var(--mantine-color-blue-6)' : '1px solid var(--mantine-color-gray-3)'
                                    }}
                                    onClick={() => setSelectedDate(day)}
                                >
                                    <Text size="xs" c="dimmed">{safeFormat(day, 'EEE')}</Text>
                                    <Text fw={700} size="lg">{safeFormat(day, 'd')}</Text>
                                    {loadingWeek ? (
                                        <Skeleton height={20} width={60} mt={4} />
                                    ) : (
                                        <Badge
                                            size="sm"
                                            color={getStatusColor(shift?.status)}
                                            variant="dot"
                                            mt={4}
                                        >
                                            {shift?.shiftName || 'No shift'}
                                        </Badge>
                                    )}
                                </Paper>
                            );
                        })}
                    </SimpleGrid>
                )}

                {/* Today's Schedule Summary */}
                {todaySchedule && (
                    <Paper withBorder radius="lg" p="lg" bg="blue.0">
                        <Group mb="md">
                            <ThemeIcon size="lg" color="blue" variant="light">
                                <IconCalendarStats size={20} />
                            </ThemeIcon>
                            <Title order={4}>Today's Schedule</Title>
                        </Group>

                        <SimpleGrid cols={2} spacing="md">
                            <Card withBorder>
                                <Text size="sm" c="dimmed">Shift</Text>
                                <Text fw={600} size="lg">{todaySchedule.shiftName || 'Day Off'}</Text>
                            </Card>
                            <Card withBorder>
                                <Text size="sm" c="dimmed">Status</Text>
                                <Badge size="lg" color={getStatusColor(todaySchedule.status)}>
                                    {todaySchedule.status || 'OFF'}
                                </Badge>
                            </Card>
                            {todaySchedule.startTime && (
                                <>
                                    <Card withBorder>
                                        <Text size="sm" c="dimmed">Start Time</Text>
                                        <Group>
                                            <IconClock size={18} />
                                            <Text fw={600}>{todaySchedule.startTime}</Text>
                                        </Group>
                                    </Card>
                                    <Card withBorder>
                                        <Text size="sm" c="dimmed">End Time</Text>
                                        <Group>
                                            <IconClock size={18} />
                                            <Text fw={600}>{todaySchedule.endTime}</Text>
                                        </Group>
                                    </Card>
                                </>
                            )}
                        </SimpleGrid>
                    </Paper>
                )}

                {/* Selected Day Details */}
                {schedule && Array.isArray(schedule) && (
                    <Paper withBorder radius="lg" p="lg">
                        <Group justify="space-between" mb="md">
                            <Group>
                                <ThemeIcon size="lg" color="blue" variant="light">
                                    <IconCalendarStats size={20} />
                                </ThemeIcon>
                                <div>
                                    <Title order={4}>{safeFormat(selectedDate, 'EEEE, MMMM d, yyyy')}</Title>
                                    <Text size="sm" c="dimmed">Your shift details</Text>
                                </div>
                            </Group>
                        </Group>

                        {(() => {
                            const shift = getShiftForDate(selectedDate);
                            return shift ? (
                                <Stack>
                                    <Card withBorder>
                                        <Group justify="space-between">
                                            <div>
                                                <Text size="sm" c="dimmed">Shift</Text>
                                                <Text fw={600} size="lg">
                                                    {shift.shiftName}
                                                </Text>
                                            </div>
                                            <Badge
                                                size="lg"
                                                color={getStatusColor(shift.status)}
                                            >
                                                {shift.status}
                                            </Badge>
                                        </Group>
                                    </Card>

                                    {shift.startTime && (
                                        <SimpleGrid cols={2}>
                                            <Card withBorder>
                                                <Text size="sm" c="dimmed">Start Time</Text>
                                                <Group>
                                                    <IconClock size={18} />
                                                    <Text fw={600}>{shift.startTime}</Text>
                                                </Group>
                                            </Card>
                                            <Card withBorder>
                                                <Text size="sm" c="dimmed">End Time</Text>
                                                <Group>
                                                    <IconClock size={18} />
                                                    <Text fw={600}>{shift.endTime}</Text>
                                                </Group>
                                            </Card>
                                        </SimpleGrid>
                                    )}

                                    {shift.totalTasks !== undefined && (
                                        <Card withBorder>
                                            <Group justify="space-between">
                                                <div>
                                                    <Text size="sm" c="dimmed">Tasks Completed</Text>
                                                    <Text fw={600} size="xl">
                                                        {shift.completedTasks || 0}/{shift.totalTasks}
                                                    </Text>
                                                </div>
                                                <Button
                                                    variant="light"
                                                    onClick={() => navigate('/housekeeping/tasks')}
                                                >
                                                    View Tasks
                                                </Button>
                                            </Group>
                                        </Card>
                                    )}
                                </Stack>
                            ) : (
                                <Alert color="gray" title="No Shift">
                                    You don't have a shift scheduled for this day.
                                </Alert>
                            );
                        })()}
                    </Paper>
                )}

                {/* Weekly Summary */}
                {scheduleSummary && (
                    <Paper withBorder radius="lg" p="lg">
                        <Title order={4} mb="md">Week Summary</Title>
                        <SimpleGrid cols={{ base: 2, sm: 4 }} spacing="md">
                            <Card withBorder>
                                <Text size="xs" c="dimmed">Total Shifts</Text>
                                <Text fw={700} size="xl">{scheduleSummary.totalShifts || 0}</Text>
                            </Card>
                            <Card withBorder>
                                <Text size="xs" c="dimmed">Total Hours</Text>
                                <Text fw={700} size="xl">{scheduleSummary.totalHours || 0}h</Text>
                            </Card>
                            <Card withBorder>
                                <Text size="xs" c="dimmed">Completed</Text>
                                <Text fw={700} size="xl">{scheduleSummary.completedShifts || 0}</Text>
                            </Card>
                            <Card withBorder>
                                <Text size="xs" c="dimmed">Upcoming</Text>
                                <Text fw={700} size="xl">{scheduleSummary.upcomingShifts || 0}</Text>
                            </Card>
                        </SimpleGrid>
                    </Paper>
                )}

                {/* Upcoming Shifts Timeline */}
                {schedule && Array.isArray(schedule) && schedule.length > 0 && (
                    <Paper withBorder radius="lg" p="lg">
                        <Title order={4} mb="md">Upcoming Shifts</Title>
                        <Timeline active={scheduleSummary?.completedShifts || 0}>
                            {schedule
                                .filter(shift => shift.status !== 'OFF')
                                .map((shift, index) => (
                                    <Timeline.Item
                                        key={index}
                                        bullet={getStatusIcon(shift.status)}
                                        title={shift.shiftName}
                                    >
                                        <Text size="sm">
                                            {safeFormat(shift.date, 'EEEE, MMMM d')} • {shift.startTime || '--:--'} - {shift.endTime || '--:--'}
                                        </Text>
                                        {shift.totalTasks !== undefined && (
                                            <Text size="xs" c="dimmed" mt={4}>
                                                {shift.totalTasks} tasks scheduled
                                            </Text>
                                        )}
                                    </Timeline.Item>
                                ))}
                        </Timeline>
                    </Paper>
                )}
            </Stack>
        </Container>
    );
}