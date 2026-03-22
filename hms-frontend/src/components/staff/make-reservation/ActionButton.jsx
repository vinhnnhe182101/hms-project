import { useNavigate } from "react-router-dom";
import { useMakeReservationArea } from "../../../hooks/common/area/make-reservation-area-provider";
import { reservationApi } from "../../../apis/staff/reservationApi";
import { Button, Group } from "@mantine/core";
import { IconCheck, IconX } from "@tabler/icons-react";
import { STAFF_MAP_ROUTES } from "../../../constants/staff";

export const ActionButton = () => {
    const navigate = useNavigate();
    const { state: reservationRequest } = useMakeReservationArea();

    const cancelHandler = () => {
        navigate(STAFF_MAP_ROUTES.RESERVATIONS);
    };

    const submitHandler = async () => {
        const reservationResponse = await reservationApi.makeReservation(reservationRequest);
        console.log("Reservation response:", reservationResponse);
        if (reservationResponse) {
            alert("Reservation created successfully!");
        }
    };

    return (
        <Group justify="flex-end" mt="xl">
            <Button variant="subtle" color="gray" leftSection={<IconX size={16} />} onClick={cancelHandler}>
                Cancel
            </Button>
            <Button color="teal" size="md" radius="md" leftSection={<IconCheck size={16} />} onClick={submitHandler}>
                Confirm Reservation
            </Button>
        </Group>
    );
};
