// src/pages/customer/components/BookingHistoryCard.jsx
import { Card, Badge, Group, Text, Stack, Divider, Button, ThemeIcon } from '@mantine/core';
import {
    IconCalendar,
    IconDoor,
    IconUsers,
    IconCoin,
    IconClock,
    IconMapPin
} from '@tabler/icons-react';

const statusColors = {
    confirmed: 'blue',
    pending: 'yellow',
    completed: 'green',
    cancelled: 'red',
    'in-house': 'teal'
};

const statusLabels = {
    confirmed: 'Confirmed',
    pending: 'Pending',
    completed: 'Completed',
    cancelled: 'Cancelled',
    'in-house': 'In House'
};

export function BookingHistoryCard({ booking, onViewDetails }) {
    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('en-US', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        });
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    };

    return (
        <Card withBorder radius="md" p="md" shadow="sm">
            <Card.Section withBorder inheritPadding py="xs" bg="gray.0">
                <Group justify="space-between">
                    <Group gap="xs">
                        <ThemeIcon size="sm" color="blue" variant="light">
                            <IconCalendar size={12} />
                        </ThemeIcon>
                        <Text fw={500} size="sm">{booking.code}</Text>
                    </Group>
                    <Badge
                        color={statusColors[booking.status?.toLowerCase()]}
                        variant="light"
                        size="lg"
                    >
                        {statusLabels[booking.status?.toLowerCase()] || booking.status}
                    </Badge>
                </Group>
            </Card.Section>

            <Stack mt="md" gap="xs">
                {/* Room Info */}
                <Group gap="xs">
                    <IconDoor size={16} color="gray" />
                    <Text size="sm">
                        {booking.roomType} {booking.roomNumber && `- Room ${booking.roomNumber}`}
                    </Text>
                </Group>

                {/* Date Range */}
                <Group gap="xs">
                    <IconClock size={16} color="gray" />
                    <Text size="sm">
                        {formatDate(booking.checkIn)} - {formatDate(booking.checkOut)}
                    </Text>
                </Group>

                {/* Guests */}
                <Group gap="xs">
                    <IconUsers size={16} color="gray" />
                    <Text size="sm">
                        {booking.adults} Adults {booking.children > 0 && `, ${booking.children} Children`}
                    </Text>
                </Group>

                {/* Total Price */}
                <Group gap="xs">
                    <IconCoin size={16} color="gray" />
                    <Text size="sm" fw={500} c="blue">
                        {formatCurrency(booking.totalPrice)}
                    </Text>
                </Group>

                {/* Nights */}
                <Text size="xs" c="dimmed">
                    {booking.nights} nights
                </Text>
            </Stack>

            <Divider my="sm" />

            <Group justify="space-between">
                <Button
                    variant="light"
                    size="compact-md"
                    onClick={() => onViewDetails(booking)}
                    fullWidth
                >
                    View Details
                </Button>
            </Group>
        </Card>
    );
}