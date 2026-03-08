import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { login } from '../../apis/authApi';
import {
    TextInput,
    PasswordInput,
    Button,
    Paper,
    Title,
    Text,
    Container,
    Group,
    Divider,
    Stack,
    Box,
    Alert
} from '@mantine/core';
import { IconBrandGoogle, IconArrowLeft, IconAlertCircle } from '@tabler/icons-react';

export default function LoginPage() {
    const navigate = useNavigate();
    const { saveCustomer } = useAuth();

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            const result = await login(email, password);
            if (result.success) {
                saveCustomer(result.data);
                navigate('/');
            } else {
                setError(result.message);
            }
        } catch (err) {
            const msg = err?.response?.data?.message || 'Đăng nhập thất bại. Vui lòng thử lại.';
            setError(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box
            style={{
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, var(--mantine-color-teal-9) 0%, var(--mantine-color-teal-7) 50%, var(--mantine-color-teal-9) 100%)',
                padding: '20px',
            }}
        >
            <Container size={420} my={40}>
                <Paper radius="md" p="xl" withBorder shadow="md">
                    <Stack align="center" mb="lg">
                        <Title
                            order={2}
                            fw={900}
                            style={{ letterSpacing: '2px', color: 'var(--mantine-color-teal-9)' }}
                        >
                            ROYAL HOTEL
                        </Title>
                        <Text c="dimmed" size="sm" ta="center">
                            Chào mừng trở lại
                        </Text>
                    </Stack>

                    <form onSubmit={handleSubmit}>
                        <Stack gap="md">
                            <TextInput
                                id="login-email"
                                label="Email"
                                placeholder="your@email.com"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                disabled={loading}
                            />

                            <PasswordInput
                                id="login-password"
                                label="Mật khẩu"
                                placeholder="••••••••"
                                required
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                disabled={loading}
                            />

                            {error && (
                                <Alert icon={<IconAlertCircle size={16} />} title="Lỗi" color="red" radius="md">
                                    {error}
                                </Alert>
                            )}

                            <Button
                                id="login-submit"
                                type="submit"
                                fullWidth
                                loading={loading}
                                color="teal"
                                size="md"
                                mt="sm"
                            >
                                Đăng nhập
                            </Button>

                            <Divider label="Hoặc" labelPosition="center" my="sm" />

                            <Button
                                component="a"
                                href="http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:5173/oauth2/redirect"
                                variant="default"
                                leftSection={<IconBrandGoogle size={18} />}
                                fullWidth
                                size="md"
                            >
                                Đăng nhập với Google
                            </Button>
                        </Stack>
                    </form>

                    <Stack align="center" mt="xl" gap="xs">
                        <Text size="sm">
                            Chưa có tài khoản?{' '}
                            <Text
                                component="span"
                                fw={600}
                                color="teal"
                                style={{ cursor: 'pointer' }}
                                onClick={() => navigate('/register')}
                            >
                                Đăng ký ngay
                            </Text>
                        </Text>

                        <Group
                            gap={5}
                            style={{ cursor: 'pointer' }}
                            onClick={() => navigate('/')}
                        >
                            <IconArrowLeft size={14} color="var(--mantine-color-gray-6)" />
                            <Text size="xs" c="dimmed">
                                Quay về trang chủ
                            </Text>
                        </Group>
                    </Stack>
                </Paper>
            </Container>
        </Box>
    );
}
