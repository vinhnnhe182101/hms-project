import { useEffect, useState } from 'react';
import {
    ActionIcon,
    Badge,
    Button,
    Card,
    Divider,
    Group,
    Modal,
    NumberInput,
    Paper,
    ScrollArea,
    Select,
    SimpleGrid,
    Stack,
    Table,
    Text,
    TextInput,
    Title,
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { modals } from '@mantine/modals';
import { notifications } from '@mantine/notifications';
import {
    getRoomsFromStorage,
    saveRoomsToStorage,
    subscribeRooms,
} from '../../utils/roomInventory';
import {
    IconBath,
    IconBed,
    IconEye,
    IconFilter,
    IconEdit,
    IconPlus,
    IconSearch,
    IconTrash,
} from '@tabler/icons-react';

const roomTypeOptions = ['Deluxe', 'Suite', 'Standard', 'Family'];
const roomStatusOptions = ['Available', 'Occupied', 'Cleaning', 'Maintenance'];

const statusColorMap = {
    Available: 'green',
    Occupied: 'red',
    Cleaning: 'yellow',
    Maintenance: 'orange',
};

const emptyRoomForm = {
    roomNumber: '',
    roomType: 'Deluxe',
    floor: 1,
    status: 'Available',
    rate: 0,
    beds: 1,
    bathrooms: 1,
};

function RoomFormModal({ opened, mode, onClose, onSubmit, initialValues }) {
    const form = useForm({
        initialValues,
        validate: {
            roomNumber: (value) => (value.trim().length < 3 ? 'Room number is required' : null),
            roomType: (value) => (!value ? 'Room type is required' : null),
            floor: (value) => (value < 1 ? 'Floor must be at least 1' : null),
            status: (value) => (!value ? 'Status is required' : null),
            rate: (value) => (value <= 0 ? 'Rate must be greater than 0' : null),
            beds: (value) => (value < 1 ? 'Beds must be at least 1' : null),
            bathrooms: (value) => (value < 1 ? 'Bathrooms must be at least 1' : null),
        },
    });

    return (
        <Modal
            opened={opened}
            onClose={onClose}
            title={mode === 'create' ? 'Add New Room' : 'Edit Room'}
            centered
            size="lg"
        >
            <form onSubmit={form.onSubmit((values) => onSubmit(values, form))}>
                <Stack>
                    <SimpleGrid cols={{ base: 1, sm: 2 }}>
                        <TextInput
                            label="Room Number"
                            placeholder="Room #101"
                            {...form.getInputProps('roomNumber')}
                        />
                        <Select
                            label="Room Type"
                            data={roomTypeOptions}
                            {...form.getInputProps('roomType')}
                        />
                    </SimpleGrid>

                    <SimpleGrid cols={{ base: 1, sm: 2, md: 4 }}>
                        <NumberInput
                            label="Floor"
                            min={1}
                            allowDecimal={false}
                            {...form.getInputProps('floor')}
                        />
                        <Select
                            label="Status"
                            data={roomStatusOptions}
                            {...form.getInputProps('status')}
                        />
                        <NumberInput
                            label="Rate ($)"
                            min={1}
                            allowDecimal={false}
                            {...form.getInputProps('rate')}
                        />
                        <NumberInput
                            label="Beds"
                            min={1}
                            allowDecimal={false}
                            {...form.getInputProps('beds')}
                        />
                    </SimpleGrid>

                    <NumberInput
                        label="Bathrooms"
                        min={1}
                        allowDecimal={false}
                        {...form.getInputProps('bathrooms')}
                    />

                    <Group justify="flex-end">
                        <Button variant="default" onClick={onClose}>Cancel</Button>
                        <Button type="submit">{mode === 'create' ? 'Create room' : 'Save changes'}</Button>
                    </Group>
                </Stack>
            </form>
        </Modal>
    );
}

function RoomDetailsModal({ opened, room, onClose }) {
    if (!room) {
        return null;
    }

    return (
        <Modal opened={opened} onClose={onClose} title={room.roomNumber} centered size="md">
            <Stack gap="md">
                <Group justify="space-between">
                    <Text fw={600}>Room Type</Text>
                    <Text>{room.roomType}</Text>
                </Group>
                <Group justify="space-between">
                    <Text fw={600}>Floor</Text>
                    <Text>{room.floor}</Text>
                </Group>
                <Group justify="space-between">
                    <Text fw={600}>Rate per night</Text>
                    <Text>${room.rate}</Text>
                </Group>
                <Group justify="space-between">
                    <Text fw={600}>Facilities</Text>
                    <Group gap="xs">
                        <Badge variant="light" leftSection={<IconBed size={12} />}>{room.beds} beds</Badge>
                        <Badge variant="light" leftSection={<IconBath size={12} />}>{room.bathrooms} baths</Badge>
                    </Group>
                </Group>
                <Group justify="space-between">
                    <Text fw={600}>Status</Text>
                    <Badge color={statusColorMap[room.status]} variant="light">{room.status}</Badge>
                </Group>
            </Stack>
        </Modal>
    );
}

export default function RoomManagementPage() {
    const [rooms, setRooms] = useState(() => getRoomsFromStorage());
    const [searchValue, setSearchValue] = useState('');
    const [statusFilter, setStatusFilter] = useState(null);
    const [typeFilter, setTypeFilter] = useState(null);
    const [selectedRoom, setSelectedRoom] = useState(null);
    const [modalMode, setModalMode] = useState('create');
    const [formOpened, setFormOpened] = useState(false);
    const [detailsOpened, setDetailsOpened] = useState(false);

    useEffect(() => {
        return subscribeRooms(setRooms);
    }, []);

    const updateRooms = (updater) => {
        setRooms((currentRooms) => {
            const nextRooms = typeof updater === 'function' ? updater(currentRooms) : updater;
            saveRoomsToStorage(nextRooms);
            return nextRooms;
        });
    };

    const filteredRooms = rooms.filter((room) => {
        const query = searchValue.trim().toLowerCase();
        const matchesQuery = !query
            || room.roomNumber.toLowerCase().includes(query)
            || room.roomType.toLowerCase().includes(query)
            || String(room.floor).includes(query);
        const matchesStatus = !statusFilter || room.status === statusFilter;
        const matchesType = !typeFilter || room.roomType === typeFilter;

        return matchesQuery && matchesStatus && matchesType;
    });

    const roomStats = {
        total: rooms.length,
        available: rooms.filter((room) => room.status === 'Available').length,
        occupied: rooms.filter((room) => room.status === 'Occupied').length,
        maintenance: rooms.filter((room) => room.status === 'Maintenance').length,
    };

    const openCreateModal = () => {
        setModalMode('create');
        setSelectedRoom(emptyRoomForm);
        setFormOpened(true);
    };

    const openEditModal = (room) => {
        setModalMode('edit');
        setSelectedRoom(room);
        setFormOpened(true);
    };

    const openDetailsModal = (room) => {
        setSelectedRoom(room);
        setDetailsOpened(true);
    };

    const handleSubmit = (values, form) => {
        if (modalMode === 'create') {
            const newRoom = {
                ...values,
                id: Date.now(),
                floor: Number(values.floor),
                rate: Number(values.rate),
                beds: Number(values.beds),
                bathrooms: Number(values.bathrooms),
            };

            updateRooms((currentRooms) => [newRoom, ...currentRooms]);
            notifications.show({ color: 'green', message: 'Room created successfully.' });
        } else {
            updateRooms((currentRooms) => currentRooms.map((room) => (
                room.id === selectedRoom.id
                    ? {
                        ...room,
                        ...values,
                        floor: Number(values.floor),
                        rate: Number(values.rate),
                        beds: Number(values.beds),
                        bathrooms: Number(values.bathrooms),
                    }
                    : room
            )));
            notifications.show({ color: 'blue', message: 'Room updated successfully.' });
        }

        form.reset();
        setFormOpened(false);
    };

    const handleDelete = (room) => {
        modals.openConfirmModal({
            title: 'Delete room',
            centered: true,
            children: (
                <Text size="sm">
                    Delete {room.roomNumber}? This action only updates mock data on the current screen.
                </Text>
            ),
            labels: { confirm: 'Delete', cancel: 'Cancel' },
            confirmProps: { color: 'red' },
            onConfirm: () => {
                updateRooms((currentRooms) => currentRooms.filter((item) => item.id !== room.id));
                notifications.show({ color: 'red', message: `${room.roomNumber} deleted.` });
            },
        });
    };

    return (
        <Stack gap="lg">
            <Group justify="space-between" align="flex-start">
                <div>
                    <Title order={1}>Manage Rooms</Title>
                    <Text c="dimmed" mt={4}>Room list management with search, filters and quick actions.</Text>
                </div>

                <Button leftSection={<IconPlus size={16} />} onClick={openCreateModal}>
                    Add New Room
                </Button>
            </Group>

            <SimpleGrid cols={{ base: 1, sm: 2, xl: 4 }} spacing="md">
                <Card withBorder radius="md" padding="lg">
                    <Text c="dimmed" size="sm">Total rooms</Text>
                    <Title order={2} mt="xs">{roomStats.total}</Title>
                </Card>
                <Card withBorder radius="md" padding="lg">
                    <Text c="dimmed" size="sm">Available</Text>
                    <Title order={2} mt="xs">{roomStats.available}</Title>
                </Card>
                <Card withBorder radius="md" padding="lg">
                    <Text c="dimmed" size="sm">Occupied</Text>
                    <Title order={2} mt="xs">{roomStats.occupied}</Title>
                </Card>
                <Card withBorder radius="md" padding="lg">
                    <Text c="dimmed" size="sm">Maintenance</Text>
                    <Title order={2} mt="xs">{roomStats.maintenance}</Title>
                </Card>
            </SimpleGrid>

            <Paper withBorder radius="lg" p="lg">
                <Stack gap="lg">
                    <div>
                        <Title order={2}>Room List Management</Title>
                        <Text c="dimmed" mt={4}>Filter rooms by keyword, status and type.</Text>
                    </div>

                    <Group align="end" grow>
                        <TextInput
                            label="Search"
                            placeholder="Search room number, type or floor"
                            leftSection={<IconSearch size={16} />}
                            value={searchValue}
                            onChange={(event) => setSearchValue(event.currentTarget.value)}
                        />
                        <Select
                            clearable
                            label="Filter by Status"
                            placeholder="All statuses"
                            leftSection={<IconFilter size={16} />}
                            data={roomStatusOptions}
                            value={statusFilter}
                            onChange={setStatusFilter}
                        />
                        <Select
                            clearable
                            label="Filter by Type"
                            placeholder="All room types"
                            data={roomTypeOptions}
                            value={typeFilter}
                            onChange={setTypeFilter}
                        />
                    </Group>

                    <Divider />

                    <ScrollArea>
                        <Table highlightOnHover verticalSpacing="md" miw={900}>
                            <Table.Thead>
                                <Table.Tr>
                                    <Table.Th>Room #</Table.Th>
                                    <Table.Th>Room Type</Table.Th>
                                    <Table.Th>Floor</Table.Th>
                                    <Table.Th>Status</Table.Th>
                                    <Table.Th>Rate per Night ($)</Table.Th>
                                    <Table.Th>Facilities</Table.Th>
                                    <Table.Th>Action</Table.Th>
                                </Table.Tr>
                            </Table.Thead>
                            <Table.Tbody>
                                {filteredRooms.length > 0 ? filteredRooms.map((room) => (
                                    <Table.Tr key={room.id}>
                                        <Table.Td fw={600}>{room.roomNumber}</Table.Td>
                                        <Table.Td>
                                            <Group gap="xs">
                                                <IconBed size={16} />
                                                <Text>{room.roomType}</Text>
                                            </Group>
                                        </Table.Td>
                                        <Table.Td>{room.floor}</Table.Td>
                                        <Table.Td>
                                            <Badge color={statusColorMap[room.status]} variant="light">
                                                {room.status}
                                            </Badge>
                                        </Table.Td>
                                        <Table.Td>${room.rate}</Table.Td>
                                        <Table.Td>
                                            <Group gap="xs">
                                                <Badge variant="light">{room.beds} beds</Badge>
                                                <Badge variant="light">{room.bathrooms} baths</Badge>
                                            </Group>
                                        </Table.Td>
                                        <Table.Td>
                                            <Group gap="xs" wrap="nowrap">
                                                <ActionIcon
                                                    variant="subtle"
                                                    color="blue"
                                                    onClick={() => openDetailsModal(room)}
                                                    aria-label={`View ${room.roomNumber}`}
                                                >
                                                    <IconEye size={18} />
                                                </ActionIcon>
                                                <ActionIcon
                                                    variant="subtle"
                                                    color="dark"
                                                    onClick={() => openEditModal(room)}
                                                    aria-label={`Edit ${room.roomNumber}`}
                                                >
                                                    <IconEdit size={18} />
                                                </ActionIcon>
                                                <ActionIcon
                                                    variant="subtle"
                                                    color="red"
                                                    onClick={() => handleDelete(room)}
                                                    aria-label={`Delete ${room.roomNumber}`}
                                                >
                                                    <IconTrash size={18} />
                                                </ActionIcon>
                                            </Group>
                                        </Table.Td>
                                    </Table.Tr>
                                )) : (
                                    <Table.Tr>
                                        <Table.Td colSpan={7}>
                                            <Text ta="center" py="lg" c="dimmed">
                                                No rooms matched the current filters.
                                            </Text>
                                        </Table.Td>
                                    </Table.Tr>
                                )}
                            </Table.Tbody>
                        </Table>
                    </ScrollArea>
                </Stack>
            </Paper>

            <RoomFormModal
                opened={formOpened}
                mode={modalMode}
                initialValues={selectedRoom || emptyRoomForm}
                onClose={() => setFormOpened(false)}
                onSubmit={handleSubmit}
            />

            <RoomDetailsModal
                opened={detailsOpened}
                room={selectedRoom && selectedRoom.id ? selectedRoom : null}
                onClose={() => setDetailsOpened(false)}
            />
        </Stack>
    );
}