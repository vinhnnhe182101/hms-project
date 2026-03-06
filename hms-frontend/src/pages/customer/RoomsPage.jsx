import { useState, useEffect } from 'react';
import {
    Container, Grid, Card, Image, Stack, Box, Text, Button,
    Select, Group, Badge, RangeSlider, Title, LoadingOverlay,
    Pagination, Center, Loader, Rating
} from '@mantine/core';
import { IconUsers, IconBed, IconChevronRight } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';
import { getRoomClassList } from '../../apis/roomClassApi';

export default function RoomsPage() {
    const navigate = useNavigate();

    // State for rooms data and loading status
    const [rooms, setRooms] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // State for pagination
    const [page, setPage] = useState(0); // 0-indexed for API
    const [totalPages, setTotalPages] = useState(0);
    const [totalItems, setTotalItems] = useState(0);
    const [pageSize, setPageSize] = useState(9);

    // State for filters (client-side filter on price range)
    const [priceRange, setPriceRange] = useState([0, 10]);
    const [debouncedPriceRange, setDebouncedPriceRange] = useState([0, 10]);

    // Fetch room classes from backend
    const fetchRooms = async (currentPage = 0, size = pageSize) => {
        setLoading(true);
        setError(null);
        try {
            const data = await getRoomClassList(currentPage, size);
            if (data && data.data) {
                setRooms(data.data);
                setTotalPages(data.totalPages);
                setTotalItems(data.totalItems);
                // Đồng bộ pageSize từ backend nếu backend trả về khác
                if (data.pageSize && data.pageSize !== pageSize) {
                    setPageSize(Number(data.pageSize));
                }
            } else {
                setRooms([]);
                setTotalPages(0);
                setTotalItems(0);
            }
        } catch (err) {
            console.error('Error fetching room classes:', err);
            setError(err.response?.data?.message || err.message || 'Không thể tải dữ liệu phòng. Vui lòng thử lại sau.');
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
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
    };

    // Client-side filter by price range (unit: triệu đồng)
    const filteredRooms = rooms.filter((room) => {
        const priceInMillion = (room.basePrice || 0) / 1_000_000;
        return priceInMillion >= debouncedPriceRange[0] && priceInMillion <= debouncedPriceRange[1];
    });

    return (
        <Box>
            {/* Page Header */}
            <Box style={{ backgroundColor: '#2c3e50', color: 'white', padding: '50px 0' }}>
                <Container size="xl">
                    <Title order={1} mb={10} style={{ fontSize: '28px', fontWeight: 700, color: 'white' }}>
                        Các Loại Phòng
                    </Title>
                    <Text style={{ fontSize: '16px', opacity: 0.85 }}>
                        Khám phá các loại phòng sang trọng của chúng tôi
                    </Text>
                </Container>
            </Box>

            {/* Main Content */}
            <Container size="xl" py={60}>
                <Grid>
                    {/* Sidebar Filters */}
                    <Grid.Col span={{ base: 12, md: 3 }}>
                        <Box style={{ position: 'sticky', top: '20px' }}>
                            <Stack gap="xl">
                                {/* Price Filter (client-side) */}
                                <Box>
                                    <Text fw={600} mb="md" style={{ fontSize: '16px' }}>Bộ lọc</Text>
                                    <Text fw={500} mb="sm" style={{ fontSize: '14px' }}>Mức giá (đêm)</Text>
                                    <RangeSlider
                                        value={priceRange}
                                        onChange={setPriceRange}
                                        onChangeEnd={setDebouncedPriceRange}
                                        min={0}
                                        max={10}
                                        step={0.5}
                                        minRange={0.5}
                                        label={(value) => `${value}tr`}
                                        marks={[
                                            { value: 0, label: '0' },
                                            { value: 5, label: '5tr' },
                                            { value: 10, label: '10tr' }
                                        ]}
                                        color="#D4A574"
                                        mb="md"
                                    />
                                    <Text size="sm" c="dimmed" mt={30} ta="center">
                                        {priceRange[0]}tr – {priceRange[1]}tr
                                    </Text>
                                </Box>

                                <Box>
                                    <Button
                                        variant="light"
                                        color="gray"
                                        fullWidth
                                        onClick={() => {
                                            setPriceRange([0, 10]);
                                            setDebouncedPriceRange([0, 10]);
                                            setPage(0);
                                        }}
                                    >
                                        Xóa bộ lọc
                                    </Button>
                                </Box>
                            </Stack>
                        </Box>
                    </Grid.Col>

                    {/* Room Grid */}
                    <Grid.Col span={{ base: 12, md: 9 }}>
                        <Box style={{ position: 'relative', minHeight: '200px' }}>
                            <LoadingOverlay visible={loading} zIndex={1000} overlayProps={{ radius: 'sm', blur: 2 }} />

                            <Group justify="space-between" mb="lg">
                                <Text size="md" fw={500}>
                                    Tìm thấy {totalItems} loại phòng
                                </Text>
                                <Select
                                    label="Số lượng:"
                                    value={pageSize.toString()}
                                    onChange={handlePageSizeChange}
                                    data={['3', '6', '9', '12']}
                                    style={{ width: '130px' }}
                                    leftSectionWidth={0}
                                    allowDeselect={false}
                                />
                            </Group>

                            {error && (
                                <Text c="red" ta="center" my="lg">{error}</Text>
                            )}

                            {!loading && !error && filteredRooms.length === 0 && (
                                <Text ta="center" my="xl" size="lg" c="dimmed">
                                    Không tìm thấy loại phòng phù hợp.
                                </Text>
                            )}

                            <Grid>
                                {filteredRooms.map((room) => (
                                    <Grid.Col key={room.id} span={{ base: 12, sm: 6, md: 4 }}>
                                        <Card
                                            shadow="sm"
                                            padding="0"
                                            radius="md"
                                            withBorder
                                            style={{
                                                height: '100%',
                                                display: 'flex',
                                                flexDirection: 'column',
                                                transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                                                cursor: 'pointer'
                                            }}
                                            onMouseEnter={(e) => {
                                                e.currentTarget.style.transform = 'translateY(-8px)';
                                                e.currentTarget.style.boxShadow = '0 12px 32px rgba(0,0,0,0.15)';
                                            }}
                                            onMouseLeave={(e) => {
                                                e.currentTarget.style.transform = 'translateY(0)';
                                                e.currentTarget.style.boxShadow = '';
                                            }}
                                            onClick={() => navigate(`/rooms/${room.id}`)}
                                        >
                                            {/* Room Image (Base64 from backend) */}
                                            <Card.Section>
                                                <Image
                                                    src={room.primaryImage?.dataUrl || 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=500'}
                                                    height={220}
                                                    alt={room.name}
                                                    style={{ objectFit: 'cover' }}
                                                    fallbackSrc="https://via.placeholder.com/300x220?text=No+Image"
                                                />
                                            </Card.Section>

                                            <Stack p="md" gap="xs" style={{ flex: 1 }}>
                                                {/* Room name & total rooms badge */}
                                                <Group justify="space-between" align="start">
                                                    <Title order={3} fw={600} style={{ fontSize: '16px', flex: 1 }}>
                                                        {room.name}
                                                    </Title>
                                                    <Badge variant="light" color="blue" radius="sm" style={{ flexShrink: 0 }}>
                                                        {room.totalRooms} phòng
                                                    </Badge>
                                                </Group>

                                                {/* Average Rating */}
                                                <Group gap={6}>
                                                    <Rating
                                                        value={room.averageRating || 0}
                                                        fractions={2}
                                                        readOnly
                                                        size="xs"
                                                        color="#FFB800"
                                                    />
                                                    <Text size="xs" c="dimmed">
                                                        {room.averageRating
                                                            ? room.averageRating.toFixed(1)
                                                            : 'Chưa có đánh giá'}
                                                    </Text>
                                                </Group>

                                                {/* Capacity */}
                                                <Group gap="xs">
                                                    <IconUsers size={15} color="#4CAF50" />
                                                    <Text size="sm" c="dimmed">
                                                        {room.standardCapacity} khách tiêu chuẩn
                                                    </Text>
                                                </Group>

                                                <Box mt="auto">
                                                    <Group justify="space-between" mb="xs" align="flex-end">
                                                        <Box>
                                                            <Text fw={700} c="#D4A574" style={{ fontSize: '18px' }}>
                                                                {formatPrice(room.basePrice || 0)}
                                                            </Text>
                                                            <Text c="dimmed" style={{ fontSize: '12px' }}>/ đêm</Text>
                                                        </Box>
                                                    </Group>

                                                    <Stack gap="xs">
                                                        <Button
                                                            fullWidth
                                                            style={{
                                                                backgroundColor: '#D4A574',
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
                                                            Xem chi tiết
                                                        </Button>
                                                    </Stack>
                                                </Box>
                                            </Stack>
                                        </Card>
                                    </Grid.Col>
                                ))}
                            </Grid>
                        </Box>

                        {/* Pagination */}
                        {totalPages > 1 && (
                            <Box mt={40} style={{ display: 'flex', justifyContent: 'center' }}>
                                <Pagination
                                    total={totalPages}
                                    value={page + 1}
                                    onChange={(p) => setPage(p - 1)}
                                    color="orange"
                                    size="lg"
                                />
                            </Box>
                        )}
                    </Grid.Col>
                </Grid>
            </Container>
        </Box>
    );
}
