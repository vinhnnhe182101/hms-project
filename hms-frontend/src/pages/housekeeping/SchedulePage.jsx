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
    Select,
    SimpleGrid,
    Timeline,
    Divider,
    Loader,
    Center,
    Alert
} from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import {
    IconCalendar,
    IconClock,
    IconArrowLeft,
    IconCalendarStats,
    IconCheck,
    IconX,
    IconUser,
    IconChevronLeft,
    IconChevronRight
} from '@tabler/icons-react';
import { format, addDays, subDays, startOfWeek, endOfWeek, eachDayOfInterval, isSameDay, parseISO, isValid } from 'date-fns';

export default function SchedulePage() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [weekDays, setWeekDays] = useState([]);
    const [schedule, setSchedule] = useState(null);

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
        loadSchedule();
    }, [selectedDate]);

    const loadSchedule = async () => {
        setLoading(true);
        try {
            // Mock data - replace with actual API call
            // const response = await housekeepingApi.getMySchedule(selectedDate);

            // Mock schedule data
            setTimeout(() => {
                setSchedule({
                    shifts: [
                        {
                            date: new Date().toISOString(),
                            shiftName: 'Morning Shift',
                            startTime: '08:00',
                            endTime: '16:00',
                            status: 'COMPLETED',
                            tasks: 4,
                            completedTasks: 4
                        },
                        {
                            date: addDays(new Date(), 1).toISOString(),
                            shiftName: 'Morning Shift',
                            startTime: '08:00',
                            endTime: '16:00',
                            status: 'SCHEDULED',
                            tasks: 5,
                            completedTasks: 0
                        },
                        {
                            date: addDays(new Date(), 2).toISOString(),
                            shiftName: 'Afternoon Shift',
                            startTime: '14:00',
                            endTime: '22:00',
                            status: 'SCHEDULED',
                            tasks: 3,
                            completedTasks: 0
                        },
                        {
                            date: addDays(new Date(), 3).toISOString(),
                            shiftName: 'Day Off',
                            startTime: null,
                            endTime: null,
                            status: 'OFF',
                            tasks: 0,
                            completedTasks: 0
                        },
                        {
                            date: addDays(new Date(), 4).toISOString(),
                            shiftName: 'Morning Shift',
                            startTime: '08:00',
                            endTime: '16:00',
                            status: 'SCHEDULED',
                            tasks: 4,
                            completedTasks: 0
                        }
                    ],
                    summary: {
                        totalShifts: 5,
                        totalHours: 40,
                        completedShifts: 1,
                        upcomingShifts: 4
                    }
                });
                setLoading(false);
            }, 500);
        } catch (error) {
            console.error('Error loading schedule:', error);
            setLoading(false);
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
    };

    const getShiftForDate = (date) => {
        if (!schedule || !schedule.shifts) return null;
        return schedule.shifts.find(shift => {
            try {
                const shiftDate = typeof shift.date === 'string' ? parseISO(shift.date) : shift.date;
                return shiftDate && isValid(shiftDate) && isSameDay(shiftDate, date);
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

    if (loading) {
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
                                        cursor: 'pointer'
                                    }}
                                    onClick={() => setSelectedDate(day)}
                                >
                                    <Text size="xs" c="dimmed">{safeFormat(day, 'EEE')}</Text>
                                    <Text fw={700} size="lg">{safeFormat(day, 'd')}</Text>
                                    <Badge
                                        size="sm"
                                        color={getStatusColor(shift?.status)}
                                        variant="dot"
                                        mt={4}
                                    >
                                        {shift?.shiftName || 'No shift'}
                                    </Badge>
                                </Paper>
                            );
                        })}
                    </SimpleGrid>
                )}

                {/* Selected Day Details */}
                {schedule && (
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

                                    <Card withBorder>
                                        <Group justify="space-between">
                                            <div>
                                                <Text size="sm" c="dimmed">Tasks Completed</Text>
                                                <Text fw={600} size="xl">
                                                    {shift.completedTasks}/{shift.tasks}
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
                {schedule && (
                    <Paper withBorder radius="lg" p="lg">
                        <Title order={4} mb="md">Week Summary</Title>
                        <SimpleGrid cols={{ base: 2, sm: 4 }} spacing="md">
                            <Card withBorder>
                                <Text size="xs" c="dimmed">Total Shifts</Text>
                                <Text fw={700} size="xl">{schedule.summary.totalShifts}</Text>
                            </Card>
                            <Card withBorder>
                                <Text size="xs" c="dimmed">Total Hours</Text>
                                <Text fw={700} size="xl">{schedule.summary.totalHours}h</Text>
                            </Card>
                            <Card withBorder>
                                <Text size="xs" c="dimmed">Completed</Text>
                                <Text fw={700} size="xl">{schedule.summary.completedShifts}</Text>
                            </Card>
                            <Card withBorder>
                                <Text size="xs" c="dimmed">Upcoming</Text>
                                <Text fw={700} size="xl">{schedule.summary.upcomingShifts}</Text>
                            </Card>
                        </SimpleGrid>
                    </Paper>
                )}

                {/* Upcoming Shifts Timeline */}
                {schedule && schedule.shifts && (
                    <Paper withBorder radius="lg" p="lg">
                        <Title order={4} mb="md">Upcoming Shifts</Title>
                        <Timeline active={schedule.summary.completedShifts}>
                            {schedule.shifts
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
                                        <Text size="xs" c="dimmed" mt={4}>
                                            {shift.tasks} tasks scheduled
                                        </Text>
                                    </Timeline.Item>
                                ))}
                        </Timeline>
                    </Paper>
                )}
            </Stack>
        </Container>
    );
}