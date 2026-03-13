import { useState } from 'react';
import {
    ActionIcon,
    Badge,
    Button,
    Group,
    Modal,
    MultiSelect,
    NumberInput,
    Paper,
    ScrollArea,
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
    IconBath,
    IconDeviceTv,
    IconEye,
    IconEdit,
    IconPlus,
    IconSearch,
    IconSwimming,
    IconTrash,
    IconWifi,
    IconBarbell,
    IconBottle,
} from '@tabler/icons-react';

const amenityOptions = [
    'Wifi',
    'TV',
    'Bath',
    'Pool Access',
    'Mini-bar',
    'Gym Access',
];

const initialRoomTypes = [
    {
        id: 1,
        name: 'Standard',
        standardOccupancy: 1,
        maxOccupancy: 2,
        baseRate: 30,
        amenities: ['Wifi', 'TV', 'Bath'],
    },
    {
        id: 2,
        name: 'Deluxe',
        standardOccupancy: 1,
        maxOccupancy: 2,
        baseRate: 30,
        amenities: ['Wifi', 'TV', 'Bath', 'Pool Access', 'Mini-bar'],
    },
    {
        id: 3,
        name: 'Suite',
        standardOccupancy: 2,
        maxOccupancy: 4,
        baseRate: 50,
        amenities: ['Wifi', 'TV', 'Bath', 'Pool Access', 'Mini-bar', 'Gym Access'],
    },
    {
        id: 4,
        name: 'Family',
        standardOccupancy: 3,
        maxOccupancy: 6,
        baseRate: 50,
        amenities: ['Wifi', 'TV', 'Bath'],
    },
];

const emptyRoomType = {
    name: '',
    standardOccupancy: 1,
    maxOccupancy: 2,
    baseRate: 90,
    amenities: [],
};

function getAmenityIcon(amenity) {
    switch (amenity) {
        case 'Wifi':
            return <IconWifi size={14} />;
        case 'TV':
            return <IconDeviceTv size={14} />;
        case 'Bath':
            return <IconBath size={14} />;
        case 'Pool Access':
            return <IconSwimming size={14} />;
        case 'Mini-bar':
            return <IconBottle size={14} />;
        case 'Gym Access':
            return <IconBarbell size={14} />;
        default:
            return null;
    }
}

function RoomTypeFormModal({ opened, mode, initialValues, onClose, onSubmit }) {
    const form = useForm({
        mode: 'controlled',
        initialValues,
        validate: {
            name: (value) => (value.trim().length < 2 ? 'Type name is required' : null),
            standardOccupancy: (value) => (value < 1 ? 'Standard occupancy must be at least 1' : null),
            maxOccupancy: (value, values) => (
                value < values.standardOccupancy ? 'Max occupancy must be greater than or equal to standard occupancy' : null
            ),
            baseRate: (value) => (value <= 0 ? 'Base rate must be greater than 0' : null),
            amenities: (value) => (value.length === 0 ? 'Select at least one amenity' : null),
        },
    });

    return (
        <Modal
            opened={opened}
            onClose={onClose}
            title={mode === 'create' ? 'Add New Room Type' : 'Edit Room Type'}
            centered
            size="lg"
        >
            <form onSubmit={form.onSubmit(onSubmit)}>
                <Stack>
                    <TextInput
                        label="Type Name"
                        placeholder="Deluxe"
                        {...form.getInputProps('name')}
                    />

                    <Group grow align="start">
                        <NumberInput
                            label="Standard Occupancy"
                            min={1}
                            allowDecimal={false}
                            {...form.getInputProps('standardOccupancy')}
                        />
                        <NumberInput
                            label="Max Occupancy"
                            min={1}
                            allowDecimal={false}
                            {...form.getInputProps('maxOccupancy')}
                        />
                        <NumberInput
                            label="Base Rate per Night ($)"
                            min={1}
                            allowDecimal={false}
                            {...form.getInputProps('baseRate')}
                        />
                    </Group>

                    <MultiSelect
                        label="Amenities"
                        placeholder="Select amenities"
                        data={amenityOptions}
                        searchable
                        hidePickedOptions
                        {...form.getInputProps('amenities')}
                    />

                    <Group justify="flex-end">
                        <Button variant="default" onClick={onClose}>Cancel</Button>
                        <Button type="submit">{mode === 'create' ? 'Create room type' : 'Save changes'}</Button>
                    </Group>
                </Stack>
            </form>
        </Modal>
    );
}

function RoomTypeDetailsModal({ opened, roomType, onClose }) {
    if (!roomType) {
        return null;
    }

    return (
        <Modal opened={opened} onClose={onClose} title={roomType.name} centered>
            <Stack gap="md">
                <Group justify="space-between">
                    <Text fw={600}>Standard Occupancy</Text>
                    <Text>{roomType.standardOccupancy}</Text>
                </Group>
                <Group justify="space-between">
                    <Text fw={600}>Max Occupancy</Text>
                    <Text>{roomType.maxOccupancy}</Text>
                </Group>
                <Group justify="space-between">
                    <Text fw={600}>Base Rate</Text>
                    <Text>${roomType.baseRate}</Text>
                </Group>
                <div>
                    <Text fw={600} mb="xs">Amenities</Text>
                    <Group gap="xs">
                        {roomType.amenities.map((amenity) => (
                            <Badge key={amenity} variant="light" color="gray" leftSection={getAmenityIcon(amenity)}>
                                {amenity}
                            </Badge>
                        ))}
                    </Group>
                </div>
            </Stack>
        </Modal>
    );
}

export default function RoomTypesPage() {
    const [roomTypes, setRoomTypes] = useState(initialRoomTypes);
    const [searchValue, setSearchValue] = useState('');
    const [selectedRoomType, setSelectedRoomType] = useState(null);
    const [modalMode, setModalMode] = useState('create');
    const [formOpened, setFormOpened] = useState(false);
    const [detailsOpened, setDetailsOpened] = useState(false);

    const filteredRoomTypes = roomTypes.filter((roomType) => {
        const query = searchValue.trim().toLowerCase();
        if (!query) {
            return true;
        }

        return roomType.name.toLowerCase().includes(query)
            || roomType.amenities.some((amenity) => amenity.toLowerCase().includes(query));
    });

    const openCreateModal = () => {
        setModalMode('create');
        setSelectedRoomType(emptyRoomType);
        setFormOpened(true);
    };

    const openEditModal = (roomType) => {
        setModalMode('edit');
        setSelectedRoomType(roomType);
        setFormOpened(true);
    };

    const openDetailsModal = (roomType) => {
        setSelectedRoomType(roomType);
        setDetailsOpened(true);
    };

    const handleSubmit = (values) => {
        const normalizedValues = {
            ...values,
            standardOccupancy: Number(values.standardOccupancy),
            maxOccupancy: Number(values.maxOccupancy),
            baseRate: Number(values.baseRate),
        };

        if (modalMode === 'create') {
            setRoomTypes((currentRoomTypes) => [
                { id: Date.now(), ...normalizedValues },
                ...currentRoomTypes,
            ]);
            notifications.show({ color: 'green', message: 'Room type created successfully.' });
        } else {
            setRoomTypes((currentRoomTypes) => currentRoomTypes.map((roomType) => (
                roomType.id === selectedRoomType.id
                    ? { ...roomType, ...normalizedValues }
                    : roomType
            )));
            notifications.show({ color: 'blue', message: 'Room type updated successfully.' });
        }

        setFormOpened(false);
    };

    const handleDelete = (roomType) => {
        modals.openConfirmModal({
            title: 'Delete room type',
            centered: true,
            children: <Text size="sm">Delete {roomType.name}? This only affects the mock data on this screen.</Text>,
            labels: { confirm: 'Delete', cancel: 'Cancel' },
            confirmProps: { color: 'red' },
            onConfirm: () => {
                setRoomTypes((currentRoomTypes) => currentRoomTypes.filter((item) => item.id !== roomType.id));
                notifications.show({ color: 'red', message: `${roomType.name} deleted.` });
            },
        });
    };

    return (
        <Stack gap="lg">
            <Group justify="space-between" align="center">
                <Title order={1}>Room Types</Title>
                <Button leftSection={<IconPlus size={16} />} onClick={openCreateModal}>
                    Add New Room Type
                </Button>
            </Group>

            <Paper withBorder radius="lg" p="lg">
                <Stack gap="lg">
                    <TextInput
                        placeholder="Search"
                        leftSection={<IconSearch size={16} />}
                        value={searchValue}
                        onChange={(event) => setSearchValue(event.currentTarget.value)}
                        maw={460}
                    />

                    <ScrollArea>
                        <Table highlightOnHover verticalSpacing="lg" miw={980}>
                            <Table.Thead>
                                <Table.Tr>
                                    <Table.Th>Type Name</Table.Th>
                                    <Table.Th>Standard Occupancy</Table.Th>
                                    <Table.Th>Max Occupancy</Table.Th>
                                    <Table.Th>Base Rate per Night ($)</Table.Th>
                                    <Table.Th>Amenities</Table.Th>
                                    <Table.Th>Action</Table.Th>
                                </Table.Tr>
                            </Table.Thead>
                            <Table.Tbody>
                                {filteredRoomTypes.length > 0 ? filteredRoomTypes.map((roomType) => (
                                    <Table.Tr key={roomType.id}>
                                        <Table.Td fw={600}>{roomType.name}</Table.Td>
                                        <Table.Td>{roomType.standardOccupancy}</Table.Td>
                                        <Table.Td>{roomType.maxOccupancy}</Table.Td>
                                        <Table.Td>${roomType.baseRate}</Table.Td>
                                        <Table.Td>
                                            <Group gap="xs">
                                                {roomType.amenities.map((amenity) => (
                                                    <Badge
                                                        key={amenity}
                                                        variant="light"
                                                        color="gray"
                                                        leftSection={getAmenityIcon(amenity)}
                                                    >
                                                        {amenity}
                                                    </Badge>
                                                ))}
                                            </Group>
                                        </Table.Td>
                                        <Table.Td>
                                            <Group gap="xs" wrap="nowrap">
                                                <ActionIcon
                                                    variant="subtle"
                                                    color="blue"
                                                    onClick={() => openDetailsModal(roomType)}
                                                    aria-label={`View ${roomType.name}`}
                                                >
                                                    <IconEye size={18} />
                                                </ActionIcon>
                                                <ActionIcon
                                                    variant="subtle"
                                                    color="dark"
                                                    onClick={() => openEditModal(roomType)}
                                                    aria-label={`Edit ${roomType.name}`}
                                                >
                                                    <IconEdit size={18} />
                                                </ActionIcon>
                                                <ActionIcon
                                                    variant="subtle"
                                                    color="red"
                                                    onClick={() => handleDelete(roomType)}
                                                    aria-label={`Delete ${roomType.name}`}
                                                >
                                                    <IconTrash size={18} />
                                                </ActionIcon>
                                            </Group>
                                        </Table.Td>
                                    </Table.Tr>
                                )) : (
                                    <Table.Tr>
                                        <Table.Td colSpan={6}>
                                            <Text ta="center" py="lg" c="dimmed">
                                                No room types matched the current search.
                                            </Text>
                                        </Table.Td>
                                    </Table.Tr>
                                )}
                            </Table.Tbody>
                        </Table>
                    </ScrollArea>
                </Stack>
            </Paper>

            <RoomTypeFormModal
                key={selectedRoomType?.id ?? 'new'}
                opened={formOpened}
                mode={modalMode}
                initialValues={selectedRoomType || emptyRoomType}
                onClose={() => setFormOpened(false)}
                onSubmit={handleSubmit}
            />

            <RoomTypeDetailsModal
                opened={detailsOpened}
                roomType={selectedRoomType && selectedRoomType.id ? selectedRoomType : null}
                onClose={() => setDetailsOpened(false)}
            />
        </Stack>
    );
}