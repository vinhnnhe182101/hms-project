import { useMemo, useState } from 'react';
import {
    Badge,
    Button,
    Card,
    Container,
    Grid,
    Group,
    Image,
    SegmentedControl,
    Select,
    SimpleGrid,
    Stack,
    Text,
    TextInput,
    Title
} from '@mantine/core';
import { IconCalendarPlus, IconSearch, IconUsers, IconRuler2 } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const rooms = [
    {
        id: 101,
        name: 'Deluxe Suite',
        type: 'Suite',
        price: 199,
        capacity: 2,
        size: '35 m2',
        image: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a',
        tags: ['Ocean View', 'King Bed', 'Breakfast']
    },
    {
        id: 202,
        name: 'Executive Room',
        type: 'Executive',
        price: 299,
        capacity: 3,
        size: '45 m2',
        image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b',
        tags: ['City View', 'Workspace', 'Lounge Access']
    },
    {
        id: 303,
        name: 'Presidential Suite',
        type: 'Suite',
        price: 499,
        capacity: 4,
        size: '68 m2',
        image: 'https://images.unsplash.com/photo-1590490360182-c33d5773345f',
        tags: ['Jacuzzi', 'Panoramic View', 'Private Dining']
    },
    {
        id: 115,
        name: 'Signature Queen',
        type: 'Standard',
        price: 149,
        capacity: 2,
        size: '28 m2',
        image: 'https://images.unsplash.com/photo-1631049035182-249067d7618e',
        tags: ['Queen Bed', 'Smart TV', 'Rain Shower']
    },
    {
        id: 407,
        name: 'Family Horizon',
        type: 'Family',
        price: 359,
        capacity: 5,
        size: '58 m2',
        image: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85',
        tags: ['2 Bedrooms', 'Kids Corner', 'Mini Kitchen']
    },
    {
        id: 221,
        name: 'Skyline Studio',
        type: 'Standard',
        price: 169,
        capacity: 2,
        size: '30 m2',
        image: 'https://images.unsplash.com/photo-1618773928121-c32242e63f39',
        tags: ['Floor-to-ceiling Window', 'Coffee Bar', 'Fast Wi-Fi']
    }
];

const roomTypeOptions = [
    { label: 'All', value: 'all' },
    { label: 'Standard', value: 'standard' },
    { label: 'Executive', value: 'executive' },
    { label: 'Suite', value: 'suite' },
    { label: 'Family', value: 'family' }
];

export default function RoomsPage() {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();

    const [searchQuery, setSearchQuery] = useState('');
    const [roomType, setRoomType] = useState('all');
    const [sortBy, setSortBy] = useState('price-asc');

    const filteredRooms = useMemo(() => {
        const normalizedQuery = searchQuery.trim().toLowerCase();

        let list = rooms.filter((room) => {
            const matchesType = roomType === 'all' || room.type.toLowerCase() === roomType;
            const matchesQuery =
                normalizedQuery.length === 0 ||
                room.name.toLowerCase().includes(normalizedQuery) ||
                room.tags.some((tag) => tag.toLowerCase().includes(normalizedQuery));

            return matchesType && matchesQuery;
        });

        list = [...list].sort((a, b) => {
            if (sortBy === 'price-asc') return a.price - b.price;
            if (sortBy === 'price-desc') return b.price - a.price;
            return b.capacity - a.capacity;
        });

        return list;
    }, [searchQuery, roomType, sortBy]);

    const handleBookRoom = (roomId) => {
        if (isAuthenticated) {
            navigate(`/booking/${roomId}`);
            return;
        }

        navigate('/auth/login', { state: { from: `/booking/${roomId}` } });
    };

    return (
        <Stack gap="xl">
            <div
                style={{
                    background:
                        'linear-gradient(120deg, rgba(16,24,40,0.96) 0%, rgba(22,101,52,0.88) 54%, rgba(250,204,21,0.75) 100%)',
                    borderRadius: '20px',
                    padding: '42px 36px',
                    color: 'white',
                    position: 'relative',
                    overflow: 'hidden'
                }}
            >
                <div
                    style={{
                        position: 'absolute',
                        top: '-80px',
                        right: '-70px',
                        width: '220px',
                        height: '220px',
                        borderRadius: '50%',
                        background: 'rgba(255, 255, 255, 0.18)'
                    }}
                />
                <Stack gap={8} style={{ position: 'relative' }}>
                    <Text fw={700} tt="uppercase" size="xs" style={{ letterSpacing: '0.08em' }}>
                        Discover your stay
                    </Text>
                    <Title order={1} size={42}>
                        Rooms & Suites
                    </Title>
                    <Text size="lg" maw={700} style={{ opacity: 0.92 }}>
                        Explore curated room types for every trip style, from compact business stays to high-end family suites.
                    </Text>
                </Stack>
            </div>

            <Card withBorder radius="lg" p="lg">
                <Grid align="end" gutter="md">
                    <Grid.Col span={{ base: 12, md: 5 }}>
                        <TextInput
                            label="Search"
                            placeholder="Room name or amenity"
                            value={searchQuery}
                            onChange={(event) => setSearchQuery(event.currentTarget.value)}
                            leftSection={<IconSearch size={16} />}
                        />
                    </Grid.Col>
                    <Grid.Col span={{ base: 12, md: 4 }}>
                        <Select
                            label="Sort"
                            value={sortBy}
                            onChange={(value) => setSortBy(value || 'price-asc')}
                            data={[
                                { label: 'Price: Low to High', value: 'price-asc' },
                                { label: 'Price: High to Low', value: 'price-desc' },
                                { label: 'Capacity', value: 'capacity-desc' }
                            ]}
                        />
                    </Grid.Col>
                    <Grid.Col span={{ base: 12, md: 3 }}>
                        <Text size="sm" c="dimmed" mb={8}>
                            Found {filteredRooms.length} room(s)
                        </Text>
                    </Grid.Col>
                    <Grid.Col span={12}>
                        <SegmentedControl
                            value={roomType}
                            onChange={setRoomType}
                            fullWidth
                            data={roomTypeOptions}
                        />
                    </Grid.Col>
                </Grid>
            </Card>

            {filteredRooms.length === 0 ? (
                <Card withBorder radius="md" p="xl">
                    <Text ta="center" c="dimmed">
                        No matching rooms found. Try changing search keyword or filter.
                    </Text>
                </Card>
            ) : (
                <SimpleGrid cols={{ base: 1, md: 2, lg: 3 }} spacing="md">
                    {filteredRooms.map((room) => (
                        <Card key={room.id} withBorder radius="md" p="lg" shadow="sm">
                            <Card.Section>
                                <Image src={room.image} alt={room.name} h={220} />
                            </Card.Section>

                            <Stack mt="md" gap="sm">
                                <Group justify="space-between" align="start">
                                    <div>
                                        <Title order={3} size="h4">
                                            {room.name}
                                        </Title>
                                        <Text size="sm" c="dimmed">
                                            Room #{room.id}
                                        </Text>
                                    </div>
                                    <Badge color="teal" variant="light">
                                        {room.type}
                                    </Badge>
                                </Group>

                                <Group gap="md">
                                    <Group gap={6}>
                                        <IconUsers size={16} />
                                        <Text size="sm">{room.capacity} guests</Text>
                                    </Group>
                                    <Group gap={6}>
                                        <IconRuler2 size={16} />
                                        <Text size="sm">{room.size}</Text>
                                    </Group>
                                </Group>

                                <Group gap={6}>
                                    {room.tags.map((tag) => (
                                        <Badge key={tag} variant="dot" color="gray">
                                            {tag}
                                        </Badge>
                                    ))}
                                </Group>

                                <Group justify="space-between" mt="xs">
                                    <Text fw={700} size="xl">
                                        ${room.price}
                                        <Text component="span" c="dimmed" size="sm" ml={4}>
                                            / night
                                        </Text>
                                    </Text>
                                    <Button
                                        leftSection={<IconCalendarPlus size={16} />}
                                        onClick={() => handleBookRoom(room.id)}
                                    >
                                        {isAuthenticated ? 'Book now' : 'Login to book'}
                                    </Button>
                                </Group>
                            </Stack>
                        </Card>
                    ))}
                </SimpleGrid>
            )}

            <Container size="sm" ta="center" pb="sm">
                <Text c="dimmed" size="sm">
                    Need help choosing? Contact our concierge team for personalized recommendations.
                </Text>
            </Container>
        </Stack>
    );
}