import { Container, Title, Text, Button, Group, Paper, Stack } from '@mantine/core';
import { IconLock, IconArrowLeft, IconHome } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export default function UnauthorizedPage() {
    const navigate = useNavigate();
    const { user, getDashboardPath } = useAuth();

    const handleGoBack = () => {
        navigate(-1);
    };

    const handleGoHome = () => {
        if (user) {
            navigate(getDashboardPath());
        } else {
            navigate('/');
        }
    };

    return (
        <Container size="md" style={{ minHeight: '100vh', display: 'flex', alignItems: 'center' }}>
            <Paper withBorder p="xl" radius="md" style={{ width: '100%' }}>
                <Stack align="center" gap="lg">
                    <div style={{
                        backgroundColor: 'var(--mantine-color-red-1)',
                        borderRadius: '50%',
                        padding: '20px',
                        display: 'inline-block'
                    }}>
                        <IconLock size={64} color="var(--mantine-color-red-6)" stroke={1.5} />
                    </div>

                    <Title order={1} size={48} c="red.6">403</Title>

                    <Title order={2} ta="center">
                        Truy cập bị từ chối
                    </Title>

                    <Text size="lg" c="dimmed" ta="center" maw={400}>
                        Bạn không có quyền truy cập trang này.
                        {user && ` Vai trò hiện tại của bạn là ${user.role}.`}
                    </Text>

                    <Group justify="center" mt="md">
                        <Button
                            variant="light"
                            leftSection={<IconArrowLeft size={16} />}
                            onClick={handleGoBack}
                        >
                            Quay lại
                        </Button>
                        <Button
                            leftSection={<IconHome size={16} />}
                            onClick={handleGoHome}
                        >
                            Về trang chủ
                        </Button>
                    </Group>

                    <Text size="sm" c="dimmed" ta="center">
                        Nếu bạn nghĩ đây là lỗi, vui lòng liên hệ quản trị viên.
                    </Text>
                </Stack>
            </Paper>
        </Container>
    );
}
