// src/components/auth/RegisterForm.jsx
import { useState } from 'react';
import {
    TextInput,
    PasswordInput,
    Button,
    Paper,
    Title,
    Text,
    Anchor,
    Stack,
    Group,
    Box,
    Stepper,
    Alert,
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { notifications } from '@mantine/notifications';
import { useAuth } from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import { IconAlertCircle } from '@tabler/icons-react';

export function RegisterForm() {
    const { register } = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [active, setActive] = useState(0);
    const [error, setError] = useState(null);

    const form = useForm({
        initialValues: {
            email: '',
            password: '',
            confirmPassword: '',
            fullName: '',
            phoneNumber: '',
            identityCard: '',
        },
        validate: {
            email: (value) => (/^\S+@\S+$/.test(value) ? null : 'Invalid email'),
            password: (value) => (value.length < 6 ? 'Password must be at least 6 characters' : null),
            confirmPassword: (value, values) =>
                value !== values.password ? 'Passwords do not match' : null,
            fullName: (value) => null, // Tạm thời không validate
            phoneNumber: () => null,
            identityCard: () => null,
        },
    });

    const nextStep = () => {
        if (active === 0) {
            // Validate chỉ 3 field step 1
            const emailError = form.validateField('email');
            const passwordError = form.validateField('password');
            const confirmError = form.validateField('confirmPassword');

            console.log('Step 1 validation:', {
                email: emailError,
                password: passwordError,
                confirm: confirmError
            });

            // Nếu không có lỗi thì next
            if (!emailError.hasError && !passwordError.hasError && !confirmError.hasError) {
                setActive(1);
            } else {
                notifications.show({
                    title: 'Validation Error',
                    message: 'Please check your information',
                    color: 'red'
                });
            }
        }
    };

    const prevStep = () => setActive(0);

    const handleSubmit = async (values) => {
        setLoading(true);
        setError(null);

        const userData = {
            email: values.email,
            password: values.password,
            fullName: values.fullName,
            phoneNumber: values.phoneNumber,
            identityCard: values.identityCard,
            // role mặc định là CUSTOMER, không cần gửi
        };

        console.log('Submitting registration:', userData);

        const result = await register(userData);
        setLoading(false);

        if (result.success) {
            notifications.show({
                title: 'Success',
                message: result.message || 'Registration successful! Please login.',
                color: 'green',
            });

            // Chuyển về login sau 1.5s
            setTimeout(() => {
                navigate('/login');
            }, 1500);
        } else {
            setError(result.error);
            notifications.show({
                title: 'Error',
                message: result.error || 'Registration failed',
                color: 'red',
            });
        }
    };

    return (
        <Box style={{ maxWidth: 500 }} mx="auto">
            <Paper radius="md" p="xl" withBorder>
                <Title order={2} ta="center" mb="lg">
                    Create Customer Account
                </Title>

                {error && (
                    <Alert
                        icon={<IconAlertCircle size={16} />}
                        title="Registration Failed"
                        color="red"
                        mb="md"
                        withCloseButton
                        onClose={() => setError(null)}
                    >
                        {error}
                    </Alert>
                )}

                <Stepper active={active} onStepClick={setActive} mb="xl">
                    <Stepper.Step label="Account" description="Create account">
                        <Stack>
                            <TextInput
                                required
                                label="Email"
                                placeholder="your@email.com"
                                {...form.getInputProps('email')}
                            />
                            <PasswordInput
                                required
                                label="Password"
                                placeholder="Your password"
                                {...form.getInputProps('password')}
                            />
                            <PasswordInput
                                required
                                label="Confirm Password"
                                placeholder="Confirm your password"
                                {...form.getInputProps('confirmPassword')}
                            />
                            <Text size="sm" c="dimmed" fs="italic">
                                Note: You are registering as a Customer. Staff accounts are created by Admin.
                            </Text>
                        </Stack>
                    </Stepper.Step>

                    <Stepper.Step label="Personal" description="Personal information">
                        <Stack>
                            <TextInput
                                required
                                label="Full Name"
                                placeholder="Your full name"
                                {...form.getInputProps('fullName')}
                            />
                            <TextInput
                                required
                                label="Phone Number"
                                placeholder="Your phone number"
                                {...form.getInputProps('phoneNumber')}
                            />
                            <TextInput
                                label="Identity Card"
                                placeholder="ID/Passport number"
                                {...form.getInputProps('identityCard')}
                            />
                        </Stack>
                    </Stepper.Step>
                    <Stepper.Completed>
                        <Stack align="center" gap="md">
                            <Text size="lg" fw={500}>All set!</Text>
                            <Text c="dimmed" ta="center">
                                Click register to create your customer account.
                            </Text>
                        </Stack>
                    </Stepper.Completed>
                </Stepper>

                <Group justify="space-between" mt="xl">
                    {active === 1 && (
                        <Button variant="default" onClick={prevStep}>
                            Back
                        </Button>
                    )}
                    {active === 0 && (
                        <Button onClick={nextStep}>Next</Button>
                    )}
                    {active === 1 && (
                        <Button onClick={() => form.onSubmit(handleSubmit)()} loading={loading}>
                            Register
                        </Button>
                    )}
                </Group>

                <Text ta="center" mt="md">
                    Already have an account?{' '}
                    <Anchor component="button" onClick={() => navigate('/login')}>
                        Login
                    </Anchor>
                </Text>
            </Paper>
        </Box>
    );
}