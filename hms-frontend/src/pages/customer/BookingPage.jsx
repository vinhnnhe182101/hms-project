import { useState, useEffect } from 'react';
import {
    Box, Container, Grid, Text, Title, Table, Group, Badge,
    NumberInput, Button, Divider, Stack, Card, Loader, Center,
    Select
} from '@mantine/core';
import { DatePickerInput } from '@mantine/dates';
import { IconUsers, IconBuilding, IconCalendar, IconCoin } from '@tabler/icons-react';
import { getRoomClassList } from '../../apis/roomClassApi';
import '@mantine/dates/styles.css';

export default function BookingPage() {
    const [roomClasses, setRoomClasses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [quantities, setQuantities] = useState({});

    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);

    const [checkIn, setCheckIn] = useState(today);
    const [checkOut, setCheckOut] = useState(tomorrow);
    const [guests, setGuests] = useState(1);

    // Số đêm
    const nights = checkIn && checkOut
        ? Math.max(1, Math.round((new Date(checkOut) - new Date(checkIn)) / (1000 * 60 * 60 * 24)))
        : 1;

    useEffect(() => {
        const fetchRooms = async () => {
            try {
                setLoading(true);
                const data = await getRoomClassList(0, 50);
                const rooms = data?.data || [];
                setRoomClasses(rooms);
                // Khởi tạo quantities = 0 cho mỗi phòng
                const initQty = {};
                rooms.forEach(r => { initQty[r.id] = 0; });
                setQuantities(initQty);
            } catch (err) {
                console.error('Error fetching rooms:', err);
            } finally {
                setLoading(false);
            }
        };
        fetchRooms();
    }, []);

    const formatPrice = (price) =>
        new Intl.NumberFormat('vi-VN').format(price || 0);

    const handleQuantityChange = (id, value, totalRooms) => {
        const val = Math.min(Math.max(0, Number(value) || 0), totalRooms);
        setQuantities(prev => ({ ...prev, [id]: val }));
    };

    // Tổng tiền
    const totalPrice = roomClasses.reduce((sum, room) => {
        const qty = quantities[room.id] || 0;
        return sum + qty * (room.basePrice || 0) * nights;
    }, 0);

    const hasSelection = Object.values(quantities).some(q => q > 0);

    const handleContinue = () => {
        const selected = roomClasses
            .filter(r => (quantities[r.id] || 0) > 0)
            .map(r => ({
                id: r.id,
                name: r.name,
                quantity: quantities[r.id],
                pricePerNight: r.basePrice,
                total: quantities[r.id] * r.basePrice * nights,
            }));

        alert(`Đặt phòng:\n${JSON.stringify({ checkIn, checkOut, nights, guests, rooms: selected }, null, 2)}`);
    };

    return (
        <Box style={{ backgroundColor: '#f8f9fa', minHeight: '100vh' }}>
            {/* Header */}
            <Box style={{ backgroundColor: '#2c3e50', color: 'white', padding: '50px 0' }}>
                <Container size="xl">
                    <Title order={1} style={{ fontSize: '28px', fontWeight: 700, color: 'white' }} mb={8}>
                        Đặt Phòng
                    </Title>
                    <Text style={{ fontSize: '16px', opacity: 0.85 }}>
                        Chọn loại phòng phù hợp và điền thông tin đặt phòng
                    </Text>
                </Container>
            </Box>

            <Container size="xl" py={48}>
                <Grid gutter={32}>

                    {/* ── Left: Room table ── */}
                    <Grid.Col span={{ base: 12, md: 8 }}>
                        <Card shadow="sm" radius="md" withBorder padding={0} style={{ overflow: 'hidden' }}>
                            {/* Table header */}
                            <Box style={{ backgroundColor: '#2c3e50', padding: '14px 20px' }}>
                                <Group gap="sm">
                                    <IconBuilding size={18} color="#D4A574" />
                                    <Text fw={700} style={{ color: 'white', fontSize: '16px' }}>
                                        ĐẶT PHÒNG
                                    </Text>
                                </Group>
                            </Box>

                            {loading ? (
                                <Center py={80}><Loader color="#D4A574" size="lg" /></Center>
                            ) : (
                                <>
                                    <Table striped highlightOnHover withColumnBorders style={{ fontSize: '14px' }}>
                                        <Table.Thead>
                                            <Table.Tr style={{ backgroundColor: '#f1f3f5' }}>
                                                <Table.Th style={{ padding: '14px 16px', fontSize: '14px', fontWeight: 700 }}>
                                                    Loại phòng
                                                </Table.Th>
                                                <Table.Th style={{ padding: '14px 12px', fontSize: '14px', fontWeight: 700, textAlign: 'center' }}>
                                                    Giá phòng
                                                </Table.Th>
                                                <Table.Th style={{ padding: '14px 12px', fontSize: '14px', fontWeight: 700, textAlign: 'center' }}>
                                                    Phòng trống
                                                </Table.Th>
                                                <Table.Th style={{ padding: '14px 12px', fontSize: '14px', fontWeight: 700, textAlign: 'center' }}>
                                                    Số lượng
                                                </Table.Th>
                                            </Table.Tr>
                                        </Table.Thead>
                                        <Table.Tbody>
                                            {roomClasses.map((room) => {
                                                const qty = quantities[room.id] || 0;
                                                const available = room.totalRooms || 0;
                                                const options = Array.from({ length: available + 1 }, (_, i) => ({
                                                    value: String(i),
                                                    label: i === 0 ? '00' : String(i).padStart(2, '0'),
                                                }));

                                                return (
                                                    <Table.Tr
                                                        key={room.id}
                                                        style={{
                                                            backgroundColor: qty > 0 ? '#fff9f0' : undefined,
                                                            transition: 'background 0.2s'
                                                        }}
                                                    >
                                                        {/* Room name + capacity */}
                                                        <Table.Td style={{ padding: '16px 16px' }}>
                                                            <Text fw={600} style={{ fontSize: '15px', marginBottom: 4 }}>
                                                                {room.name}
                                                            </Text>
                                                            <Group gap={12}>
                                                                <Group gap={4}>
                                                                    <IconUsers size={14} color="#888" />
                                                                    <Text size="xs" c="dimmed">
                                                                        {room.standardCapacity} Người
                                                                    </Text>
                                                                </Group>
                                                            </Group>
                                                        </Table.Td>

                                                        {/* Price */}
                                                        <Table.Td style={{ padding: '16px 12px', textAlign: 'center' }}>
                                                            <Text fw={700} c="#D4A574" style={{ fontSize: '15px' }}>
                                                                {formatPrice(room.basePrice)}
                                                            </Text>
                                                            <Text size="xs" c="dimmed">VNĐ / đêm</Text>
                                                        </Table.Td>

                                                        {/* Available */}
                                                        <Table.Td style={{ padding: '16px 12px', textAlign: 'center' }}>
                                                            <Badge
                                                                size="lg"
                                                                variant="light"
                                                                color={available === 0 ? 'red' : available <= 2 ? 'orange' : 'green'}
                                                            >
                                                                {available}
                                                            </Badge>
                                                        </Table.Td>

                                                        {/* Quantity selector */}
                                                        <Table.Td style={{ padding: '16px 12px', textAlign: 'center' }}>
                                                            {available === 0 ? (
                                                                <Text size="sm" c="dimmed">Hết phòng</Text>
                                                            ) : (
                                                                <Select
                                                                    value={String(qty)}
                                                                    onChange={(val) => handleQuantityChange(room.id, val, available)}
                                                                    data={options}
                                                                    style={{ width: '80px', margin: '0 auto' }}
                                                                    allowDeselect={false}
                                                                    styles={{
                                                                        input: {
                                                                            textAlign: 'center',
                                                                            fontWeight: 600,
                                                                            borderColor: qty > 0 ? '#D4A574' : undefined,
                                                                        }
                                                                    }}
                                                                />
                                                            )}
                                                        </Table.Td>
                                                    </Table.Tr>
                                                );
                                            })}
                                        </Table.Tbody>
                                    </Table>

                                    {/* Total */}
                                    <Box
                                        style={{
                                            padding: '16px 20px',
                                            borderTop: '2px solid #e9ecef',
                                            backgroundColor: totalPrice > 0 ? '#fff9f0' : '#f8f9fa'
                                        }}
                                    >
                                        <Group justify="space-between" align="center">
                                            <Group gap={6}>
                                                <IconCoin size={18} color="#D4A574" />
                                                <Text fw={600} style={{ fontSize: '15px' }}>
                                                    Tổng thành tiền:
                                                </Text>
                                            </Group>
                                            <Text fw={800} c="#D4A574" style={{ fontSize: '20px' }}>
                                                {totalPrice > 0 ? `${formatPrice(totalPrice)} VNĐ` : '—'}
                                            </Text>
                                        </Group>
                                        {totalPrice > 0 && (
                                            <Text size="xs" c="dimmed" mt={4}>
                                                ({nights} đêm × các phòng đã chọn ×{' '}giá phòng)
                                            </Text>
                                        )}
                                    </Box>
                                </>
                            )}
                        </Card>
                    </Grid.Col>

                    {/* ── Right: Booking info ── */}
                    <Grid.Col span={{ base: 12, md: 4 }}>
                        <Card
                            shadow="md"
                            radius="md"
                            padding={0}
                            withBorder
                            style={{ position: 'sticky', top: '24px', overflow: 'hidden' }}
                        >
                            {/* Card header */}
                            <Box style={{ backgroundColor: '#2c3e50', padding: '14px 20px' }}>
                                <Text fw={700} style={{ color: 'white', fontSize: '16px', letterSpacing: '0.5px' }}>
                                    THÔNG TIN
                                </Text>
                            </Box>

                            <Stack gap="lg" p="xl">
                                {/* Check-in */}
                                <Box>
                                    <Text size="sm" fw={500} mb={6} c="dimmed">Ngày đến:</Text>
                                    <DatePickerInput
                                        value={checkIn}
                                        onChange={(date) => {
                                            setCheckIn(date);
                                            if (date && checkOut && new Date(date) >= new Date(checkOut)) {
                                                const next = new Date(date);
                                                next.setDate(next.getDate() + 1);
                                                setCheckOut(next);
                                            }
                                        }}
                                        minDate={today}
                                        valueFormat="DD/MM/YYYY"
                                        leftSection={<IconCalendar size={16} color="#D4A574" />}
                                        styles={{
                                            input: { borderColor: '#D4A574', fontWeight: 500 },
                                        }}
                                    />
                                </Box>

                                {/* Check-out */}
                                <Box>
                                    <Text size="sm" fw={500} mb={6} c="dimmed">Ngày đi:</Text>
                                    <DatePickerInput
                                        value={checkOut}
                                        onChange={(date) => {
                                            if (!date) return;
                                            if (checkIn && new Date(date) <= new Date(checkIn)) {
                                                const next = new Date(checkIn);
                                                next.setDate(next.getDate() + 1);
                                                setCheckOut(next);
                                            } else {
                                                setCheckOut(date);
                                            }
                                        }}
                                        minDate={checkIn ? new Date(new Date(checkIn).getTime() + 86400000) : tomorrow}
                                        valueFormat="DD/MM/YYYY"
                                        leftSection={<IconCalendar size={16} color="#D4A574" />}
                                        styles={{
                                            input: { borderColor: '#D4A574', fontWeight: 500 },
                                        }}
                                    />
                                </Box>

                                <Divider />

                                {/* Số người */}
                                <Box>
                                    <Text size="sm" fw={500} mb={6} c="dimmed">Số người:</Text>
                                    <NumberInput
                                        value={guests}
                                        onChange={setGuests}
                                        min={1}
                                        max={999}
                                        leftSection={<IconUsers size={16} color="#D4A574" />}
                                        styles={{ input: { borderColor: '#D4A574' } }}
                                        clampBehavior="strict"
                                    />
                                </Box>

                                <Divider />

                                {/* Summary box */}
                                {hasSelection && (
                                    <Box
                                        style={{
                                            backgroundColor: '#fff9f0',
                                            borderRadius: 8,
                                            padding: '12px 14px',
                                            border: '1px solid #D4A574'
                                        }}
                                    >
                                        <Text size="sm" fw={600} mb={8}>Phòng đã chọn:</Text>
                                        <Stack gap={4}>
                                            {roomClasses
                                                .filter(r => (quantities[r.id] || 0) > 0)
                                                .map(r => (
                                                    <Group key={r.id} justify="space-between">
                                                        <Text size="xs" c="dimmed">{r.name} × {quantities[r.id]}</Text>
                                                        <Text size="xs" fw={600} c="#D4A574">
                                                            {formatPrice(quantities[r.id] * r.basePrice * nights)} VNĐ
                                                        </Text>
                                                    </Group>
                                                ))}
                                        </Stack>
                                    </Box>
                                )}

                                <Button
                                    fullWidth
                                    size="lg"
                                    disabled={!hasSelection}
                                    onClick={handleContinue}
                                    style={{
                                        backgroundColor: hasSelection ? '#D4A574' : undefined,
                                        fontSize: '16px',
                                        fontWeight: 600,
                                        padding: '14px',
                                        transition: 'all 0.2s',
                                    }}
                                >
                                    Tiếp tục →
                                </Button>
                                {!hasSelection && (
                                    <Text size="xs" c="dimmed" ta="center">
                                        Vui lòng chọn ít nhất 1 phòng để tiếp tục
                                    </Text>
                                )}
                            </Stack>
                        </Card>
                    </Grid.Col>
                </Grid>
            </Container>
        </Box>
    );
}
