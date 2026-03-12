import { Title, Text, Button, Group, Grid, Card, Image, Badge, Container } from '@mantine/core';
import { IconCalendarPlus, IconHotelService, IconWifi, IconCoffee, IconArrowRight } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export default function HomePage() {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();

    const featuredRooms = [
        {
            id: 1,
            name: 'Deluxe Suite',
            price: 199,
            image: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a',
            capacity: 2,
            amenities: ['Ocean View', 'King Bed', 'Free WiFi']
        },
        {
            id: 2,
            name: 'Executive Room',
            price: 299,
            image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b',
            capacity: 3,
            amenities: ['City View', 'Queen Bed', 'Breakfast']
        },
        {
            id: 3,
            name: 'Presidential Suite',
            price: 499,
            image: 'https://images.unsplash.com/photo-1590490360182-c33d5773345f',
            capacity: 4,
            amenities: ['Panoramic View', 'King Bed', 'Jacuzzi']
        },
    ];

    const handleBookRoom = (roomId) => {
        if (isAuthenticated) {
            navigate(`/booking/${roomId}`);
        } else {
            navigate('/auth/login', { state: { from: `/booking/${roomId}` } });
        }
    };

    return (
        <div>
            <div
                style={{
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    color: 'white',
                    padding: '80px 0',
                    marginBottom: '40px',
                    borderRadius: '0 0 20px 20px'
                }}
            >
                <Container size="lg">
                    <Title order={1} size={48} mb="md">Welcome to HMS Hotel</Title>
                    <Text size="xl" mb="xl" maw={600}>
                        Experience luxury and comfort at its finest. Book your stay with us and enjoy world-class amenities.
                    </Text>
                    <Group>
                        <Button
                            size="lg"
                            variant="white"
                            onClick={() => navigate('/rooms')}
                            rightSection={<IconArrowRight size={18} />}
                        >
                            View All Rooms
                        </Button>
                        {!isAuthenticated && (
                            <Button
                                size="lg"
                                variant="outline"
                                color="white"
                                onClick={() => navigate('/auth/register')}
                            >
                                Sign Up for Exclusive Deals
                            </Button>
                        )}
                    </Group>
                </Container>
            </div>

            <Container size="lg" mb={60}>
                <Title order={2} ta="center" mb="xl">Why Choose Us?</Title>
                <Grid>
                    <Grid.Col span={{ base: 12, md: 4 }}>
                        <Card shadow="sm" padding="lg" radius="md" withBorder>
                            <Group justify="center" mb="md">
                                <IconHotelService size={48} color="var(--mantine-color-blue-6)" />
                            </Group>
                            <Title order={3} ta="center" mb="xs">Luxury Rooms</Title>
                            <Text ta="center" c="dimmed">
                                Spacious and elegantly designed rooms for your comfort
                            </Text>
                        </Card>
                    </Grid.Col>
                    <Grid.Col span={{ base: 12, md: 4 }}>
                        <Card shadow="sm" padding="lg" radius="md" withBorder>
                            <Group justify="center" mb="md">
                                <IconWifi size={48} color="var(--mantine-color-blue-6)" />
                            </Group>
                            <Title order={3} ta="center" mb="xs">Free WiFi</Title>
                            <Text ta="center" c="dimmed">
                                Stay connected with high-speed internet throughout the hotel
                            </Text>
                        </Card>
                    </Grid.Col>
                    <Grid.Col span={{ base: 12, md: 4 }}>
                        <Card shadow="sm" padding="lg" radius="md" withBorder>
                            <Group justify="center" mb="md">
                                <IconCoffee size={48} color="var(--mantine-color-blue-6)" />
                            </Group>
                            <Title order={3} ta="center" mb="xs">Breakfast Included</Title>
                            <Text ta="center" c="dimmed">
                                Start your day with our delicious complimentary breakfast
                            </Text>
                        </Card>
                    </Grid.Col>
                </Grid>
            </Container>

            {/* Featured Rooms */}
            <Container size="lg" mb={60}>
                <Group justify="space-between" mb="xl">
                    <Title order={2}>Featured Rooms</Title>
                    <Button
                        variant="light"
                        onClick={() => navigate('/rooms')}
                        rightSection={<IconArrowRight size={16} />}
                    >
                        View All
                    </Button>
                </Group>

                <Grid>
                    {featuredRooms.map((room) => (
                        <Grid.Col key={room.id} span={{ base: 12, md: 4 }}>
                            <Card shadow="sm" padding="lg" radius="md" withBorder>
                                <Card.Section>
                                    <Image
                                        src={room.image}
                                        height={200}
                                        alt={room.name}
                                    />
                                </Card.Section>

                                <Group justify="space-between" mt="md" mb="xs">
                                    <Title order={3}>{room.name}</Title>
                                    <Badge color="blue" size="lg">${room.price}/night</Badge>
                                </Group>

                                <Text size="sm" c="dimmed" mb="md">
                                    Capacity: {room.capacity} persons
                                </Text>

                                <Group mb="md">
                                    {room.amenities.map((amenity, index) => (
                                        <Badge key={index} variant="light">{amenity}</Badge>
                                    ))}
                                </Group>

                                <Button
                                    fullWidth
                                    variant="light"
                                    leftSection={<IconCalendarPlus size={16} />}
                                    onClick={() => handleBookRoom(room.id)}
                                >
                                    {isAuthenticated ? 'Book Now' : 'Login to Book'}
                                </Button>
                            </Card>
                        </Grid.Col>
                    ))}
                </Grid>
            </Container>

            {/* Call to Action */}
            <div style={{
                background: '#f8f9fa',
                padding: '60px 0',
                borderRadius: '20px'
            }}>
                <Container size="lg" ta="center">
                    <Title order={2} mb="md">Ready to Experience Luxury?</Title>
                    <Text size="lg" mb="xl" maw={600} mx="auto">
                        Join thousands of satisfied guests who have enjoyed their stay at HMS Hotel.
                    </Text>
                    <Group justify="center">
                        <Button
                            size="lg"
                            onClick={() => navigate('/rooms')}
                        >
                            Browse Rooms
                        </Button>
                        {!isAuthenticated && (
                            <Button
                                size="lg"
                                variant="light"
                                onClick={() => navigate('/auth/register')}
                            >
                                Create Account
                            </Button>
                        )}
                    </Group>
                </Container>
            </div>
        </div>
    );
}