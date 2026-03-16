import {ReservationProvider} from "../../hooks/common/list/reservation-list-provider.jsx";
import {Box, Button, Group, Title} from "@mantine/core";
import {IconPlus} from "@tabler/icons-react";
import {ReservationSearch} from "../../components/staff/reservation/ReservationSearch.jsx";
import {ReservationTable} from "../../components/staff/reservation/ReservationTable.jsx";
import {ReservationPagination} from "../../components/staff/reservation/ReservationPagination.jsx";
import {useNavigate} from "react-router-dom";
import {STAFF_MAP_ROUTES} from "../../constants/staff.jsx";

export const ReservationListPage = () => {

    const navigate = useNavigate();

    const makeReservationHandler = () => {
        navigate(STAFF_MAP_ROUTES.MAKE_RESERVATION.path);
    }

    return (
            <Box>
                <ReservationProvider>
                    {/* Page header */}
                    <Group justify="space-between" mb="lg" align="flex-end">
                        <Title order={2} fw={600} c="gray.8">
                            Reservation List
                        </Title>
                        <Button leftSection={<IconPlus size={16}/>} color="teal" radius="md"
                                onClick={makeReservationHandler}>
                            Create Reservation
                        </Button>
                    </Group>

                    {/* Search + Filter */}
                    <ReservationSearch/>

                    {/* Table */}
                    <ReservationTable/>

                    {/* Pagination */}
                    <ReservationPagination/>
                </ReservationProvider>
            </Box>
    )
}
