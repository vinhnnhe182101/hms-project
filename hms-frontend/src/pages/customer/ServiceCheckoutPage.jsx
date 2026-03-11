import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
    Container, Title, Card, Text, Button, Group, Stack, Table,
    Select, Box, NumberInput, ActionIcon, Anchor, Loader, Center, Alert
} from '@mantine/core';
import { IconArrowLeft, IconCheck, IconTrash, IconPlus, IconInfoCircle } from '@tabler/icons-react';
import { useAuth } from '../../hooks/useAuth';
import { getActiveAllocations, createServiceBookings } from '../../apis/customer/serviceBookingApi';

export default function ServiceCheckoutPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const { user: customer } = useAuth();

    const cart = location.state?.cart || [];
    const [allocations, setAllocations] = useState(() => {
        const init = {};
        cart.forEach(item => {
            init[item.id] = [{ roomId: null, qty: item.quantity }];
        });
        return init;
    });

    const [activeRooms, setActiveRooms] = useState([]);
    const [loadingRooms, setLoadingRooms] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!customer?.customerId) {
            setLoadingRooms(false);
            return;
        }

        const fetchRooms = async () => {
            try {
                const data = await getActiveAllocations(customer.customerId);
                // Map API data => Select options
                const options = data.map(alloc => ({
                    value: String(alloc.allocationId),
                    label: `${alloc.roomNumber} - ${alloc.roomClassName}`
                }));
                setActiveRooms(options);
            } catch (error) {
                console.error("Lỗi khi tải phòng:", error);
            } finally {
                setLoadingRooms(false);
            }
        };

        fetchRooms();
    }, [customer]);

    if (cart.length === 0) {
        return (
            <Container py={60} ta="center">
                <Title order={2} mb="md">Giỏ hàng của bạn đang trống</Title>
                <Button onClick={() => navigate('/services')} color="teal">
                    Quay lại danh sách dịch vụ
                </Button>
            </Container>
        );
    }

    const getTotalPrice = () => cart.reduce((total, item) => total + (item.price * item.quantity), 0);

    const formatPrice = (price) =>
        new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price || 0);

    const handleUpdateAlloc = (serviceId, index, field, value) => {
        setAllocations(prev => {
            const newAllocs = [...prev[serviceId]];
            newAllocs[index] = { ...newAllocs[index], [field]: value };
            return { ...prev, [serviceId]: newAllocs };
        });
    };

    const handleRemoveAlloc = (serviceId, index) => {
        setAllocations(prev => {
            const newAllocs = [...prev[serviceId]];
            newAllocs.splice(index, 1);
            return { ...prev, [serviceId]: newAllocs };
        });
    };

    const handleAddAlloc = (serviceId) => {
        setAllocations(prev => {
            const currentAllocs = prev[serviceId];
            const currentTotal = currentAllocs.reduce((sum, a) => sum + (a.qty || 0), 0);
            const item = cart.find(i => i.id === serviceId);
            const remaining = item.quantity - currentTotal;

            return {
                ...prev,
                [serviceId]: [
                    ...currentAllocs,
                    { roomId: null, qty: remaining > 0 ? remaining : 1 }
                ]
            };
        });
    };

    const handleConfirmBooking = async () => {
        if (!customer?.customerId) {
            alert('Vui lòng đăng nhập trước khi đặt dịch vụ!');
            navigate('/login');
            return;
        }

        let isValid = true;
        const finalPayload = {
            customerId: customer.customerId,
            items: []
        };

        for (const item of cart) {
            const allocs = allocations[item.id] || [];

            // 1. Kiểm tra xem tổng chia có bằng tổng đặt không
            const totalQty = allocs.reduce((sum, a) => sum + (a.qty || 0), 0);
            if (totalQty !== item.quantity) {
                alert(`Dịch vụ "${item.name}" có tổng số lượng sử dụng (${totalQty}) chưa khớp với số lượng mua ban đầu (${item.quantity})!`);
                isValid = false;
                break;
            }

            // 2. Chắc chắn rằng mỗi dòng chia đều đã được chọn phòng
            for (const a of allocs) {
                if (!a.roomId) {
                    alert(`Vui lòng chọn phòng cụ thể cho dịch vụ "${item.name}"!`);
                    isValid = false;
                    break;
                }

                finalPayload.items.push({
                    serviceId: item.id,
                    allocationId: parseInt(a.roomId),
                    quantity: a.qty,
                    price: item.price
                });
            }
            if (!isValid) break;
        }

        if (!isValid) return;

        setIsSubmitting(true);
        try {
            await createServiceBookings(finalPayload);
            alert('✅ Đơn dịch vụ đã được ghi nhận vào hệ thống thành công!\nSẽ có nhân viên liên hệ xác nhận và phục vụ bạn sớm nhất.');
            navigate('/services');
        } catch (error) {
            alert(error.response?.data || 'Có lỗi xảy ra khi đặt dịch vụ. Vui lòng thử lại!');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <Box style={{ backgroundColor: '#f8f9fa', minHeight: '100vh', padding: '40px 0' }}>
            <Container size="md">
                <Anchor
                    onClick={() => navigate('/services')}
                    c="dimmed"
                    style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '24px' }}
                >
                    <IconArrowLeft size={16} /> Quay lại trang Dịch vụ
                </Anchor>

                <Card shadow="sm" padding="xl" radius="md" withBorder>
                    <Title order={2} mb="xl" style={{ color: '#2c3e50', borderBottom: '2px solid #e9ecef', paddingBottom: '16px' }}>
                        Chi Tiết Đặt Dịch Vụ
                    </Title>

                    {/* Loading Context hoặc Chưa Đăng nhập & Check-in */}
                    {loadingRooms ? (
                        <Center py={40}><Loader color="teal" /></Center>
                    ) : !customer ? (
                        <Alert icon={<IconInfoCircle size={16} />} color="blue" mb="lg">
                            Bạn cần đăng nhập và có phòng đang thuê để tiếp tục đặt dịch vụ.
                        </Alert>
                    ) : activeRooms.length === 0 ? (
                        <Alert icon={<IconInfoCircle size={16} />} color="orange" mb="lg">
                            Bạn hiện tại chưa nhận phòng hoặc đơn đặt phòng chưa được xác nhận, nên không thể đặt dịch vụ báo phòng lúc này.
                        </Alert>
                    ) : (
                        <Text c="dimmed" mb="md" size="sm">
                            Bạn vui lòng phân bổ dịch vụ và số lượng về các phòng đang sử dụng dưới đây:
                        </Text>
                    )}

                    <Table striped highlightOnHover withTableBorder>
                        <Table.Thead>
                            <Table.Tr style={{ backgroundColor: '#f1f3f5' }}>
                                <Table.Th>Dịch vụ</Table.Th>
                                <Table.Th style={{ textAlign: 'center' }}>Số lượng đã mua</Table.Th>
                                <Table.Th>Phân bổ phòng sử dụng</Table.Th>
                                <Table.Th style={{ textAlign: 'right' }}>Thành tiền</Table.Th>
                            </Table.Tr>
                        </Table.Thead>
                        <Table.Tbody>
                            {cart.map((item) => {
                                const allocs = allocations[item.id] || [];
                                const currentTotal = allocs.reduce((sum, a) => sum + (a.qty || 0), 0);
                                const isQtyMatched = currentTotal === item.quantity;

                                return (
                                    <Table.Tr key={item.id}>
                                        <Table.Td>
                                            <Text fw={600}>{item.name}</Text>
                                            <Text size="xs" c="dimmed">{formatPrice(item.price)} / lượt</Text>
                                        </Table.Td>

                                        <Table.Td style={{ textAlign: 'center', verticalAlign: 'top', paddingTop: '16px' }}>
                                            <Text fw={700} size="lg">{item.quantity}</Text>
                                            {!isQtyMatched && (
                                                <Text size="xs" c="red" mt={4} fw={500}>
                                                    Đang nhập: {currentTotal}
                                                </Text>
                                            )}
                                        </Table.Td>

                                        <Table.Td>
                                            <Stack gap="sm" mt="xs" mb="xs">
                                                {allocs.map((alloc, index) => (
                                                    <Group key={index} gap="xs" wrap="nowrap">
                                                        <Select
                                                            placeholder="Chọn phòng..."
                                                            data={activeRooms}
                                                            value={alloc.roomId}
                                                            onChange={(val) => handleUpdateAlloc(item.id, index, 'roomId', val)}
                                                            style={{ flex: 1, minWidth: 120 }}
                                                            disabled={activeRooms.length === 0}
                                                        />
                                                        <NumberInput
                                                            min={1}
                                                            max={item.quantity}
                                                            value={alloc.qty}
                                                            onChange={(val) => handleUpdateAlloc(item.id, index, 'qty', val || 1)}
                                                            style={{ width: 70 }}
                                                        />
                                                        <ActionIcon
                                                            color="red"
                                                            variant="subtle"
                                                            onClick={() => handleRemoveAlloc(item.id, index)}
                                                            disabled={allocs.length === 1} // Không cho xóa nếu chỉ có 1 dòng
                                                        >
                                                            <IconTrash size={16} />
                                                        </ActionIcon>
                                                    </Group>
                                                ))}

                                                {currentTotal < item.quantity && (
                                                    <Button
                                                        variant="light"
                                                        color="blue"
                                                        size="xs"
                                                        leftSection={<IconPlus size={14} />}
                                                        onClick={() => handleAddAlloc(item.id)}
                                                        style={{ alignSelf: 'flex-start' }}
                                                    >
                                                        Thêm phòng khác
                                                    </Button>
                                                )}
                                            </Stack>
                                        </Table.Td>

                                        <Table.Td style={{ textAlign: 'right', verticalAlign: 'top', paddingTop: '16px' }}>
                                            <Text fw={600} color="teal.6">
                                                {formatPrice(item.price * item.quantity)}
                                            </Text>
                                        </Table.Td>
                                    </Table.Tr>
                                );
                            })}
                        </Table.Tbody>
                    </Table>

                    <Group justify="space-between" mt="xl" pt="md" style={{ borderTop: '2px solid #e9ecef' }}>
                        <Title order={3}>Tổng thanh toán:</Title>
                        <Title order={2} color="teal.6">
                            {formatPrice(getTotalPrice())}
                        </Title>
                    </Group>

                    <Group justify="flex-end" mt="xl">
                        <Button
                            variant="default"
                            size="lg"
                            onClick={() => navigate('/services')}
                        >
                            Hủy
                        </Button>
                        <Button
                            size="lg"
                            color="teal"
                            leftSection={<IconCheck size={20} />}
                            onClick={handleConfirmBooking}
                            loading={isSubmitting}
                            disabled={loadingRooms || !customer || activeRooms.length === 0}
                        >
                            Xác Nhận Đặt
                        </Button>
                    </Group>
                </Card>
            </Container>
        </Box>
    );
}
