import { Container, Title, Text, Button, Grid, Card, Image, Stack, Box, Group } from '@mantine/core';
import { IconToolsKitchen2, IconSwimming, IconSparkles } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';

export default function HomePage() {
    const navigate = useNavigate();

    const featuredRooms = [
        {
            id: 1,
            tag: 'PHỔ BIẾN NHẤT',
            name: 'Deluxe Ocean View',
            description: 'Phòng rộng 45m² với ban công nhìn ra biển, trang bị đầy đủ tiện nghi hiện đại.',
            price: '2.500.000đ',
            image: 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=500'
        },
        {
            id: 2,
            tag: 'GIA ĐÌNH',
            name: 'Family Suite',
            description: 'Phòng gần biển rộng rãi và gần biển với 2 phòng ngủ và phòng khách riêng biệt.',
            price: '4.200.000đ',
            image: 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=500'
        },
        {
            id: 3,
            tag: 'CAO CẤP',
            name: 'Royal Penthouse',
            description: 'Tầng penthouse xa xỉ tầng cao nhất với hồ bơi riêng và phục vụ 24/7.',
            price: '15.000.000đ',
            image: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=500'
        }
    ];

    const services = [
        {
            icon: IconToolsKitchen2,
            title: 'Nhà Hàng 5 Sao',
            description: 'Thưởng thức ẩm thực Á - Âu từ các đầu bếp hàng đầu.'
        },
        {
            icon: IconSparkles,
            title: 'Spa & Wellness',
            description: 'Thư giãn cơ thể và tâm trí với các liệu pháp chăm sóc chuyên nghiệp.'
        },
        {
            icon: IconSwimming,
            title: 'Hồ Bơi Vô Cực',
            description: 'Hồ bơi ngoài trời với tầm nhìn toàn cảnh thành phố và bờ biển.'
        }
    ];

    const testimonials = [
        {
            name: 'Nguyễn Văn A',
            role: 'Khách hàng thân thiết',
            comment: 'Kỳ nghỉ tuyệt vời nhất của gia đình tôi. Phòng đẹp, sạch sẽ, nhân viên cực kỳ thân thiện và chu đáo.'
        },
        {
            name: 'Trần Thị B',
            role: 'Du khách',
            comment: 'Nội thất sang trọng hàng đầu, bể bơi view đẹp. Chắc chắn tôi sẽ quay lại vào lần tới.'
        },
        {
            name: 'Lê Minh C',
            role: 'Khách doanh nghiệp',
            comment: 'Dịch vụ chuyên nghiệp, check-in nhanh chóng. Phòng Penthouse thực sự đẳng cấp.'
        }
    ];

    return (
        <Box>
            <Box
                style={{
                    background: 'linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5)), url(https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=1200) center/cover',
                    padding: '120px 0 180px',
                    color: 'white',
                    textAlign: 'center',
                    position: 'relative'
                }}
            >
                <Container size="lg">
                    <Title order={1} fw={700} mb="md" style={{ fontSize: '46px' }}>
                        Trải nghiệm kỳ nghỉ đẳng cấp 5 sao
                    </Title>
                    <Text mb={40} opacity={0.9} style={{ fontSize: '17px' }}>
                        Không gian sang trọng, dịch vụ hoàn hảo và những khoảnh khắc đáng nhớ dành cho bạn.
                    </Text>

                    {/* Booking CTA */}
                    <Button
                        size="xl"
                        style={{
                            backgroundColor: '#D4A574',
                            fontSize: '18px',
                            padding: '16px 56px',
                            fontWeight: 600,
                            borderRadius: '8px',
                            boxShadow: '0 4px 16px rgba(212,165,116,0.4)'
                        }}
                        onClick={() => navigate('/booking')}
                    >
                        Đặt phòng ngay →
                    </Button>
                </Container>
            </Box>

            {/* Featured Rooms Section */}
            <Container size="xl" py={80}>
                <Box ta="center" mb={50}>
                    <Title order={2} fw={700} mb="sm" style={{ fontSize: '32px' }}>
                        Phòng Nổi Bật
                    </Title>
                    <Text c="dimmed" style={{ fontSize: '15px' }}>
                        Được thiết kế tỉ mỉ với tầm nhìn tuyệt đẹp, mang đến sự thoải mái tuyệt đối cho quý khách.
                    </Text>
                </Box>

                <Grid>
                    {featuredRooms.map((room) => (
                        <Grid.Col key={room.id} span={{ base: 12, sm: 6, md: 4 }}>
                            <Card
                                shadow="sm"
                                padding="0"
                                radius="md"
                                withBorder
                                style={{
                                    height: '100%',
                                    transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                                    cursor: 'pointer',
                                    overflow: 'hidden'
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
                                <Card.Section>
                                    <Box style={{ position: 'relative' }}>
                                        <Image
                                            src={room.image}
                                            height={220}
                                            alt={room.name}
                                        />
                                        <Box
                                            style={{
                                                position: 'absolute',
                                                top: '16px',
                                                left: '16px',
                                                backgroundColor: '#D4A574',
                                                color: 'white',
                                                padding: '4px 12px',
                                                borderRadius: '4px',
                                                fontSize: '11px',
                                                fontWeight: 600
                                            }}
                                        >
                                            {room.tag}
                                        </Box>
                                    </Box>
                                </Card.Section>

                                <Stack p="lg" gap="xs">
                                    <Title order={3} fw={600} style={{ fontSize: '16px' }}>
                                        {room.name}
                                    </Title>
                                    <Text c="dimmed" style={{ minHeight: '50px', fontSize: '13px' }}>
                                        {room.description}
                                    </Text>
                                    <Group justify="space-between" mt="md">
                                        <Box>
                                            <Text fw={700} c="#D4A574" style={{ fontSize: '20px' }}>
                                                {room.price}
                                            </Text>
                                            <Text c="dimmed" style={{ fontSize: '14px' }}>/ đêm</Text>
                                        </Box>
                                        <Button variant="outline" color="gray" size="sm" style={{ fontSize: '15px' }}>
                                            Chi tiết
                                        </Button>
                                    </Group>
                                </Stack>
                            </Card>
                        </Grid.Col>
                    ))}
                </Grid>

                <Box ta="center" mt={40}>
                    <Button
                        size="lg"
                        variant="outline"
                        style={{
                            borderColor: '#D4A574',
                            color: '#D4A574',
                            fontSize: '16px',
                            padding: '12px 40px'
                        }}
                        onClick={() => navigate('/rooms')}
                    >
                        Xem thêm
                    </Button>
                </Box>
            </Container>

            {/* Services Section */}
            <Box style={{ backgroundColor: '#f8f9fa', padding: '80px 0' }}>
                <Container size="xl">
                    <Box ta="center" mb={50}>
                        <Title order={2} fw={700} mb="sm" style={{ fontSize: '32px' }}>
                            Dịch Vụ Tiện Ích
                        </Title>
                        <Text c="dimmed" style={{ fontSize: '15px' }}>
                            Tận hưởng những dịch vụ đẳng cấp dành riêng cho quý khách.
                        </Text>
                    </Box>

                    <Grid>
                        {services.map((service, index) => {
                            const Icon = service.icon;
                            return (
                                <Grid.Col key={index} span={{ base: 12, sm: 6, md: 4 }}>
                                    <Card
                                        shadow="sm"
                                        padding="xl"
                                        radius="md"
                                        style={{
                                            height: '100%',
                                            textAlign: 'center',
                                            transition: 'transform 0.3s ease',
                                            cursor: 'pointer'
                                        }}
                                        onMouseEnter={(e) => {
                                            e.currentTarget.style.transform = 'translateY(-4px)';
                                        }}
                                        onMouseLeave={(e) => {
                                            e.currentTarget.style.transform = 'translateY(0)';
                                        }}
                                    >
                                        <Box
                                            style={{
                                                width: '64px',
                                                height: '64px',
                                                borderRadius: '50%',
                                                backgroundColor: '#FFF5E6',
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                                margin: '0 auto 20px'
                                            }}
                                        >
                                            <Icon size={32} color="#D4A574" />
                                        </Box>
                                        <Title order={3} fw={600} mb="sm" style={{ fontSize: '20px' }}>
                                            {service.title}
                                        </Title>
                                        <Text c="dimmed" style={{ fontSize: '16px' }}>
                                            {service.description}
                                        </Text>
                                        <Button
                                            variant="subtle"
                                            color="gray"
                                            size="sm"
                                            mt="md"
                                            onClick={() => navigate('/services')}
                                            style={{ fontSize: '15px' }}
                                        >
                                            Xem thêm
                                        </Button>
                                    </Card>
                                </Grid.Col>
                            );
                        })}
                    </Grid>

                    <Box ta="center" mt={40}>
                        <Button
                            size="lg"
                            variant="outline"
                            style={{
                                borderColor: '#D4A574',
                                color: '#D4A574',
                                fontSize: '16px',
                                padding: '12px 40px'
                            }}
                            onClick={() => navigate('/services')}
                        >
                            Xem thêm
                        </Button>
                    </Box>
                </Container>
            </Box>

            {/* Testimonials Section */}
            <Container size="xl" py={60}>
                <Box ta="center" mb={40}>
                    <Title order={2} fw={700} mb="sm" style={{ fontSize: '32px' }}>
                        Khách Hàng Nói Gì
                    </Title>
                </Box>

                <Grid>
                    {testimonials.map((testimonial, index) => (
                        <Grid.Col key={index} span={{ base: 12, sm: 6, md: 4 }}>
                            <Card
                                shadow="sm"
                                padding="xl"
                                radius="md"
                                style={{ height: '100%' }}
                            >
                                <Text c="dimmed" mb="xl" style={{ fontStyle: 'italic', fontSize: '16px' }}>
                                    "{testimonial.comment}"
                                </Text>
                                <Group gap="md">
                                    <Box
                                        style={{
                                            width: '48px',
                                            height: '48px',
                                            borderRadius: '50%',
                                            backgroundColor: '#e9ecef',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center'
                                        }}
                                    >
                                        <Text fw={600} c="dimmed" style={{ fontSize: '18px' }}>
                                            {testimonial.name.charAt(0)}
                                        </Text>
                                    </Box>
                                    <Box>
                                        <Text fw={600} style={{ fontSize: '16px' }}>
                                            {testimonial.name}
                                        </Text>
                                        <Text c="dimmed" style={{ fontSize: '14px' }}>
                                            {testimonial.role}
                                        </Text>
                                    </Box>
                                </Group>
                            </Card>
                        </Grid.Col>
                    ))}
                </Grid>
            </Container>
        </Box>
    );
}
