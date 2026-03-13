// src/pages/customer/BookingHistoryPage.jsx
import { useState, useEffect } from 'react';
import {
    Container,
    Title,
    Text,
    Paper,
    Stack,
    Group,
    Tabs,
    SimpleGrid,
    Badge,
    Button,
    Select,
    TextInput,
    Loader,
    Center,
    Pagination,
    Alert
} from '@mantine/core';
import {
    IconCalendarStats,
    IconSearch,
    IconFilter,
    IconAlertCircle,
    IconRefresh
} from '@tabler/icons-react';
import { useDisclosure } from '@mantine/hooks';
import { notifications } from '@mantine/notifications';
import { BookingHistoryCard } from './components/BookingHistoryCard';
import { BookingHistoryStats } from './components/BookingHistoryStats';
import { BookingHistoryFilter } from './components/BookingHistoryFilter';
import { BookingDetailModal } from './BookingDetailModal';
import { customerApi } from '../../apis/customerApi.js';

export default function BookingHistoryPage() {
    const [bookings, setBookings] = useState([]);
    const [filteredBookings, setFilteredBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedBooking, setSelectedBooking] = useState(null);
    const [modalOpened, { open, close }] = useDisclosure(false);
    const [activeTab, setActiveTab] = useState('all');
    const [searchQuery, setSearchQuery] = useState('');
    const [sortBy, setSortBy] = useState('newest');
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 6;

    // Fetch bookings
    useEffect(() => {
        fetchBookings();
    }, []);

    // Filter và sort bookings
    useEffect(() => {
        let filtered = [...bookings];

        // Filter by tab
        if (activeTab !== 'all') {
            filtered = filtered.filter(booking =>
                booking.status.toLowerCase() === activeTab.toLowerCase()
            );
        }

        // Filter by search
        if (searchQuery) {
            filtered = filtered.filter(booking =>
                booking.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
                booking.roomNumber?.toLowerCase().includes(searchQuery.toLowerCase())
            );
        }

        // Sort
        filtered.sort((a, b) => {
            switch(sortBy) {
                case 'newest':
                    return new Date(b.createdAt) - new Date(a.createdAt);
                case 'oldest':
                    return new Date(a.createdAt) - new Date(b.createdAt);
                case 'checkin':
                    return new Date(b.checkIn) - new Date(a.checkIn);
                case 'price':
                    return b.totalPrice - a.totalPrice;
                default:
                    return 0;
            }
        });

        setFilteredBookings(filtered);
        setCurrentPage(1);
    }, [bookings, activeTab, searchQuery, sortBy]);

    const fetchBookings = async () => {
        setLoading(true);
        try {
            const response = await customerApi.getBookingHistory();
            setBookings(response.data.data || []);
        } catch (error) {
            notifications.show({
                title: 'Error',
                message: 'Failed to load booking history',
                color: 'red'
            });
        } finally {
            setLoading(false);
        }
    };

    const handleViewDetails = (booking) => {
        setSelectedBooking(booking);
        open();
    };

    const handleRefresh = () => {
        fetchBookings();
        notifications.show({
            title: 'Success',
            message: 'Booking history refreshed',
            color: 'green'
        });
    };

    // Pagination
    const totalPages = Math.ceil(filteredBookings.length / itemsPerPage);
    const paginatedBookings = filteredBookings.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );

    if (loading) {
        return (
            <Center style={{ height: '60vh' }}>
                <Loader size="xl" />
            </Center>
        );
    }

    return (
        <Container size="xl" py="xl">
            <Stack gap="lg">
                {/* Header */}
                <Group justify="space-between" align="center">
                    <div>
                        <Title order={1}>Booking History</Title>
                        <Text c="dimmed" size="sm">
                            View and manage all your past and upcoming bookings
                        </Text>
                    </div>
                    <Button
                        leftSection={<IconRefresh size={16} />}
                        variant="light"
                        onClick={handleRefresh}
                    >
                        Refresh
                    </Button>
                </Group>

                {/* Stats Cards */}
                <BookingHistoryStats bookings={bookings} />

                {/* Filters */}
                <Paper withBorder p="md" radius="md">
                    <Stack>
                        <Group justify="space-between">
                            <Tabs value={activeTab} onChange={setActiveTab}>
                                <Tabs.List>
                                    <Tabs.Tab value="all">All Bookings</Tabs.Tab>
                                    <Tabs.Tab value="confirmed">Confirmed</Tabs.Tab>
                                    <Tabs.Tab value="completed">Completed</Tabs.Tab>
                                    <Tabs.Tab value="cancelled">Cancelled</Tabs.Tab>
                                </Tabs.List>
                            </Tabs>

                            <Select
                                value={sortBy}
                                onChange={setSortBy}
                                data={[
                                    { value: 'newest', label: 'Newest First' },
                                    { value: 'oldest', label: 'Oldest First' },
                                    { value: 'checkin', label: 'Check-in Date' },
                                    { value: 'price', label: 'Price' }
                                ]}
                                w={180}
                            />
                        </Group>

                        <Group>
                            <TextInput
                                placeholder="Search by booking code or room number..."
                                leftSection={<IconSearch size={16} />}
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                style={{ flex: 1 }}
                            />
                        </Group>
                    </Stack>
                </Paper>

                {/* Booking List */}
                {paginatedBookings.length === 0 ? (
                    <Paper withBorder p="xl" radius="md">
                        <Center>
                            <Stack align="center" gap="md">
                                <IconCalendarStats size={48} color="gray" />
                                <Title order={3}>No bookings found</Title>
                                <Text c="dimmed" ta="center">
                                    {searchQuery || activeTab !== 'all'
                                        ? 'Try adjusting your filters'
                                        : 'You haven\'t made any bookings yet'}
                                </Text>
                                {!searchQuery && activeTab === 'all' && (
                                    <Button
                                        variant="light"
                                        onClick={() => window.location.href = '/rooms'}
                                    >
                                        Browse Rooms
                                    </Button>
                                )}
                            </Stack>
                        </Center>
                    </Paper>
                ) : (
                    <>
                        <SimpleGrid cols={{ base: 1, md: 2, lg: 3 }} spacing="md">
                            {paginatedBookings.map((booking) => (
                                <BookingHistoryCard
                                    key={booking.id}
                                    booking={booking}
                                    onViewDetails={handleViewDetails}
                                />
                            ))}
                        </SimpleGrid>

                        {totalPages > 1 && (
                            <Group justify="center" mt="md">
                                <Pagination
                                    total={totalPages}
                                    value={currentPage}
                                    onChange={setCurrentPage}
                                    size="md"
                                    radius="md"
                                />
                            </Group>
                        )}
                    </>
                )}

                {/* Booking Detail Modal */}
                <BookingDetailModal
                    opened={modalOpened}
                    onClose={close}
                    booking={selectedBooking}
                />
            </Stack>
        </Container>
    );
}