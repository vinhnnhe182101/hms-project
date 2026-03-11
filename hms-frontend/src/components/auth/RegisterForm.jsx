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
    Alert
} from '@mantine/core';
import { IconArrowLeft, IconAlertCircle } from '@tabler/icons-react';
import { authApi } from '../../apis/auth/authApi';

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
                setError(result.message || 'Đăng ký thất bại');
            }
        } catch (err) {
            const msg = err?.response?.data?.message || 'Đăng ký thất bại. Vui lòng thử lại.';
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
                            Tạo tài khoản mới
                        </Text>
                    </Stack>

                    <form onSubmit={handleSubmit}>
                        <Stack gap="md">
                            <TextInput
                                label="Họ và tên"
                                placeholder="Nguyễn Văn A"
                                required
                                value={fullName}
                                onChange={(e) => setFullName(e.target.value)}
                                disabled={loading}
                            />

                            <TextInput
                                label="Email"
                                placeholder="your@email.com"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                disabled={loading}
                            />

                            <PasswordInput
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
                                type="submit"
                                fullWidth
                                loading={loading}
                                color="teal"
                                size="md"
                                mt="sm"
                            >
                                Đăng ký
                            </Button>
                        </Stack>
                    </form>

                    <Stack align="center" mt="xl" gap="xs">
                        <Text size="sm">
                            Đã có tài khoản?{' '}
                            <Text
                                component="span"
                                fw={600}
                                color="teal"
                                style={{ cursor: 'pointer' }}
                                onClick={() => navigate('/auth/login')}
                            >
                                Đăng nhập ngay
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
