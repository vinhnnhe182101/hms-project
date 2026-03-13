import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    TextInput,
    PasswordInput,
    Button,
    Paper,
    Title,
    Text,
    Container,
    Group,
    Stack,
    Box,
    Alert,
    Overlay
} from '@mantine/core';
import { IconArrowLeft, IconAlertCircle, IconUser, IconMail, IconLock } from '@tabler/icons-react';
import { authApi } from '../../apis/auth/authApi';

const AUTH_BG_URL = 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?q=80&w=1600&auto=format&fit=crop';

export function RegisterForm() {
    const navigate = useNavigate();

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [fullName, setFullName] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            const result = await authApi.register({ email, password, fullName });
            if (result.success || result.data) {
                navigate('/auth/login');
            } else {
                setError(result.message || 'Registration failed');
            }
        } catch (err) {
            const msg = err?.response?.data?.message || 'Registration failed. Please try again.';
            setError(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box
            style={{
                minHeight: '100vh',
                position: 'relative',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                backgroundImage: `url(${AUTH_BG_URL})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                padding: '40px 20px',
            }}
        >
            <Overlay color="#000" opacity={0.4} zIndex={1} />

            <Container size={420} style={{ position: 'relative', zIndex: 2, width: '100%' }}>
                <Paper
                    radius="xl"
                    p={40}
                    style={{
                        backgroundColor: 'rgba(255, 255, 255, 0.9)',
                        backdropFilter: 'blur(10px)',
                        boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
                        border: '1px solid rgba(255, 255, 255, 0.3)'
                    }}
                >
                    <Stack align="center" mb={30}>
                        <Title
                            order={1}
                            fw={900}
                            style={{
                                letterSpacing: '2px',
                                color: 'var(--mantine-color-blue-9)',
                                fontSize: '32px',
                                textTransform: 'uppercase'
                            }}
                        >
                            FPTU HOTEL
                        </Title>
                        <Text c="dimmed" size="sm" ta="center" fw={500}>
                            Create your account to start booking.
                        </Text>
                    </Stack>

                    <form onSubmit={handleSubmit}>
                        <Stack gap="md">
                            <TextInput
                                label="Full Name"
                                placeholder="Your full name"
                                required
                                value={fullName}
                                onChange={(e) => setFullName(e.target.value)}
                                disabled={loading}
                                leftSection={<IconUser size={16} />}
                                radius="md"
                                size="md"
                            />

                            <TextInput
                                label="Email Address"
                                placeholder="name@example.com"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                disabled={loading}
                                leftSection={<IconMail size={16} />}
                                radius="md"
                                size="md"
                            />

                            <PasswordInput
                                label="Password"
                                placeholder="Create a password"
                                required
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                disabled={loading}
                                leftSection={<IconLock size={16} />}
                                radius="md"
                                size="md"
                            />

                            {error && (
                                <Alert icon={<IconAlertCircle size={16} />} color="red" radius="md" variant="light">
                                    {error}
                                </Alert>
                            )}

                            <Button
                                type="submit"
                                fullWidth
                                loading={loading}
                                color="blue"
                                size="lg"
                                mt="xl"
                                radius="md"
                                style={{
                                    boxShadow: '0 10px 15px -3px rgba(34, 139, 230, 0.3)',
                                    height: '50px'
                                }}
                            >
                                Register Now
                            </Button>
                        </Stack>
                    </form>

                    <Stack align="center" mt="xl" gap="md">
                        <Text size="sm" c="dimmed">
                            Already have an account?{' '}
                            <Text
                                component="span"
                                fw={700}
                                color="blue"
                                style={{ cursor: 'pointer', textDecoration: 'underline' }}
                                onClick={() => navigate('/auth/login')}
                            >
                                Sign in here
                            </Text>
                        </Text>

                        <Group
                            gap={5}
                            style={{ cursor: 'pointer', opacity: 0.7 }}
                            onClick={() => navigate('/')}
                            onMouseEnter={(e) => e.currentTarget.style.opacity = 1}
                            onMouseLeave={(e) => e.currentTarget.style.opacity = 0.7}
                        >
                            <IconArrowLeft size={14} />
                            <Text size="xs" fw={500}>
                                Back to website
                            </Text>
                        </Group>
                    </Stack>
                </Paper>
            </Container>
        </Box>
    );
}
