import { Container, Group, Text, Anchor } from '@mantine/core';

export function CustomerFooter() {
    return (
        <Container size="lg" style={{ height: '100%' }}>
            <Group justify="space-between" style={{ height: '100%' }}>
                <Text size="sm" c="dimmed">
                    © 2026 HMS Hotel. All rights reserved.
                </Text>
                <Group gap="xl">
                    <Anchor size="sm" c="dimmed" href="#">Privacy Policy</Anchor>
                    <Anchor size="sm" c="dimmed" href="#">Terms of Service</Anchor>
                    <Anchor size="sm" c="dimmed" href="#">Contact</Anchor>
                </Group>
            </Group>
        </Container>
    );
}