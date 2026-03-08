import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
    Box, Container, Grid, Card, Title, Text, TextInput, Button,
    Stack, Group, Divider, Textarea, Badge, Alert
} from '@mantine/core';
import { IconUser, IconPhone, IconIdBadge, IconCalendar, IconUsers, IconCoin, IconArrowLeft, IconInfoCircle, IconLogin } from '@tabler/icons-react';
import dayjs from 'dayjs';
import { createBooking } from '../../apis/reservationApi';
import { useAuth } from '../../context/AuthContext';

export default function CheckoutPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const { customer } = useAuth();

    const bookingData = location.state;

    const [name, setName] = useState('');
    const [phone, setPhone] = useState('');
    const [identityCard, setIdentityCard] = useState('');
    const [note, setNote] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Tự động điền thông tin từ customer đã đăng nhập
    useEffect(() => {
        if (customer) {
            if (customer.fullName) setName(customer.fullName);
            if (customer.phoneNumber) setPhone(customer.phoneNumber);
            if (customer.identityCard) setIdentityCard(customer.identityCard);
        }
    }, [customer]);

    if (!bookingData) {
        return (
            <Container py={60} ta="center">
                <Title order={2} c="red" mb="md">Không tìm thấy thông tin đặt phòng</Title>
                <Button onClick={() => navigate('/booking')}>Quay lại trang Đặt Phòng</Button>
            </Container>
        );
    }

    const { checkIn, checkOut, nights, guests, rooms } = bookingData;


    const totalPrice = rooms.reduce((sum, r) => sum + r.total, 0);

    const formatPrice = (price) =>
        new Intl.NumberFormat('vi-VN').format(price || 0);

    const handleConfirmBooking = async () => {
        if (!name.trim() || !phone.trim() || !identityCard.trim()) {
            alert('Vui lòng điền đầy đủ thông tin khách hàng!');
            return;
        }

        setIsSubmitting(true);
        try {
            const bookingPayload = {
                checkIn: dayjs(checkIn).toISOString(),
                checkOut: dayjs(checkOut).toISOString(),
                nights,
                guests,
                rooms: rooms.map(r => ({
                    id: r.id,
                    name: r.name,
                    quantity: r.quantity,
                    pricePerNight: r.pricePerNight,
                    total: r.total
                })),
                customer: {
                    customerId: customer?.customerId ?? null,
                    name,
                    phone,
                    identityCard,
                    note
                }
            };

            await createBooking(bookingPayload);
            alert('Đặt phòng thành công! Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.');
            navigate('/');
        } catch (error) {
            console.error('Lỗi khi đặt phòng:', error);
            const errorMessage = error.response?.data?.error || 'Có lỗi xảy ra khi đặt phòng. Vui lòng thử lại.';
            alert(errorMessage);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <Box style={{ backgroundColor: '#f8f9fa', minHeight: '100vh' }}>
            <Box style={{ backgroundColor: '#2c3e50', color: 'white', padding: '50px 0' }}>
                <Container size="xl">
                    <Title order={1} style={{ fontSize: '28px', fontWeight: 700, color: 'white' }} mb={8}>
                        Xác Nhận Thông Tin
                    </Title>
                    <Text style={{ fontSize: '16px', opacity: 0.85 }}>
                        Vui lòng điền thông tin người đặt và kiểm tra lại phòng đã chọn
                    </Text>
                </Container>
            </Box>

            <Container size="xl" py={48}>
                <Grid gutter={32}>
                    {/* Left: Customer info form */}
                    <Grid.Col span={{ base: 12, md: 7 }}>
                        <Button
                            variant="subtle"
                            color="gray"
                            leftSection={<IconArrowLeft size={16} />}
                            onClick={() => navigate(-1)}
                            mb="sm"
                            px={0}
                            styles={{ root: { '&:hover': { backgroundColor: 'transparent' } } }}
                        >
                            Quay lại
                        </Button>
                        <Card shadow="sm" radius="md" withBorder padding="xl">
                            <Group justify="space-between" align="center" mb="lg">
                                <Title order={3} style={{ color: '#2c3e50' }}>Thông Tin Khách Hàng</Title>
                                {customer ? (
                                    <Badge color="green" variant="light" leftSection={<IconInfoCircle size={12} />}>
                                        Đã tự động điền
                                    </Badge>
                                ) : (
                                    <Badge
                                        color="orange"
                                        variant="light"
                                        style={{ cursor: 'pointer' }}
                                        leftSection={<IconLogin size={12} />}
                                        onClick={() => navigate('/login')}
                                    >
                                        Đăng nhập để tự động điền
                                    </Badge>
                                )}
                            </Group>

                            <Stack gap="md">
                                <TextInput
                                    label="Họ và tên"
                                    placeholder="Nhập họ và tên người đặt"
                                    value={name}
                                    onChange={(e) => setName(e.currentTarget.value)}
                                    leftSection={<IconUser size={16} color="#D4A574" />}
                                    withAsterisk
                                    description={customer ? 'Thay đổi sẽ được cập nhật vào hồ sơ tài khoản' : undefined}
                                />
                                <TextInput
                                    label="Số điện thoại"
                                    placeholder="Nhập số điện thoại liên hệ"
                                    value={phone}
                                    onChange={(e) => setPhone(e.currentTarget.value)}
                                    leftSection={<IconPhone size={16} color="#D4A574" />}
                                    withAsterisk
                                    description={customer ? 'Thay đổi sẽ được cập nhật vào hồ sơ tài khoản' : undefined}
                                />
                                <TextInput
                                    label="CMND / CCCD"
                                    placeholder="Nhập số Chứng minh nhân dân hoặc Căn cước công dân"
                                    value={identityCard}
                                    onChange={(e) => setIdentityCard(e.currentTarget.value)}
                                    leftSection={<IconIdBadge size={16} color="#D4A574" />}
                                    withAsterisk
                                    description={customer ? 'Thay đổi sẽ được cập nhật vào hồ sơ tài khoản' : undefined}
                                />
                                <Textarea
                                    label="Ghi chú"
                                    placeholder="Ghi chú thêm (không bắt buộc)"
                                    value={note}
                                    onChange={(e) => setNote(e.currentTarget.value)}
                                    minRows={3}
                                />
                            </Stack>
                        </Card>
                    </Grid.Col>

                    {/* Right: Booking Summary */}
                    <Grid.Col span={{ base: 12, md: 5 }}>
                        <Card shadow="md" radius="md" padding="xl" withBorder style={{ position: 'sticky', top: '24px' }}>
                            <Title order={3} mb="lg" style={{ color: '#2c3e50' }}>Tóm Tắt Đặt Phòng</Title>

                            <Stack gap="sm">
                                <Group align="flex-start" wrap="nowrap">
                                    <IconCalendar size={20} color="#D4A574" style={{ marginTop: 2 }} />
                                    <Box>
                                        <Text size="sm" fw={600}>Nhận phòng</Text>
                                        <Text size="sm" c="dimmed">
                                            {dayjs(checkIn).format('HH:mm - DD/MM/YYYY')}
                                        </Text>
                                    </Box>
                                </Group>

                                <Group align="flex-start" wrap="nowrap">
                                    <IconCalendar size={20} color="#D4A574" style={{ marginTop: 2 }} />
                                    <Box>
                                        <Text size="sm" fw={600}>Trả phòng</Text>
                                        <Text size="sm" c="dimmed">
                                            {dayjs(checkOut).format('HH:mm - DD/MM/YYYY')}
                                        </Text>
                                    </Box>
                                </Group>

                                <Group align="flex-start" wrap="nowrap">
                                    <IconUsers size={20} color="#D4A574" style={{ marginTop: 2 }} />
                                    <Box>
                                        <Text size="sm" fw={600}>Số người</Text>
                                        <Text size="sm" c="dimmed">{guests} người</Text>
                                    </Box>
                                </Group>

                                <Divider my="sm" />

                                <Text fw={600} mb={4}>Phòng đã chọn:</Text>
                                {rooms.map((room, idx) => (
                                    <Group key={idx} justify="space-between" align="center" wrap="nowrap">
                                        <Box style={{ flex: 1 }}>
                                            <Text size="sm" fw={500}>{room.name}</Text>
                                            <Text size="xs" c="dimmed">{room.quantity} phòng × {nights} đêm</Text>
                                        </Box>
                                        <Text size="sm" fw={600} c="#D4A574">
                                            {formatPrice(room.total)} VNĐ
                                        </Text>
                                    </Group>
                                ))}

                                <Divider my="sm" />

                                <Group justify="space-between" align="center">
                                    <Group gap={6}>
                                        <IconCoin size={20} color="#D4A574" />
                                        <Text size="md" fw={700}>Tổng thanh toán:</Text>
                                    </Group>
                                    <Text size="xl" fw={800} c="#D4A574">
                                        {formatPrice(totalPrice)} VNĐ
                                    </Text>
                                </Group>

                                <Button
                                    fullWidth
                                    size="lg"
                                    mt="xl"
                                    onClick={handleConfirmBooking}
                                    disabled={!name.trim() || !phone.trim() || !identityCard.trim()}
                                    loading={isSubmitting}
                                    style={{
                                        backgroundColor: (!name.trim() || !phone.trim() || !identityCard.trim()) ? undefined : '#D4A574',
                                        fontSize: '16px',
                                        fontWeight: 600,
                                        padding: '14px',
                                    }}
                                >
                                    Xác nhận đặt phòng
                                </Button>
                            </Stack>
                        </Card>
                    </Grid.Col>
                </Grid>
            </Container>
        </Box>
    );
}
