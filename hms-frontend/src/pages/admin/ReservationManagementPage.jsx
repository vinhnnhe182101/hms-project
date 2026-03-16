import { useEffect, useMemo, useState } from 'react';
import {
    ActionIcon,
    Badge,
    Button,
    Group,
    LoadingOverlay,
    Pagination,
    Paper,
    Select,
    Stack,
    Table,
    Text,
    TextInput,
    Title,
} from '@mantine/core';
import { useDebouncedValue } from '@mantine/hooks';
import { notifications } from '@mantine/notifications';
import { modals } from '@mantine/modals';
import { IconRefresh, IconSearch, IconTrash } from '@tabler/icons-react';
import { reservationApi } from '../../apis/admin/reservationApi';

const reservationStatusOptions = [
    { value: 'PENDING_DEPOSIT', label: 'Pending Deposit' },
    { value: 'CONFIRMED', label: 'Confirmed' },
    { value: 'CANCELLED', label: 'Cancelled' },
    { value: 'IN_HOUSE', label: 'In House' },
    { value: 'CHECKED_OUT', label: 'Checked Out' },
    { value: 'FINISHED', label: 'Finished' },
];

const statusColorMap = {
    PENDING_DEPOSIT: 'yellow',
    CONFIRMED: 'blue',
    CANCELLED: 'red',
    IN_HOUSE: 'teal',
    CHECKED_OUT: 'orange',
    FINISHED: 'green',
};

function formatDate(value) {
    if (!value) return '-';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return '-';
    return date.toLocaleString('vi-VN');
}

function getApiErrorMessage(error, fallbackMessage) {
    return error?.response?.data?.message
        || error?.response?.data?.error
        || error?.message
        || fallbackMessage;
}

export default function ReservationManagementPage() {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [totalElements, setTotalElements] = useState(0);

    const [guestName, setGuestName] = useState('');
    const [status, setStatus] = useState(null);
    const [checkInDateFrom, setCheckInDateFrom] = useState('');
    const [checkInDateTo, setCheckInDateTo] = useState('');
    const [debouncedGuestName] = useDebouncedValue(guestName, 400);

    const queryParams = useMemo(() => ({
        page: page - 1,
        size: 10,
        guestName: debouncedGuestName.trim() || undefined,
        status: status || undefined,
        checkInDateFrom: checkInDateFrom || undefined,
        checkInDateTo: checkInDateTo || undefined,
    }), [page, debouncedGuestName, status, checkInDateFrom, checkInDateTo]);

    const fetchReservations = async () => {
        setLoading(true);
        try {
            const data = await reservationApi.getReservations(queryParams);
            setReservations(data.content);
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || 0);
        } catch (error) {
            notifications.show({
                color: 'red',
                title: 'Error',
                message: getApiErrorMessage(error, 'Failed to load reservations.'),
            });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchReservations();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [queryParams]);

    const handleClearFilters = () => {
        setGuestName('');
        setStatus(null);
        setCheckInDateFrom('');
        setCheckInDateTo('');
        setPage(1);
    };

    const handleCancelReservation = (reservation) => {
        modals.openConfirmModal({
            title: 'Cancel reservation',
            centered: true,
            children: (
                <Text size="sm">
                    Cancel reservation <b>{reservation.bookingCode}</b>?
                </Text>
            ),
            labels: { confirm: 'Cancel reservation', cancel: 'Close' },
            confirmProps: { color: 'red' },
            onConfirm: async () => {
                try {
                    await reservationApi.cancelReservation(reservation.bookingId);
                    notifications.show({
                        color: 'green',
                        title: 'Success',
                        message: `Reservation ${reservation.bookingCode} has been cancelled.`,
                    });
                    fetchReservations();
                } catch (error) {
                    notifications.show({
                        color: 'red',
                        title: 'Error',
                        message: getApiErrorMessage(error, 'Failed to cancel reservation.'),
                    });
                }
            },
        });
    };

    return (
        <div style={{ position: 'relative', minHeight: 320 }}>
            <LoadingOverlay visible={loading} zIndex={1000} overlayProps={{ blur: 2 }} />

            <Stack gap="lg">
                <Group justify="space-between" align="center">
                    <Title order={1}>Reservations</Title>
                    <Group>
                        <Button
                            variant="default"
                            leftSection={<IconRefresh size={16} />}
                            onClick={fetchReservations}
                        >
                            Refresh
                        </Button>
                    </Group>
                </Group>

                <Paper withBorder radius="lg" p="lg">
                    <Stack gap="md">
                        <Group grow align="end">
                            <TextInput
                                label="Guest name"
                                placeholder="Search by guest name"
                                leftSection={<IconSearch size={16} />}
                                value={guestName}
                                onChange={(event) => {
                                    setGuestName(event.currentTarget.value);
                                    setPage(1);
                                }}
                            />
                            <Select
                                clearable
                                label="Status"
                                placeholder="All statuses"
                                data={reservationStatusOptions}
                                value={status}
                                onChange={(value) => {
                                    setStatus(value);
                                    setPage(1);
                                }}
                            />
                            <TextInput
                                type="date"
                                label="Check-in from"
                                value={checkInDateFrom}
                                onChange={(event) => {
                                    setCheckInDateFrom(event.currentTarget.value);
                                    setPage(1);
                                }}
                            />
                            <TextInput
                                type="date"
                                label="Check-in to"
                                value={checkInDateTo}
                                onChange={(event) => {
                                    setCheckInDateTo(event.currentTarget.value);
                                    setPage(1);
                                }}
                            />
                        </Group>

                        <Group justify="space-between">
                            <Text size="sm" c="dimmed">Total: {totalElements} reservations</Text>
                            <Button variant="subtle" onClick={handleClearFilters}>Clear filters</Button>
                        </Group>

                        <Table highlightOnHover verticalSpacing="md" miw={1000}>
                            <Table.Thead>
                                <Table.Tr>
                                    <Table.Th>Code</Table.Th>
                                    <Table.Th>Guest</Table.Th>
                                    <Table.Th>Check-in</Table.Th>
                                    <Table.Th>Check-out</Table.Th>
                                    <Table.Th>Status</Table.Th>
                                    <Table.Th>Members</Table.Th>
                                    <Table.Th>Created at</Table.Th>
                                    <Table.Th>Action</Table.Th>
                                </Table.Tr>
                            </Table.Thead>
                            <Table.Tbody>
                                {reservations.length > 0 ? reservations.map((reservation) => (
                                    <Table.Tr key={reservation.bookingId}>
                                        <Table.Td fw={600}>{reservation.bookingCode || '-'}</Table.Td>
                                        <Table.Td>{reservation.customer?.fullName || reservation.customer?.email || '-'}</Table.Td>
                                        <Table.Td>{formatDate(reservation.checkInDate)}</Table.Td>
                                        <Table.Td>{formatDate(reservation.checkOutDate)}</Table.Td>
                                        <Table.Td>
                                            <Badge color={statusColorMap[reservation.status] || 'gray'} variant="light">
                                                {reservation.status || 'UNKNOWN'}
                                            </Badge>
                                        </Table.Td>
                                        <Table.Td>{reservation.numberOfMembers ?? '-'}</Table.Td>
                                        <Table.Td>{formatDate(reservation.createdAt)}</Table.Td>
                                        <Table.Td>
                                            <ActionIcon
                                                color="red"
                                                variant="subtle"
                                                disabled={reservation.status === 'CANCELLED' || reservation.status === 'FINISHED'}
                                                onClick={() => handleCancelReservation(reservation)}
                                                aria-label={`Cancel ${reservation.bookingCode}`}
                                            >
                                                <IconTrash size={16} />
                                            </ActionIcon>
                                        </Table.Td>
                                    </Table.Tr>
                                )) : (
                                    <Table.Tr>
                                        <Table.Td colSpan={8}>
                                            <Text ta="center" py="lg" c="dimmed">
                                                No reservations found.
                                            </Text>
                                        </Table.Td>
                                    </Table.Tr>
                                )}
                            </Table.Tbody>
                        </Table>

                        {totalPages > 1 && (
                            <Group justify="center" mt="sm">
                                <Pagination total={totalPages} value={page} onChange={setPage} withEdges />
                            </Group>
                        )}
                    </Stack>
                </Paper>
            </Stack>
        </div>
    );
}
