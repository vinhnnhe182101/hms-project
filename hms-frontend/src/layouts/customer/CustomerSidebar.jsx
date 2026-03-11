import { NavLink } from 'react-router-dom';
import { Stack, NavLink as MantineNavLink } from '@mantine/core';
import {
    IconDashboard,
    IconCalendarPlus,
    IconHistory,
    IconUser,
    IconHeart,
    IconStar,
    IconMessage
} from '@tabler/icons-react';

export function CustomerSidebar() {
    const navItems = [
        { label: 'Bảng điều khiển', icon: IconDashboard, to: '/customer' },
        { label: 'Đặt phòng', icon: IconCalendarPlus, to: '/booking' },
        { label: 'Lịch sử đặt phòng', icon: IconHistory, to: '/history' },
        { label: 'Yêu thích', icon: IconHeart, to: '/customer/favorites' },
        { label: 'Đánh giá', icon: IconStar, to: '/customer/reviews' },
        { label: 'Tin nhắn', icon: IconMessage, to: '/customer/messages' },
        { label: 'Hồ sơ', icon: IconUser, to: '/customer/profile' },
    ];

    return (
        <Stack>
            {navItems.map((item) => (
                <MantineNavLink
                    key={item.to}
                    component={NavLink}
                    to={item.to}
                    label={item.label}
                    leftSection={<item.icon size={16} />}
                    variant="light"
                    active={(location) => location.pathname === item.to}
                />
            ))}
        </Stack>
    );
}
