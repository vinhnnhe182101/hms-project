import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import {
    TextInput,
    PasswordInput,
    Checkbox,
    Button,
    Paper,
    Title,
    Text,
    Container,
    Group,
    Stack,
    Divider,
    Box,
    Alert,
    Overlay
} from '@mantine/core';
import { IconBrandGoogle, IconArrowLeft, IconAlertCircle, IconLock, IconMail } from '@tabler/icons-react';

const AUTH_BG_URL = 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&q=80&w=1600';

export function LoginForm() {
    const navigate = useNavigate();
    const { login } = useAuth();

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        const result = await login(email, password);

        if (result.success) {
            const path = '/'; // Simple redirect for now
            window.location.href = path;
        } else {
            setError(result.error);
            setLoading(false);
        }
    };

    const handleGoogleLogin = () => {
        window.location.href = 'http://localhost:8080/oauth2/authorize/google';
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
                            Welcome back! Please enter your details.
                        </Text>
                    </Stack>

                    <Button
                        variant="outline"
                        color="gray"
                        fullWidth
                        onClick={handleGoogleLogin}
                        leftSection={<IconBrandGoogle size={18} color="#4285F4" />}
                        radius="md"
                        size="md"
                        styles={{
                            root: { transition: 'transform 0.2s ease' },
                        }}
                        onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-2px)'}
                        onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}
                    >
                        Continue with Google
                    </Button>

                    <Divider label="or continue with email" labelPosition="center" my="lg" />

                    <form onSubmit={handleSubmit}>
                        <Stack gap="md">
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
                                placeholder="Enter your password"
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

                            <Group justify="space-between" mt="xs">
                                <Checkbox label="Remember me" color="blue" radius="xs" />
                                <Text
                                    size="xs"
                                    color="blue"
                                    fw={600}
                                    style={{ cursor: 'pointer' }}
                                    onClick={() => navigate('/forgot-password')}
                                >
                                    Forgot password?
                                </Text>
                            </Group>

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
                                Sign In
                            </Button>
                        </Stack>
                    </form>

                    <Stack align="center" mt="xl" gap="md">
                        <Text size="sm" c="dimmed">
                            Don't have an account?{' '}
                            <Text
                                component="span"
                                fw={700}
                                color="blue"
                                style={{ cursor: 'pointer', textDecoration: 'underline' }}
                                onClick={() => navigate('/register')}
                            >
                                Create an account
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
