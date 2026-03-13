import {Outlet} from 'react-router-dom';
import {StaffHeader} from "./StaffHeader.jsx";
import {StaffNavbar} from "./StaffNavbar.jsx";
import {AppShell, rem} from "@mantine/core";


export const StaffLayout = () => {
    return (
            <div className="min-h-screen bg-background">
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
            </div>
    );
};
