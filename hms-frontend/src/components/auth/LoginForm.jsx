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
    Alert
} from '@mantine/core';
import { IconBrandGoogle, IconArrowLeft, IconAlertCircle } from '@tabler/icons-react';

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
        window.location.href = 'http://localhost:8080/oauth2/authorization/google';
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
                            Chào mừng bạn quay trở lại!
                        </Text>
                    </Stack>

                    <Button
                        variant="default"
                        color="gray"
                        fullWidth
                        onClick={handleGoogleLogin}
                        leftSection={<IconBrandGoogle size={18} color="#4285F4" />}
                        radius="md"
                    >
                        Tiếp tục với Google
                    </Button>

                    <Divider label="Hoặc đăng nhập bằng email" labelPosition="center" my="lg" />

                    <form onSubmit={handleSubmit}>
                        <Stack gap="md">
                            <TextInput
                                label="Địa chỉ Email"
                                placeholder="your@email.com"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                disabled={loading}
                            />
                            <PasswordInput
                                label="Mật khẩu"
                                placeholder="Mật khẩu của bạn"
                                required
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                disabled={loading}
                            />

                            {error && (
                                <Alert icon={<IconAlertCircle size={16} />} title="Lỗi đăng nhập" color="red" radius="md">
                                    {error}
                                </Alert>
                            )}

                            <Group justify="space-between" mt="xs">
                                <Checkbox label="Ghi nhớ đăng nhập" color="teal" />
                                <Text
                                    size="sm"
                                    color="teal"
                                    style={{ cursor: 'pointer' }}
                                    onClick={() => navigate('/auth/forgot-password')}
                                >
                                    Quên mật khẩu?
                                </Text>
                            </Group>

                            <Button
                                type="submit"
                                fullWidth
                                loading={loading}
                                color="teal"
                                size="md"
                                mt="sm"
                            >
                                Đăng nhập
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
                                onClick={() => navigate('/auth/register')}
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
