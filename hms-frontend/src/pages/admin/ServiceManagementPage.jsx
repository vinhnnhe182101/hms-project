import { useState } from 'react';
import {
    ActionIcon,
    Badge,
    Button,
    Divider,
    Group,
    Modal,
    NumberInput,
    Paper,
    ScrollArea,
    Select,
    Stack,
    Switch,
    Table,
    Text,
    Textarea,
    TextInput,
    Title,
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { modals } from '@mantine/modals';
import { notifications } from '@mantine/notifications';
import {
    IconCar,
    IconEye,
    IconMassage,
    IconEdit,
    IconPlus,
    IconSearch,
    IconToolsKitchen2,
    IconTrash,
    IconWashMachine,
} from '@tabler/icons-react';

const serviceTypeOptions = ['Dining', 'Spa', 'Transportation', 'Fitness', 'Laundry'];

const typeIconMap = {
    Dining: IconToolsKitchen2,
    Spa: IconMassage,
    Transportation: IconCar,
    Fitness: IconMassage,
    Laundry: IconWashMachine,
};

const typeColorMap = {
    Dining: 'orange',
    Spa: 'pink',
    Transportation: 'blue',
    Fitness: 'green',
    Laundry: 'cyan',
};

const initialServices = [
    { id: 1, name: 'Airport Transfer', type: 'Transportation', description: 'Premium airport pickup and drop-off service available 24/7.', price: 10, duration: '4 min', available: true },
    { id: 2, name: 'Room Service', type: 'Dining', description: 'Restaurant-quality meals delivered directly to your room.', price: 20, duration: '30 min', available: true },
    { id: 3, name: 'Couples Massage', type: 'Spa', description: 'Relaxing couples massage with essential oils in a private suite.', price: 80, duration: '60 min', available: true },
    { id: 4, name: 'Gym Access', type: 'Fitness', description: 'Full access to the fitness center with modern equipment.', price: 15, duration: '120 min', available: true },
    { id: 5, name: 'Laundry & Pressing', type: 'Laundry', description: 'Same-day laundry and pressing service for your garments.', price: 12, duration: '4 hr', available: false },
    { id: 6, name: 'City Tour', type: 'Transportation', description: 'Guided city tour by luxury vehicle with a personal guide.', price: 30, duration: '3 hr', available: true },
    { id: 7, name: 'Breakfast in Bed', type: 'Dining', description: 'Continental breakfast served to your room every morning.', price: 18, duration: '15 min', available: true },
    { id: 8, name: 'Deep Tissue Massage', type: 'Spa', description: 'Therapeutic deep tissue massage to relieve muscle tension.', price: 60, duration: '45 min', available: false },
];

const emptyService = {
    name: '',
    type: 'Dining',
    description: '',
    price: 0,
    duration: '',
    available: true,
};

function ServiceFormModal({ opened, mode, initialValues, onClose, onSubmit }) {
    const form = useForm({
        mode: 'controlled',
        initialValues,
        validate: {
            name: (value) => (value.trim().length < 2 ? 'Service name is required' : null),
            type: (value) => (!value ? 'Type is required' : null),
            description: (value) => (value.trim().length < 5 ? 'Description is required' : null),
            price: (value) => (value <= 0 ? 'Price must be greater than 0' : null),
            duration: (value) => (value.trim().length < 1 ? 'Duration is required' : null),
        },
    });

    return (
        <Modal
            opened={opened}
            onClose={onClose}
            title={mode === 'create' ? 'Add New Service' : 'Edit Service'}
            centered
            size="lg"
        >
            <form onSubmit={form.onSubmit(onSubmit)}>
                <Stack>
                    <Group grow align="start">
                        <TextInput
                            label="Service Name"
                            placeholder="e.g. Airport Transfer"
                            {...form.getInputProps('name')}
                        />
                        <Select
                            label="Type"
                            data={serviceTypeOptions}
                            {...form.getInputProps('type')}
                        />
                    </Group>

                    <Textarea
                        label="Description"
                        placeholder="Describe the service"
                        autosize
                        minRows={2}
                        maxRows={4}
                        {...form.getInputProps('description')}
                    />

                    <Group grow align="start">
                        <NumberInput
                            label="Price ($)"
                            min={1}
                            allowDecimal={false}
                            {...form.getInputProps('price')}
                        />
                        <TextInput
                            label="Duration"
                            placeholder="e.g. 30 min, 2 hr"
                            {...form.getInputProps('duration')}
                        />
                    </Group>

                    <Switch
                        label="Available"
                        checked={form.values.available}
                        onChange={(event) => form.setFieldValue('available', event.currentTarget.checked)}
                    />

                    <Group justify="flex-end">
                        <Button variant="default" onClick={onClose}>Cancel</Button>
                        <Button type="submit">
                            {mode === 'create' ? 'Create service' : 'Save changes'}
                        </Button>
                    </Group>
                </Stack>
            </form>
        </Modal>
    );
}

function ServiceDetailsModal({ opened, service, onClose }) {
    if (!service) {
        return null;
    }

    const TypeIcon = typeIconMap[service.type] || IconToolsKitchen2;

    return (
        <Modal opened={opened} onClose={onClose} title={service.name} centered>
            <Stack gap="md">
                <Group justify="space-between">
                    <Text fw={600}>Type</Text>
                    <Badge color={typeColorMap[service.type]} variant="light" leftSection={<TypeIcon size={12} />}>
                        {service.type}
                    </Badge>
                </Group>
                <Group justify="space-between">
                    <Text fw={600}>Price</Text>
                    <Text>${service.price}</Text>
                </Group>
                <Group justify="space-between">
                    <Text fw={600}>Duration</Text>
                    <Text>{service.duration}</Text>
                </Group>
                <Group justify="space-between">
                    <Text fw={600}>Availability</Text>
                    <Badge color={service.available ? 'green' : 'red'} variant="light">
                        {service.available ? 'Yes' : 'No'}
                    </Badge>
                </Group>
                <div>
                    <Text fw={600} mb={4}>Description</Text>
                    <Text c="dimmed" size="sm">{service.description}</Text>
                </div>
            </Stack>
        </Modal>
    );
}

export default function ServiceManagementPage() {
    const [services, setServices] = useState(initialServices);
    const [searchValue, setSearchValue] = useState('');
    const [typeFilter, setTypeFilter] = useState(null);
    const [selectedService, setSelectedService] = useState(null);
    const [modalMode, setModalMode] = useState('create');
    const [formOpened, setFormOpened] = useState(false);
    const [detailsOpened, setDetailsOpened] = useState(false);

    const filteredServices = services.filter((service) => {
        const query = searchValue.trim().toLowerCase();
        const matchesQuery = !query
            || service.name.toLowerCase().includes(query)
            || service.description.toLowerCase().includes(query);
        const matchesType = !typeFilter || service.type === typeFilter;

        return matchesQuery && matchesType;
    });

    const openCreateModal = () => {
        setModalMode('create');
        setSelectedService(emptyService);
        setFormOpened(true);
    };

    const openEditModal = (service) => {
        setModalMode('edit');
        setSelectedService(service);
        setFormOpened(true);
    };

    const openDetailsModal = (service) => {
        setSelectedService(service);
        setDetailsOpened(true);
    };

    const handleSubmit = (values) => {
        const normalized = {
            ...values,
            price: Number(values.price),
        };

        if (modalMode === 'create') {
            setServices((prev) => [{ id: Date.now(), ...normalized }, ...prev]);
            notifications.show({ color: 'green', message: 'Service created successfully.' });
        } else {
            setServices((prev) => prev.map((s) =>
                s.id === selectedService.id ? { ...s, ...normalized } : s
            ));
            notifications.show({ color: 'blue', message: 'Service updated successfully.' });
        }

        setFormOpened(false);
    };

    const handleDelete = (service) => {
        modals.openConfirmModal({
            title: 'Delete service',
            centered: true,
            children: <Text size="sm">Delete <b>{service.name}</b>? This only affects the current mock data.</Text>,
            labels: { confirm: 'Delete', cancel: 'Cancel' },
            confirmProps: { color: 'red' },
            onConfirm: () => {
                setServices((prev) => prev.filter((s) => s.id !== service.id));
                notifications.show({ color: 'red', message: `${service.name} deleted.` });
            },
        });
    };

    return (
        <Stack gap="lg">
            <Group justify="space-between" align="center">
                <Title order={1}>Services</Title>
                <Button leftSection={<IconPlus size={16} />} onClick={openCreateModal}>
                    Add New Service
                </Button>
            </Group>

            <Paper withBorder radius="lg" p="lg">
                <Stack gap="lg">
                    <Group justify="space-between" align="flex-end">
                        <Title order={2}>Services</Title>
                        <Group>
                            <TextInput
                                placeholder="Search"
                                leftSection={<IconSearch size={16} />}
                                value={searchValue}
                                onChange={(event) => setSearchValue(event.currentTarget.value)}
                                w={220}
                            />
                            <Select
                                clearable
                                placeholder="By Type"
                                data={serviceTypeOptions}
                                value={typeFilter}
                                onChange={setTypeFilter}
                                w={160}
                            />
                        </Group>
                    </Group>

                    <Divider />

                    <ScrollArea>
                        <Table highlightOnHover verticalSpacing="md" miw={900}>
                            <Table.Thead>
                                <Table.Tr>
                                    <Table.Th>Service Name</Table.Th>
                                    <Table.Th>Type</Table.Th>
                                    <Table.Th>Description</Table.Th>
                                    <Table.Th>Price ($)</Table.Th>
                                    <Table.Th>Duration</Table.Th>
                                    <Table.Th>Availability</Table.Th>
                                    <Table.Th>Action</Table.Th>
                                </Table.Tr>
                            </Table.Thead>
                            <Table.Tbody>
                                {filteredServices.length > 0 ? filteredServices.map((service) => {
                                    const TypeIcon = typeIconMap[service.type] || IconToolsKitchen2;

                                    return (
                                        <Table.Tr key={service.id}>
                                            <Table.Td fw={600}>{service.name}</Table.Td>
                                            <Table.Td>
                                                <Badge
                                                    color={typeColorMap[service.type]}
                                                    variant="light"
                                                    leftSection={<TypeIcon size={12} />}
                                                >
                                                    {service.type}
                                                </Badge>
                                            </Table.Td>
                                            <Table.Td maw={260}>
                                                <Text size="sm" lineClamp={2}>{service.description}</Text>
                                            </Table.Td>
                                            <Table.Td>${service.price}</Table.Td>
                                            <Table.Td>{service.duration}</Table.Td>
                                            <Table.Td>
                                                <Badge color={service.available ? 'green' : 'red'} variant="light">
                                                    {service.available ? 'Yes' : 'No'}
                                                </Badge>
                                            </Table.Td>
                                            <Table.Td>
                                                <Group gap="xs" wrap="nowrap">
                                                    <ActionIcon
                                                        variant="subtle"
                                                        color="blue"
                                                        onClick={() => openDetailsModal(service)}
                                                        aria-label={`View ${service.name}`}
                                                    >
                                                        <IconEye size={18} />
                                                    </ActionIcon>
                                                    <ActionIcon
                                                        variant="subtle"
                                                        color="dark"
                                                        onClick={() => openEditModal(service)}
                                                        aria-label={`Edit ${service.name}`}
                                                    >
                                                        <IconEdit size={18} />
                                                    </ActionIcon>
                                                    <ActionIcon
                                                        variant="subtle"
                                                        color="red"
                                                        onClick={() => handleDelete(service)}
                                                        aria-label={`Delete ${service.name}`}
                                                    >
                                                        <IconTrash size={18} />
                                                    </ActionIcon>
                                                </Group>
                                            </Table.Td>
                                        </Table.Tr>
                                    );
                                }) : (
                                    <Table.Tr>
                                        <Table.Td colSpan={7}>
                                            <Text ta="center" py="lg" c="dimmed">
                                                No services matched the current search.
                                            </Text>
                                        </Table.Td>
                                    </Table.Tr>
                                )}
                            </Table.Tbody>
                        </Table>
                    </ScrollArea>
                </Stack>
            </Paper>

            <ServiceFormModal
                key={selectedService?.id ?? 'new'}
                opened={formOpened}
                mode={modalMode}
                initialValues={selectedService || emptyService}
                onClose={() => setFormOpened(false)}
                onSubmit={handleSubmit}
            />

            <ServiceDetailsModal
                opened={detailsOpened}
                service={selectedService && selectedService.id ? selectedService : null}
                onClose={() => setDetailsOpened(false)}
            />
        </Stack>
    );
}
