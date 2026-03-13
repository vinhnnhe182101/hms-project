import {StaffHeader} from "./StaffHeader.jsx";
import {StaffNavbar} from "./StaffNavbar.jsx";
import {AppShell, Container, rem} from "@mantine/core";
import {Outlet} from "react-router-dom";
import {useStaffLayout} from "../../hooks/staff/layout/use-staff-layout.jsx";

export const StaffLayoutContent = () => {
    const {isMobileOpened} = useStaffLayout();
    return (
            <AppShell
                    padding="md"
                    header={{height: 60}}
                    navbar={{
                        width: 240,
                        breakpoint: "lg",
                        collapsed: {mobile: !isMobileOpened, desktop: true},
                    }}
            >
                {/* HEADER */}
                <StaffHeader/>

                {/* MOBILE NAVBAR */}
                <StaffNavbar/>

                {/* CONTENT */}
                <AppShell.Main pt={`calc(${rem(60)} + var(--mantine-spacing-md))`}>
                    <Container size="xl">
                        <Outlet/>
                    </Container>
                </AppShell.Main>
            </AppShell>
    )
}
