import { useState, useEffect } from 'react';
import {
    Container, Grid, Card, Image, Stack, Box, Text, Button,
    Select, Group, Badge, Title, LoadingOverlay,
    Pagination, Center, Loader, Rating
} from '@mantine/core';
import { DateTimePicker } from '@mantine/dates';
import dayjs from 'dayjs';
import { IconUsers, IconBed, IconChevronRight } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';
import { getRoomClassList } from '../../apis/roomClassApi';

export default function RoomsPage() {
    const navigate = useNavigate();

    const [rooms, setRooms] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalItems, setTotalItems] = useState(0);
    const [pageSize, setPageSize] = useState(9);

    const [checkIn, setCheckIn] = useState(dayjs().toDate());
    const [checkOut, setCheckOut] = useState(dayjs().add(1, 'day').toDate());

    const fetchRooms = async (currentPage = 0, size = pageSize, inDate = checkIn, outDate = checkOut) => {
        setLoading(true);
        setError(null);
        try {
            const inIso = inDate ? dayjs(inDate).format('YYYY-MM-DDTHH:mm:ss') : null;
            const outIso = outDate ? dayjs(outDate).format('YYYY-MM-DDTHH:mm:ss') : null;
            const data = await getRoomClassList(currentPage, size, inIso, outIso);
            if (data && data.data) {
                setRooms(data.data);
                setTotalPages(data.totalPages);
                setTotalItems(data.totalItems);
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

    return (
        <Box>
            {/* Page Header */}
            <Box style={{ backgroundColor: 'var(--mantine-color-teal-9)', color: 'white', padding: '50px 0' }}>
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
                                {/* Search Date Filters */}
                                <Box>
                                    <Text fw={600} mb="md" style={{ fontSize: '16px' }}>Bộ lọc tìm kiếm</Text>

                                    <DateTimePicker
                                        label="Nhận phòng"
                                        placeholder="Chọn ngày giờ"
                                        value={checkIn}
                                        onChange={(date) => {
                                            setCheckIn(date);
                                            if (date && checkOut && date >= checkOut) {
                                                setCheckOut(dayjs(date).add(1, 'hour').toDate());
                                            }
                                        }}
                                        minDate={new Date()}
                                        mb="sm"
                                        clearable={false}
                                        valueFormat="HH:mm DD/MM/YYYY"
                                    />

                                    <DateTimePicker
                                        label="Trả phòng"
                                        placeholder="Chọn ngày giờ"
                                        value={checkOut}
                                        onChange={(date) => {
                                            if (date && checkIn && date <= checkIn) {
                                                // Handle invalid backwards selection
                                                setCheckOut(dayjs(checkIn).add(1, 'hour').toDate());
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
                                        color="teal"
                                        mb="sm"
                                        onClick={() => {
                                            setPage(0);
                                            fetchRooms(0, pageSize, checkIn, checkOut);
                                        }}
                                    >
                                        Tìm phòng trống
                                    </Button>

                                    <Button
                                        variant="light"
                                        color="gray"
                                        fullWidth
                                        onClick={() => {
                                            const defaultIn = dayjs().toDate();
                                            const defaultOut = dayjs().toDate();
                                            setCheckIn(defaultIn);
                                            setCheckOut(defaultOut);
                                            setPage(0);
                                            fetchRooms(0, pageSize, defaultIn, defaultOut);
                                        }}
                                    >
                                        Đặt lại
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

                            {!loading && !error && rooms.length === 0 && (
                                <Text ta="center" my="xl" size="lg" c="dimmed">
                                    Không tìm thấy loại phòng phù hợp.
                                </Text>
                            )}

                            <Grid>
                                {rooms.map((room) => (
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
                                                    fallbackSrc="https://placehold.co/300x220?text=No+Image"
                                                />
                                            </Card.Section>

                                            <Stack p="md" gap="xs" style={{ flex: 1 }}>
                                                {/* Room name & total rooms badge */}
                                                <Group justify="space-between" align="start">
                                                    <Title order={3} fw={600} style={{ fontSize: '16px', flex: 1 }}>
                                                        {room.name}
                                                    </Title>
                                                    <Badge variant="light" color={room.totalRooms > 0 ? "green" : "red"} radius="sm" style={{ flexShrink: 0 }}>
                                                        {room.totalRooms} phòng trống
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
                                                            : 'Chưa có đánh giá'}
                                                    </Text>
                                                </Group>

                                                {/* Capacity */}
                                                <Group gap="xs">
                                                    <IconUsers size={15} color="var(--mantine-color-teal-6)" />
                                                    <Text size="sm" c="dimmed">
                                                        {room.standardCapacity} khách tiêu chuẩn
                                                    </Text>
                                                </Group>

                                                <Box mt="auto">
                                                    <Group justify="space-between" mb="xs" align="flex-end">
                                                        <Box>
                                                            <Text fw={700} color="teal.6" style={{ fontSize: '18px' }}>
                                                                {formatPrice(room.basePrice || 0)}
                                                            </Text>
                                                            <Text c="dimmed" style={{ fontSize: '12px' }}>/ đêm</Text>
                                                        </Box>
                                                    </Group>

                                                    <Stack gap="xs">
                                                        <Button
                                                            fullWidth
                                                            color="teal"
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
                                    color="teal"
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
