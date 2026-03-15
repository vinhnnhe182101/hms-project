import {useStaffLayout} from "../../hooks/staff/layout/use-staff-layout.jsx";
import {NavLink} from "react-router-dom";
import {AppShell, Avatar, Burger, Group, Text} from "@mantine/core";
import {STAFF_NAVBAR_ITEMS} from "../../constants/staff.jsx";
import {Hotel} from "lucide-react";

export const StaffHeader = () => {
    const {isMobileOpen, toggle} = useStaffLayout();

    return (
            <AppShell.Header withBorder>
                <Group h="100%" px="md" justify="space-between">

                    {/* LEFT */}
                    <Group>
                        <Burger
                                opened={isMobileOpen}
                                onClick={toggle}
                                hiddenFrom="lg"
                                size="sm"
                        />

                        <Group gap="xs">
                            <Hotel size={28} color="var(--mantine-color-teal-600)"/>
                            <Text fw={600} size="lg">
                                HMS
                            </Text>
                            <Text size="sm" c="dimmed" visibleFrom="xs">
                                | Staff Portal
                            </Text>
                        </Group>
                    </Group>

                    {/* DESKTOP NAV */}
                    <Group visibleFrom="lg" gap={4}>
                        {STAFF_NAVBAR_ITEMS.map((item) => {
                            const Icon = item.icon;
                            return (
                                    <NavLink
                                            key={item.path}
                                            to={item.path}
                                            className={({isActive}) => `
                                    flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium transition-colors
                                    ${
                                                    isActive
                                                            ? "bg-teal-100 text-teal-700 hover:bg-teal-200"
                                                            : "text-gray-700 hover:bg-gray-100 hover:text-gray-900"
                                            }
                                `}
                                    >
                                        <Icon size={18}/>
                                        {item.label}
                                    </NavLink>
                            )
                        })}
                    </Group>

                    {/* AVATAR */}
                    <Avatar color="teal" radius="xl">
                        S
                    </Avatar>
                </Group>
            </AppShell.Header>
    );
}
