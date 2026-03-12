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
    Select,
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { notifications } from '@mantine/notifications';
import { useAuth } from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';

export function RegisterForm() {
    const { register } = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [active, setActive] = useState(0);

    const form = useForm({
        initialValues: {
            // Account info
            email: '',
            password: '',
            confirmPassword: '',
            role: 'CUSTOMER',

            // Personal info
            fullName: '',
            phoneNumber: '',
            identityCard: '',
        },
        validate: {
            email: (value) => (/^\S+@\S+$/.test(value) ? null : 'Invalid email'),
            password: (value) => (value.length < 6 ? 'Password must be at least 6 characters' : null),
            confirmPassword: (value, values) =>
                value !== values.password ? 'Passwords do not match' : null,
            fullName: (value) => (value.length < 2 ? 'Name is too short' : null),
            phoneNumber: (value) => {
                if (!value) return null;
                return (/^\d{10,11}$/.test(value) ? null : 'Phone number must be 10-11 digits');
            },
            identityCard: (value) => {
                if (!value) return null;
                return (/^\d{9,12}$/.test(value) ? null : 'Identity card must be 9-12 digits');
            },
        },
    });

    const nextStep = () => {
        if (active === 0) {
            const { hasErrors } = form.validate(['email', 'password', 'confirmPassword']);
            if (!hasErrors) setActive(1);
        }
    };

    const prevStep = () => setActive(0);

    const handleSubmit = async (values) => {
        setLoading(true);

        const userData = {
            email: values.email,
            password: values.password,
            fullName: values.fullName,
            phoneNumber: values.phoneNumber,
            identityCard: values.identityCard,
            role: values.role,
        };

        const result = await register(userData);
        setLoading(false);

        if (result.success) {
            notifications.show({
                title: 'Success',
                message: result.message || 'Registration successful! Please login.',
                color: 'green',
            });
            navigate('/login');
        } else {
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
                    Create Account
                </Title>

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
                            <Select
                                label="Account Type"
                                placeholder="Select account type"
                                data={[
                                    { value: 'CUSTOMER', label: 'Customer' },
                                    { value: 'ADMIN', label: 'Administrator' },
                                    { value: 'HOUSEKEEPING', label: 'Housekeeping Staff' },
                                ]}
                                {...form.getInputProps('role')}
                            />
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
                        Completed! Click register to create your account.
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