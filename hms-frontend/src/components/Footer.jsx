import { Container, Grid, Text, Stack } from '@mantine/core';

export default function Footer() {
    return (
        <footer style={{
            backgroundColor: '#1a2332',
            color: '#fff',
            padding: '60px 0 30px'
        }}>
            <Container size="xl">
                <Grid>
                    <Grid.Col span={{ base: 12, md: 3 }}>
                        <Stack gap="md">
                            <Text size="xl" fw={700} style={{ fontSize: '20px' }}>ROYAL HOTEL</Text>
                            <Text size="md" c="dimmed" style={{ fontSize: '14px' }}>
                                Sang trọng, tiện nghi và trải nghiệm đẳng cấp.
                            </Text>
                        </Stack>
                    </Grid.Col>

                    <Grid.Col span={{ base: 12, md: 3 }}>
                        <Stack gap="md">
                            <Text fw={600} style={{ fontSize: '16px' }}>Về Chúng Tôi</Text>
                            <Text size="md" c="dimmed" style={{ cursor: 'pointer', fontSize: '14px' }}>
                                Câu chuyện thương hiệu
                            </Text>
                            <Text size="md" c="dimmed" style={{ cursor: 'pointer', fontSize: '14px' }}>
                                Tuyển dụng
                            </Text>
                            <Text size="md" c="dimmed" style={{ cursor: 'pointer', fontSize: '14px' }}>
                                Liên hệ
                            </Text>
                        </Stack>
                    </Grid.Col>

                    <Grid.Col span={{ base: 12, md: 3 }}>
                        <Stack gap="md">
                            <Text fw={600} style={{ fontSize: '16px' }}>Hỗ Trợ</Text>
                            <Text size="md" c="dimmed" style={{ cursor: 'pointer', fontSize: '14px' }}>
                                Chính sách đặt phòng
                            </Text>
                            <Text size="md" c="dimmed" style={{ cursor: 'pointer', fontSize: '14px' }}>
                                Trung tâm trợ giúp
                            </Text>
                            <Text size="md" c="dimmed" style={{ cursor: 'pointer', fontSize: '14px' }}>
                                Chính sách bảo mật
                            </Text>
                        </Stack>
                    </Grid.Col>

                    <Grid.Col span={{ base: 12, md: 3 }}>
                        <Stack gap="md">
                            <Text fw={600} style={{ fontSize: '16px' }}>Liên Hệ</Text>
                            <Text size="md" c="dimmed" style={{ fontSize: '14px' }}>
                                123 Đường Biển, Nha Trang
                            </Text>
                            <Text size="md" c="dimmed" style={{ fontSize: '14px' }}>
                                contact@royalhotel.vn
                            </Text>
                            <Text size="md" c="dimmed" style={{ fontSize: '14px' }}>
                                +84 123 4567
                            </Text>
                        </Stack>
                    </Grid.Col>
                </Grid>

                <Text size="md" c="dimmed" ta="center" mt={50} style={{ fontSize: '14px' }}>
                    © 2024 Royal Hotel. All rights reserved.
                </Text>
            </Container>
        </footer>
    );
}
