import { useState, useEffect } from 'react';
import {
    Container, Grid, Card, Box, Text, Title, Button, Group, Stack,
    Badge, Tabs, Modal, Table, Loader, Center, Image, Pagination
} from '@mantine/core';
import { IconPlus, IconMinus, IconTrash } from '@tabler/icons-react';
import { getAllServices, getServicesByCategory } from '../../apis/serviceApi';

/**
 * Mapping ServiceCategory (backend enum) → icon + tên tiếng Việt hiển thị
 * Backend chỉ có: SPA, MINIBAR
 */
const CATEGORY_CONFIG = {
    SPA: { icon: '💆', label: 'Spa & Massage', color: 'violet' },
    MINIBAR: { icon: '🍹', label: 'Minibar', color: 'blue' },
};

const ALL_CATEGORIES = [
    { id: 'all', label: 'Tất Cả', icon: '🌟' },
    { id: 'SPA', label: 'Spa & Massage', icon: '💆' },
    { id: 'MINIBAR', label: 'Minibar', icon: '🍹' },
];

export default function ServicesPage() {
    const [cart, setCart] = useState([]);
    const [cartOpened, setCartOpened] = useState(false);

    const [activeCategory, setActiveCategory] = useState('all');
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Pagination states
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const pageSize = 6;

    // Đặt lại trang về 1 mỗi khi đổi category
    useEffect(() => {
        setCurrentPage(1);
    }, [activeCategory]);

    // Fetch services khi category hoặc trang thay đổi
    useEffect(() => {
        const fetchServices = async () => {
            setLoading(true);
            setError(null);
            try {
                let data;
                if (activeCategory === 'all') {
                    data = await getAllServices(currentPage - 1, pageSize);
                } else {
                    data = await getServicesByCategory(activeCategory, currentPage - 1, pageSize);
                }

                // Trích xuất dữ liệu mảng. Tùy theo API, thường nó là `data.content` hoặc `data.data`.
                const content = data?.data || data?.content || [];
                setServices(content);
                setTotalPages(data?.totalPages || 1);
            } catch (err) {
                console.error('Error fetching services:', err);
                setError(err.response?.data?.message || 'Không thể tải dữ liệu dịch vụ. Vui lòng thử lại.');
                setServices([]);
                setTotalPages(1);
            } finally {
                setLoading(false);
            }
        };
        fetchServices();
    }, [activeCategory, currentPage]);

    // ── Cart helpers ──
    const addToCart = (service) => {
        const existing = cart.find((item) => item.id === service.id);
        if (existing) {
            setCart(cart.map((item) =>
                item.id === service.id ? { ...item, quantity: item.quantity + 1 } : item
            ));
        } else {
            setCart([...cart, { ...service, quantity: 1 }]);
        }
    };

    const updateQuantity = (id, quantity) => {
        if (quantity <= 0) {
            removeFromCart(id);
        } else {
            setCart(cart.map((item) => item.id === id ? { ...item, quantity } : item));
        }
    };

    const removeFromCart = (id) => setCart(cart.filter((item) => item.id !== id));

    const getTotalPrice = () => cart.reduce((total, item) => total + (item.price * item.quantity), 0);

    const formatPrice = (price) =>
        new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price || 0);

    const getCategoryInfo = (category) => CATEGORY_CONFIG[category] || { icon: '🔧', label: category, color: 'gray' };

    // ── ServiceCard component ──
    const ServiceCard = ({ service }) => {
        const catInfo = getCategoryInfo(service.serviceCategory);
        return (
            <Card
                shadow="sm"
                padding="lg"
                radius="md"
                withBorder
                style={{
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    transition: 'transform 0.3s ease, box-shadow 0.3s ease'
                }}
                onMouseEnter={(e) => {
                    e.currentTarget.style.transform = 'translateY(-4px)';
                    e.currentTarget.style.boxShadow = '0 8px 24px rgba(0,0,0,0.12)';
                }}
                onMouseLeave={(e) => {
                    e.currentTarget.style.transform = 'translateY(0)';
                    e.currentTarget.style.boxShadow = '';
                }}
            >
                <Card.Section>
                    <Image
                        src="https://images.unsplash.com/photo-1582719478250-c89afe4dc84b?w=400&h=250&fit=crop"
                        height={160}
                        alt={service.name}
                        fallbackSrc="https://via.placeholder.com/400x250?text=Service"
                    />
                </Card.Section>

                <Group justify="space-between" mt="md" mb="sm">
                    <Badge color={catInfo.color} variant="light" size="sm">
                        {catInfo.label}
                    </Badge>
                </Group>

                <Stack gap="xs" style={{ flex: 1 }}>
                    <Title order={4} style={{ fontSize: '16px', fontWeight: 600 }}>
                        {service.name}
                    </Title>
                    {service.description && (
                        <Text c="dimmed" style={{ fontSize: '14px', flex: 1 }}>
                            {service.description}
                        </Text>
                    )}

                    <Group justify="space-between" mt="auto" align="center">
                        <Text fw={700} c="#D4A574" style={{ fontSize: '16px' }}>
                            {formatPrice(service.price)}
                        </Text>
                        <Button
                            leftSection={<IconPlus size={16} />}
                            style={{ backgroundColor: '#D4A574', fontSize: '14px' }}
                            onClick={() => addToCart(service)}
                        >
                            Thêm
                        </Button>
                    </Group>
                </Stack>
            </Card >
        );
    };

    return (
        <Box>
            {/* Header */}
            <Box style={{ backgroundColor: '#2c3e50', color: 'white', padding: '60px 0' }}>
                <Container size="xl">
                    <Title order={1} mb="md" style={{ fontSize: '28px', fontWeight: 700 }}>
                        Dịch Vụ Phòng
                    </Title>
                    <Text style={{ fontSize: '16px', opacity: 0.9 }}>
                        Đặt dịch vụ spa, minibar và các tiện ích ngay tại phòng của bạn
                    </Text>
                </Container>
            </Box>

            <Box style={{ maxWidth: '1600px', margin: '0 auto', padding: '60px 40px' }}>
                <Grid>
                    {/* Main Content */}
                    <Grid.Col span={{ base: 12, md: 8 }}>
                        <Tabs
                            value={activeCategory}
                            onChange={setActiveCategory}
                            color="#D4A574"
                        >
                            <Tabs.List mb={30}>
                                {ALL_CATEGORIES.map((cat) => (
                                    <Tabs.Tab
                                        key={cat.id}
                                        value={cat.id}
                                        leftSection={<Text style={{ fontSize: '16px' }}>{cat.icon}</Text>}
                                        style={{ fontSize: '15px', fontWeight: 500 }}
                                    >
                                        {cat.label}
                                    </Tabs.Tab>
                                ))}
                            </Tabs.List>

                            {/* Panel content — dùng chung cho tất cả tab */}
                            {ALL_CATEGORIES.map((cat) => (
                                <Tabs.Panel key={cat.id} value={cat.id}>
                                    {loading ? (
                                        <Center py={80}>
                                            <Loader size="lg" color="#D4A574" />
                                        </Center>
                                    ) : error ? (
                                        <Box ta="center" py={60}>
                                            <Text c="red" size="lg" mb="md">{error}</Text>
                                            <Button
                                                variant="outline"
                                                style={{ borderColor: '#D4A574', color: '#D4A574' }}
                                                onClick={() => setActiveCategory(activeCategory)}
                                            >
                                                Thử lại
                                            </Button>
                                        </Box>
                                    ) : services.length === 0 ? (
                                        <Box ta="center" py={60}>
                                            <Text c="dimmed" size="lg">Chưa có dịch vụ nào trong danh mục này.</Text>
                                        </Box>
                                    ) : (
                                        <Box>
                                            <Grid>
                                                {services.map((service) => (
                                                    <Grid.Col key={service.id} span={{ base: 12, sm: 6, md: 4 }}>
                                                        <ServiceCard service={service} />
                                                    </Grid.Col>
                                                ))}
                                            </Grid>

                                            {totalPages > 1 && (
                                                <Group justify="center" mt={40}>
                                                    <Pagination
                                                        total={totalPages}
                                                        value={currentPage}
                                                        onChange={setCurrentPage}
                                                        color="orange"
                                                        size="lg"
                                                    />
                                                </Group>
                                            )}
                                        </Box>
                                    )}
                                </Tabs.Panel>
                            ))}
                        </Tabs>
                    </Grid.Col>

                    {/* Cart Sidebar */}
                    <Grid.Col span={{ base: 12, md: 4 }}>
                        <Card shadow="lg" padding="xl" radius="md" withBorder style={{ position: 'sticky', top: '20px' }}>
                            <Group justify="space-between" mb="md">
                                <Title order={3} style={{ fontSize: '18px', fontWeight: 600 }}>
                                    Giỏ Hàng
                                </Title>
                                <Badge
                                    size="xl"
                                    circle
                                    style={{ backgroundColor: '#D4A574', color: 'white', fontSize: '16px' }}
                                >
                                    {cart.reduce((total, item) => total + item.quantity, 0)}
                                </Badge>
                            </Group>

                            {cart.length === 0 ? (
                                <Box ta="center" py={50}>
                                    <Text c="dimmed" style={{ fontSize: '16px' }}>Giỏ hàng trống</Text>
                                    <Text c="dimmed" size="sm" mt="xs">Thêm dịch vụ để đặt hàng</Text>
                                </Box>
                            ) : (
                                <>
                                    <Stack gap="md" mb="md" style={{ maxHeight: '450px', overflowY: 'auto' }}>
                                        {cart.map((item) => (
                                            <Card key={item.id} padding="md" radius="md" withBorder>
                                                <Group justify="space-between" mb="sm">
                                                    <Text fw={600} style={{ fontSize: '15px', flex: 1 }}>
                                                        {item.name}
                                                    </Text>
                                                    <Button
                                                        size="xs"
                                                        variant="subtle"
                                                        color="red"
                                                        onClick={() => removeFromCart(item.id)}
                                                        p={4}
                                                    >
                                                        <IconTrash size={16} />
                                                    </Button>
                                                </Group>
                                                <Group justify="space-between">
                                                    <Group gap={6}>
                                                        <Button
                                                            size="sm"
                                                            variant="outline"
                                                            onClick={() => updateQuantity(item.id, item.quantity - 1)}
                                                            p={6}
                                                            style={{ borderColor: '#D4A574', color: '#D4A574' }}
                                                        >
                                                            <IconMinus size={14} />
                                                        </Button>
                                                        <Text fw={600} style={{ fontSize: '16px', minWidth: '32px', textAlign: 'center' }}>
                                                            {item.quantity}
                                                        </Text>
                                                        <Button
                                                            size="sm"
                                                            variant="outline"
                                                            onClick={() => updateQuantity(item.id, item.quantity + 1)}
                                                            p={6}
                                                            style={{ borderColor: '#D4A574', color: '#D4A574' }}
                                                        >
                                                            <IconPlus size={14} />
                                                        </Button>
                                                    </Group>
                                                    <Text fw={600} c="#D4A574" style={{ fontSize: '16px' }}>
                                                        {formatPrice(item.price * item.quantity)}
                                                    </Text>
                                                </Group>
                                            </Card>
                                        ))}
                                    </Stack>

                                    <Box pt="lg" style={{ borderTop: '2px solid #e9ecef' }}>
                                        <Group justify="space-between" mb="lg">
                                            <Text fw={700} style={{ fontSize: '16px' }}>Tổng cộng</Text>
                                            <Text fw={700} c="#D4A574" style={{ fontSize: '18px' }}>
                                                {formatPrice(getTotalPrice())}
                                            </Text>
                                        </Group>
                                        <Button
                                            fullWidth
                                            size="lg"
                                            style={{
                                                backgroundColor: '#D4A574',
                                                fontSize: '15px',
                                                fontWeight: 600,
                                                padding: '14px'
                                            }}
                                            onClick={() => setCartOpened(true)}
                                        >
                                            Đặt Hàng
                                        </Button>
                                    </Box>
                                </>
                            )}
                        </Card>
                    </Grid.Col>
                </Grid>
            </Box>

            {/* Order Confirmation Modal */}
            <Modal
                opened={cartOpened}
                onClose={() => setCartOpened(false)}
                title={
                    <Title order={2} style={{ fontSize: '28px', fontWeight: 700, color: '#2c3e50' }}>
                        Xác Nhận Đơn Hàng
                    </Title>
                }
                size="xl"
                centered
                padding="xl"
            >
                <Stack gap="xl">
                    <Table
                        striped
                        highlightOnHover
                        withTableBorder
                        withColumnBorders
                        style={{ fontSize: '16px' }}
                    >
                        <Table.Thead>
                            <Table.Tr style={{ backgroundColor: '#f8f9fa' }}>
                                <Table.Th style={{ fontSize: '18px', fontWeight: 600, padding: '16px' }}>Dịch vụ</Table.Th>
                                <Table.Th style={{ textAlign: 'center', fontSize: '18px', fontWeight: 600, padding: '16px' }}>SL</Table.Th>
                                <Table.Th style={{ textAlign: 'right', fontSize: '18px', fontWeight: 600, padding: '16px' }}>Thành tiền</Table.Th>
                            </Table.Tr>
                        </Table.Thead>
                        <Table.Tbody>
                            {cart.map((item) => (
                                <Table.Tr key={item.id}>
                                    <Table.Td style={{ fontSize: '16px', padding: '16px' }}>{item.name}</Table.Td>
                                    <Table.Td style={{ textAlign: 'center', fontSize: '16px', padding: '16px' }}>{item.quantity}</Table.Td>
                                    <Table.Td style={{ textAlign: 'right', fontSize: '16px', padding: '16px' }}>
                                        {formatPrice(item.price * item.quantity)}
                                    </Table.Td>
                                </Table.Tr>
                            ))}
                        </Table.Tbody>
                        <Table.Tfoot>
                            <Table.Tr style={{ backgroundColor: '#fff9f0' }}>
                                <Table.Td colSpan={2} style={{ fontSize: '20px', fontWeight: 700, padding: '20px' }}>
                                    Tổng cộng
                                </Table.Td>
                                <Table.Td style={{ textAlign: 'right', fontSize: '24px', fontWeight: 700, color: '#D4A574', padding: '20px' }}>
                                    {formatPrice(getTotalPrice())}
                                </Table.Td>
                            </Table.Tr>
                        </Table.Tfoot>
                    </Table>

                    <Box p="xl" style={{ backgroundColor: '#f8f9fa', borderRadius: '12px', textAlign: 'center' }}>
                        <Text size="lg" c="dimmed" mb="xs">📍 Giao hàng đến phòng của bạn</Text>
                        <Text size="md" c="dimmed">⏱️ Thời gian dự kiến: 15–30 phút</Text>
                    </Box>

                    <Group justify="center" gap="lg" mt="md">
                        <Button
                            variant="outline"
                            size="xl"
                            onClick={() => setCartOpened(false)}
                            style={{ borderColor: '#D4A574', color: '#D4A574', fontSize: '18px', padding: '16px 48px', fontWeight: 600 }}
                        >
                            Hủy
                        </Button>
                        <Button
                            size="xl"
                            style={{ backgroundColor: '#D4A574', fontSize: '18px', padding: '16px 48px', fontWeight: 600 }}
                            onClick={() => {
                                alert('✅ Đơn hàng đã được gửi thành công!\n\n🚀 Chúng tôi sẽ giao đến phòng của bạn trong 15–30 phút.\n\n📞 Hotline: 1900-xxxx');
                                setCart([]);
                                setCartOpened(false);
                            }}
                        >
                            ✓ Xác Nhận Đặt Hàng
                        </Button>
                    </Group>
                </Stack>
            </Modal>
        </Box>
    );
}
