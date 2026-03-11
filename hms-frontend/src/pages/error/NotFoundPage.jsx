import { Container, Title, Text, Button, Group, Paper, Stack } from '@mantine/core';
import { IconMoodSad, IconArrowLeft, IconHome } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export default function NotFoundPage() {
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
                        backgroundColor: 'var(--mantine-color-yellow-1)',
                        borderRadius: '50%',
                        padding: '20px',
                        display: 'inline-block'
                    }}>
                        <IconMoodSad size={64} color="var(--mantine-color-yellow-6)" stroke={1.5} />
                    </div>

                    <Title order={1} size={48} c="yellow.6">404</Title>

                    <Title order={2} ta="center">
                        Không tìm thấy trang
                    </Title>

                    <Text size="lg" c="dimmed" ta="center" maw={400}>
                        Trang bạn đang tìm kiếm không tồn tại hoặc đã bị di dời.
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
                </Stack>
            </Paper>
        </Container>
    );
}
