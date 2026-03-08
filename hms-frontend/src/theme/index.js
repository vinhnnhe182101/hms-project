import { createTheme, rem } from '@mantine/core';

export const theme = createTheme({
    primaryColor: 'teal',
    defaultRadius: 'md',
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji"',
    headings: {
        fontWeight: '600',
        sizes: {
            h1: { fontSize: rem(28), lineHeight: '1.2' },
            h2: { fontSize: rem(24), lineHeight: '1.3' },
            h3: { fontSize: rem(20), lineHeight: '1.4' },
        }
    },
    fontSizes: {
        xs: rem(12),
        sm: rem(14),
        md: rem(16),
        lg: rem(18),
        xl: rem(20),
    },
    spacing: {
        xs: rem(8),
        sm: rem(12),
        md: rem(16),
        lg: rem(24),
        xl: rem(32),
    },
    components: {
        Button: {
            defaultProps: {
                radius: 'md',
            }
        },
        Card: {
            defaultProps: {
                radius: 'md',
                shadow: 'sm',
                padding: 'md',
            }
        },
        Modal: {
            defaultProps: {
                radius: 'md',
                shadow: 'md',
            }
        },
        TextInput: {
            defaultProps: {
                radius: 'md',
            }
        },
        PasswordInput: {
            defaultProps: {
                radius: 'md',
            }
        },
        Select: {
            defaultProps: {
                radius: 'md',
            }
        }
    }
});
