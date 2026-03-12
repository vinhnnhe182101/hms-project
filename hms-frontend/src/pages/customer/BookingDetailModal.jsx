// src/pages/customer/BookingDetailModal.jsx
import { Modal, Stack, Group, Text, Badge, Divider, Table, Paper, ThemeIcon } from '@mantine/core';
import {
    IconCalendar,
    IconDoor,
    IconUsers,
    IconCoin,
    IconClock,
    IconReceipt,
    IconCheck,
    IconX
} from '@tabler/icons-react';

export function BookingDetailModal({ opened, onClose, booking }) {
    if (!booking) return null;

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('en-US', {
            weekday: 'long',
            day: '2-digit',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    };

    const statusColors = {
        confirmed: 'blue',
        pending: 'yellow',
        completed: 'green',
        cancelled: 'red',
        'in-house': 'teal'
    };

    const paymentStatusColors = {
        paid: 'green',
        unpaid: 'red',
        partially: 'yellow'
    };

    return (
        <Modal
            opened={opened}
            onClose={onClose}
            title={`Booking Details - ${booking.code}`}
            size="lg"
            centered
        >
            <Stack>
                {/* Status Badge */}
                <Group justify="space-between">
                    <Badge
                        color={statusColors[booking.status?.toLowerCase()]}
                        size="lg"
                        variant="filled"
                    >
                        {booking.status}
                    </Badge>
                    <Badge
                        color={paymentStatusColors[booking.paymentStatus?.toLowerCase()]}
                        size="lg"
                        variant="light"
                    >
                        Payment: {booking.paymentStatus}
                    </Badge>
                </Group>

                {/* Room Information */}
                <Paper withBorder p="md" radius="md">
                    <Text fw={500} mb="sm">Room Information</Text>
                    <Stack gap="xs">
                        <Group gap="xs">
                            <ThemeIcon size="sm" color="blue" variant="light">
                                <IconDoor size={12} />
                            </ThemeIcon>
                            <Text size="sm">
                                {booking.roomType} - Room {booking.roomNumber || 'Not assigned'}
                            </Text>
                        </Group>
                        <Group gap="xs">
                            <ThemeIcon size="sm" color="blue" variant="light">
                                <IconUsers size={12} />
                            </ThemeIcon>
                            <Text size="sm">
                                {booking.adults} Adults {booking.children > 0 && `, ${booking.children} Children`}
                            </Text>
                        </Group>
                    </Stack>
                </Paper>

                {/* Date Information */}
                <Paper withBorder p="md" radius="md">
                    <Text fw={500} mb="sm">Stay Details</Text>
                    <Stack gap="xs">
                        <Group gap="xs">
                            <ThemeIcon size="sm" color="green" variant="light">
                                <IconCalendar size={12} />
                            </ThemeIcon>
                            <div>
                                <Text size="sm" fw={500}>Check-in</Text>
                                <Text size="sm">{formatDate(booking.checkIn)}</Text>
                            </div>
                        </Group>
                        <Group gap="xs">
                            <ThemeIcon size="sm" color="red" variant="light">
                                <IconCalendar size={12} />
                            </ThemeIcon>
                            <div>
                                <Text size="sm" fw={500}>Check-out</Text>
                                <Text size="sm">{formatDate(booking.checkOut)}</Text>
                            </div>
                        </Group>
                        <Text size="sm" c="dimmed" mt="xs">
                            Total: {booking.nights} nights
                        </Text>
                    </Stack>
                </Paper>

                {/* Price Breakdown */}
                <Paper withBorder p="md" radius="md">
                    <Text fw={500} mb="sm">Price Breakdown</Text>
                    <Stack gap="xs">
                        <Group justify="space-between">
                            <Text size="sm">Room Rate ({booking.nights} nights)</Text>
                            <Text size="sm">{formatCurrency(booking.roomPrice)}</Text>
                        </Group>

                        {booking.services && booking.services.length > 0 && (
                            <>
                                <Divider />
                                <Text size="sm" fw={500}>Services</Text>
                                {booking.services.map((service, index) => (
                                    <Group key={index} justify="space-between">
                                        <Text size="sm">{service.name} x{service.quantity}</Text>
                                        <Text size="sm">{formatCurrency(service.price)}</Text>
                                    </Group>
                                ))}
                            </>
                        )}

                        {booking.minibar && booking.minibar.length > 0 && (
                            <>
                                <Divider />
                                <Text size="sm" fw={500}>Minibar</Text>
                                {booking.minibar.map((item, index) => (
                                    <Group key={index} justify="space-between">
                                        <Text size="sm">{item.name} x{item.quantity}</Text>
                                        <Text size="sm">{formatCurrency(item.price)}</Text>
                                    </Group>
                                ))}
                            </>
                        )}

                        {booking.damageFee > 0 && (
                            <>
                                <Divider />
                                <Group justify="space-between">
                                    <Text size="sm" c="red">Damage Fee</Text>
                                    <Text size="sm" c="red">{formatCurrency(booking.damageFee)}</Text>
                                </Group>
                            </>
                        )}

                        <Divider my="sm" />

                        <Group justify="space-between">
                            <Text fw={700} size="lg">Total</Text>
                            <Text fw={700} size="lg" c="blue">
                                {formatCurrency(booking.totalPrice)}
                            </Text>
                        </Group>

                        <Group justify="space-between">
                            <Text size="sm">Paid</Text>
                            <Text size="sm" c="green">{formatCurrency(booking.paidAmount || 0)}</Text>
                        </Group>

                        {booking.balance > 0 && (
                            <Group justify="space-between">
                                <Text size="sm" fw={500} c="red">Balance Due</Text>
                                <Text size="sm" fw={500} c="red">
                                    {formatCurrency(booking.balance)}
                                </Text>
                            </Group>
                        )}
                    </Stack>
                </Paper>

                {/* Payment History */}
                {booking.payments && booking.payments.length > 0 && (
                    <Paper withBorder p="md" radius="md">
                        <Text fw={500} mb="sm">Payment History</Text>
                        <Table>
                            <Table.Thead>
                                <Table.Tr>
                                    <Table.Th>Date</Table.Th>
                                    <Table.Th>Method</Table.Th>
                                    <Table.Th>Amount</Table.Th>
                                    <Table.Th>Status</Table.Th>
                                </Table.Tr>
                            </Table.Thead>
                            <Table.Tbody>
                                {booking.payments.map((payment, index) => (
                                    <Table.Tr key={index}>
                                        <Table.Td>{formatDate(payment.date)}</Table.Td>
                                        <Table.Td>{payment.method}</Table.Td>
                                        <Table.Td>{formatCurrency(payment.amount)}</Table.Td>
                                        <Table.Td>
                                            <Badge
                                                color={payment.status === 'success' ? 'green' : 'yellow'}
                                                size="sm"
                                            >
                                                {payment.status}
                                            </Badge>
                                        </Table.Td>
                                    </Table.Tr>
                                ))}
                            </Table.Tbody>
                        </Table>
                    </Paper>
                )}

                {/* Actions */}
                {booking.status?.toLowerCase() === 'confirmed' && (
                    <Button color="red" variant="light" fullWidth>
                        Cancel Booking
                    </Button>
                )}

                {booking.status?.toLowerCase() === 'completed' && (
                    <Button variant="light" fullWidth>
                        Write a Review
                    </Button>
                )}
            </Stack>
        </Modal>
    );
}