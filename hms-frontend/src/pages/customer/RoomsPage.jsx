import { useMemo, useState, useEffect } from 'react';
import {
    Container, Grid, Card, Image, Stack, Box, Text, Button,
    Select, Group, Badge, Title, LoadingOverlay,
    Pagination, Center, Loader, Rating, SegmentedControl, SimpleGrid
} from '@mantine/core';
import { DateTimePicker } from '@mantine/dates';
import dayjs from 'dayjs';
import { IconUsers, IconBed, IconChevronRight } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { getRoomClassList } from '../../apis/customer/roomClassApi';

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

    const [rooms, setRooms] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(9);
    const [totalPages, setTotalPages] = useState(0);
    const [totalItems, setTotalItems] = useState(0);
    const [checkIn, setCheckIn] = useState(dayjs().toDate());
    const [checkOut, setCheckOut] = useState(dayjs().add(1, 'day').toDate());

    const filteredRooms = useMemo(() => {
        const normalizedQuery = searchQuery.trim().toLowerCase();

        let list = rooms.filter((room) => {
            const matchesType = roomType === 'all' || room.type?.toLowerCase() === roomType;
            const matchesQuery =
                normalizedQuery.length === 0 ||
                room.name?.toLowerCase().includes(normalizedQuery) ||
                room.tags?.some((tag) => tag.toLowerCase().includes(normalizedQuery));

            return matchesType && matchesQuery;
        });

        list = [...list].sort((a, b) => {
            if (sortBy === 'price-asc') return (a.basePrice ?? a.price ?? 0) - (b.basePrice ?? b.price ?? 0);
            if (sortBy === 'price-desc') return (b.basePrice ?? b.price ?? 0) - (a.basePrice ?? a.price ?? 0);
            return (b.standardCapacity ?? b.capacity ?? 0) - (a.standardCapacity ?? a.capacity ?? 0);
        });

        return list;
    }, [searchQuery, roomType, sortBy, rooms]);

    const handleBookRoom = (roomId) => {
        if (isAuthenticated) {
            navigate(`/booking/${roomId}`);
        } else {
            navigate('/login');
        }
    };

    const fetchRooms = async (currentPage, size, inDate = null, outDate = null) => {
        setLoading(true);
        setError(null);
        try {
            const inIso = inDate ? dayjs(inDate).toISOString() : null;
            const outIso = outDate ? dayjs(outDate).toISOString() : null;
            const data = await getRoomClassList(currentPage, size, inIso, outIso);
            if (data && data.data) {
                setRooms(data.data);
                setTotalPages(data.totalPages);
                setTotalItems(data.totalItems);
                if (data.pageSize) {
                    const backendSize = Number(data.pageSize);
                    if (backendSize !== pageSize) {
                        setPageSize(backendSize);
                    }
                }
            } else {
                setRooms([]);
                setTotalPages(0);
                setTotalItems(0);
            }
        } catch (err) {
            console.error('Error fetching room classes:', err);
            setError(err.response?.data?.message || err.message || 'Unable to load room data. Please try again later.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRooms(page, pageSize);
    }, [page, pageSize]);

    // Reset to first page when pageSize changes
    const handlePageSizeChange = (value) => {
        if (value) {
            setPageSize(Number(value));
            setPage(0);
        }
    };

    // Format price to VND
    const formatPrice = (price) => {
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'VND' }).format(price);
    };

    return (
        <Box>
            {/* Page Header */}
            <Box style={{ backgroundColor: 'var(--mantine-color-blue-9)', color: 'white', padding: '50px 0' }}>
                <Container size="xl">
                    <Title order={1} mb={10} style={{ fontSize: '28px', fontWeight: 700, color: 'white' }}>
                        Room Types
                    </Title>
                    <Text style={{ fontSize: '16px', opacity: 0.85 }}>
                        Explore our luxury room types
                    </Text>
                </Container>
            </Box>

            {/* Main Content */}
            <Container size="xl" py={60}>
                <Card withBorder mb="xl" p="md">
                <Grid>
                    {/* Sidebar Filters */}
                    <Grid.Col span={{ base: 12, md: 3 }}>
                        <Box style={{ position: 'sticky', top: '20px' }}>
                            <Stack gap="xl">
                                {/* Search Date Filters */}
                                <Box>
                                    <Text fw={600} mb="md" style={{ fontSize: '16px' }}>Search Filters</Text>

                                    <DateTimePicker
                                        label="Check-in"
                                        placeholder="Select date and time"
                                        value={checkIn}
                                        onChange={(date) => {
                                            if (!date) return;
                                            
                                            const now = dayjs();
                                            let newCheckIn = date;
                                            
                                            // Don't allow selecting more than 5 minutes in the past
                                            if (dayjs(date).isBefore(now.subtract(5, 'minute'))) {
                                                newCheckIn = now.toDate();
                                            }
                                            
                                            setCheckIn(newCheckIn);
                                            
                                            // Ensure check-out is at least 1 hour after check-in
                                            const minCheckOut = dayjs(newCheckIn).add(1, 'hour');
                                            if (!checkOut || dayjs(checkOut).isBefore(minCheckOut)) {
                                                setCheckOut(minCheckOut.toDate());
                                            }
                                        }}
                                        minDate={new Date()}
                                        mb="sm"
                                        clearable={false}
                                        valueFormat="HH:mm DD/MM/YYYY"
                                    />

                                    <DateTimePicker
                                        label="Check-out"
                                        placeholder="Select date and time"
                                        value={checkOut}
                                        onChange={(date) => {
                                            if (!date) return;
                                            
                                            const minCheckOut = dayjs(checkIn).add(1, 'hour');
                                            if (dayjs(date).isBefore(minCheckOut)) {
                                                setCheckOut(minCheckOut.toDate());
                                            } else {
                                                setCheckOut(date);
                                            }
                                        }}
                                        minDate={checkIn ? dayjs(checkIn).add(1, 'minute').toDate() : new Date()}
                                        mb="md"
                                        clearable={false}
                                        valueFormat="HH:mm DD/MM/YYYY"
                                    />

                                    <Button
                                        fullWidth
                                        color="blue"
                                        mb="sm"
                                        onClick={() => {
                                            setPage(0);
                                            fetchRooms(0, pageSize, checkIn, checkOut);
                                        }}
                                    >
                                        Find available rooms
                                    </Button>

                                    <Button
                                        variant="light"
                                        color="gray"
                                        fullWidth
                                        onClick={() => {
                                            const defaultIn = dayjs().toDate();
                                            const defaultOut = dayjs().add(1, 'day').toDate();
                                            setCheckIn(defaultIn);
                                            setCheckOut(defaultOut);
                                            setPage(0);
                                            fetchRooms(0, pageSize, defaultIn, defaultOut);
                                        }}
                                    >
                                        Reset
                                    </Button>
                                </Box>
                            </Stack>
                        </Box>
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

                                            <Stack p="md" gap="xs" style={{ flex: 1 }}>
                                                {/* Room name & total rooms badge */}
                                                <Group justify="space-between" align="start">
                                                    <Title order={3} fw={600} style={{ fontSize: '16px', flex: 1 }}>
                                                        {room.name}
                                                    </Title>
                                                    <Badge variant="light" color={room.totalRooms > 0 ? "green" : "red"} radius="sm" style={{ flexShrink: 0 }}>
                                                        {room.totalRooms} rooms available
                                                    </Badge>
                                                </Group>

                                                {/* Average Rating */}
                                                <Group gap={6}>
                                                    <Rating
                                                        value={room.averageRating || 0}
                                                        fractions={2}
                                                        readOnly
                                                        size="xs"
                                                        color="yellow"
                                                    />
                                                    <Text size="xs" c="dimmed">
                                                        {room.averageRating
                                                            ? room.averageRating.toFixed(1)
                                                            : 'No reviews yet'}
                                                    </Text>
                                                </Group>

                                                {/* Capacity */}
                                                <Group gap="xs">
                                                    <IconUsers size={15} color="var(--mantine-color-blue-6)" />
                                                    <Text size="sm" c="dimmed">
                                                        {room.standardCapacity} standard guests
                                                    </Text>
                                                </Group>

                                                <Box mt="auto">
                                                    <Group justify="space-between" mb="xs" align="flex-end">
                                                        <Box>
                                                            <Text fw={700} color="blue.6" style={{ fontSize: '18px' }}>
                                                                {formatPrice(room.basePrice || 0)}
                                                            </Text>
                                                            <Text c="dimmed" style={{ fontSize: '12px' }}>/ night</Text>
                                                        </Box>
                                                    </Group>

                                                    <Stack gap="xs">
                                                        <Button
                                                            fullWidth
                                                            color="blue"
                                                            style={{
                                                                fontSize: '14px',
                                                                padding: '8px 16px',
                                                                fontWeight: 500
                                                            }}
                                                            rightSection={<IconChevronRight size={16} />}
                                                            onClick={(e) => {
                                                                e.stopPropagation();
                                                                navigate(`/rooms/${room.id}`);
                                                            }}
                                                        >
                                                            View Details
                                                        </Button>
                                                    </Stack>
                                                </Box>
                                            </Stack>
                                        </Card>
                                    ))}
                </SimpleGrid>
            )}

            {/* Pagination */}
            {totalPages > 1 && (
                <Box mt={40} style={{ display: 'flex', justifyContent: 'center' }}>
                    <Pagination
                        total={totalPages}
                        value={page + 1}
                        onChange={(p) => setPage(p - 1)}
                        color="blue"
                        size="lg"
                    />
                </Box>
            )}
            </Container>
        </Box>
    );
}
