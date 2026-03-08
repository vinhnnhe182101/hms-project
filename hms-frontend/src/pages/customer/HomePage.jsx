import { useState, useEffect } from 'react';
import { Container, Title, Text, Button, Grid, Card, Image, Stack, Box, Group } from '@mantine/core';
import { Carousel } from '@mantine/carousel';
import { IconToolsKitchen2, IconSwimming, IconSparkles } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';
import '@mantine/carousel/styles.css';
import { getRoomClassList } from '../../apis/roomClassApi';
import { getAllServices } from '../../apis/serviceApi';
import { getLatestRatings } from '../../apis/ratingApi';

export default function HomePage() {
    const navigate = useNavigate();

    const [featuredRooms, setFeaturedRooms] = useState([]);
    const [services, setServices] = useState([]);
    const [testimonials, setTestimonials] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Lấy 5 phòng nổi bật
                const roomsResponse = await getRoomClassList(0, 5);
                setFeaturedRooms(roomsResponse?.data || []);

                // Lấy 5 dịch vụ
                const servicesResponse = await getAllServices(0, 5);
                const servicesData = servicesResponse?.data || servicesResponse?.content || [];
                setServices(servicesData);

                // Lấy 3 đánh giá mới nhất
                const ratingsResponse = await getLatestRatings(0, 3);
                setTestimonials(ratingsResponse?.content || []);
            } catch (error) {
                console.error('Lỗi khi tải dữ liệu trang chủ:', error);
            }
        };
        fetchData();
    }, []);

    const formatPrice = (price) =>
        new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price || 0);

    const getServiceIcon = (category) => {
        if (category === 'SPA') return IconSparkles;
        if (category === 'MINIBAR') return IconToolsKitchen2;
        return IconSwimming;
    };

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

                <Carousel
                    withIndicators
                    slideSize={{ base: '100%', sm: '50%', md: '33.333333%' }}
                    slideGap="md"
                    loop
                    align="start"
                    slidesToScroll={1}
                >
                    {featuredRooms.map((room) => (
                        <Carousel.Slide key={room.id}>
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
                                            src={room.primaryImage?.dataUrl || 'https://placehold.co/400x250?text=No+Image'}
                                            height={220}
                                            alt={room.name}
                                        />
                                    </Box>
                                </Card.Section>

                                <Stack p="lg" gap="xs">
                                    <Title order={3} fw={600} style={{ fontSize: '16px' }}>
                                        {room.name}
                                    </Title>
                                    <Group justify="space-between" mt="md">
                                        <Box>
                                            <Text fw={700} c="#D4A574" style={{ fontSize: '20px' }}>
                                                {formatPrice(room.basePrice)}
                                            </Text>
                                            <Text c="dimmed" style={{ fontSize: '14px' }}>/ đêm</Text>
                                        </Box>
                                        <Button variant="outline" color="gray" size="sm" style={{ fontSize: '15px' }}>
                                            Chi tiết
                                        </Button>
                                    </Group>
                                </Stack>
                            </Card>
                        </Carousel.Slide>
                    ))}
                </Carousel>

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

                    <Carousel
                        withIndicators
                        slideSize={{ base: '100%', sm: '50%', md: '33.333333%' }}
                        slideGap="md"
                        loop
                        align="start"
                        slidesToScroll={1}
                    >
                        {services.map((service, index) => {
                            const Icon = getServiceIcon(service.serviceCategory);
                            return (
                                <Carousel.Slide key={index}>
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
                                            {service.name}
                                        </Title>
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
                                </Carousel.Slide>
                            );
                        })}
                    </Carousel>

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
                                            {testimonial.name || 'Ẩn danh'}
                                        </Text>
                                        <Text c="dimmed" style={{ fontSize: '14px' }}>
                                            Ngày đăng: {testimonial.date || 'Gần đây'}
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
