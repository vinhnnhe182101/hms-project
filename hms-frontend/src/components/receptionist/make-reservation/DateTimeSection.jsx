import { Button, Grid, Group, NumberInput } from "@mantine/core";
import { DateTimePicker } from "@mantine/dates";
import { IconCalendar, IconUsers } from "@tabler/icons-react";
import { useMakeReservationArea } from "../../../hooks/common/area/make-reservation-area-provider";
import { useObjectState } from "../../../hooks/common/use-object-state";
import { SectionCard } from "../../common/SectionCard";
import { formatUtils } from "../../../utils/formatUtils";

export const DateTimeSection = () => {
    const { state: reservationRequest, setState: setReservationRequest } = useMakeReservationArea();
    const { data: localReservationRequest, updateField } = useObjectState(reservationRequest);
    const { checkInDate, checkOutDate, numberOfMembers } = localReservationRequest;

    // 1. Tạo mốc thời gian "hôm nay"
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Đưa về 0h để không bị lệch giây khi so sánh

    // 2. Logic cho Check-out: Phải sau Check-in 1 ngày
    const minCheckOutDate = checkInDate
        ? new Date(new Date(checkInDate).getTime() + 24 * 60 * 60 * 1000)
        : new Date(today.getTime() + 24 * 60 * 60 * 1000);

    const handleCheckAvailability = () => {
        setReservationRequest((prev) => ({
            ...prev,
            checkInDate,
            checkOutDate,
            numberOfMembers,
        }));
    };

    return (
        <SectionCard title="1. Date & Time">
            <Grid gutter="md" align="flex-end">
                <Grid.Col span={{ base: 12, sm: 4 }}>
                    <DateTimePicker
                        label="Check-in Date"
                        placeholder="Select date & time"
                        value={checkInDate}
                        // Thêm minDate là hôm nay
                        minDate={today}
                        onChange={(value) => {
                            updateField("checkInDate", formatUtils.formatDateISO(value));
                            // Nếu ngày check-out cũ nhỏ hơn ngày check-in mới + 1, hãy reset check-out
                            if (value && checkOutDate && new Date(checkOutDate) <= value) {
                                updateField("checkOutDate", null);
                            }
                        }}
                        leftSection={<IconCalendar size={15} />}
                        valueFormat="DD/MM/YYYY HH:mm"
                        radius="md"
                    />
                </Grid.Col>
                <Grid.Col span={{ base: 12, sm: 4 }}>
                    <DateTimePicker
                        label="Check-out Date"
                        placeholder="Select date & time"
                        value={checkOutDate}
                        // Check-out phải sau check-in ít nhất 1 ngày
                        minDate={minCheckOutDate}
                        onChange={(value) => updateField("checkOutDate", formatUtils.formatDateISO(value))}
                        leftSection={<IconCalendar size={15} />}
                        valueFormat="DD/MM/YYYY HH:mm"
                        radius="md"
                    />
                </Grid.Col>
                <Grid.Col span={{ base: 12, sm: 4 }}>
                    <Group align="flex-end" grow>
                        <NumberInput
                            label="Total Guests"
                            value={numberOfMembers}
                            onChange={(value) => {
                                updateField("numberOfMembers", value);
                                setReservationRequest((prev) => ({
                                    ...prev,
                                    numberOfMembers: value,
                                }));
                            }}
                            min={1}
                            leftSection={<IconUsers size={15} />}
                            radius="md"
                        />
                        <Button
                            onClick={handleCheckAvailability}
                            disabled={!checkInDate || !checkOutDate}
                            variant="filled"
                            color="blue"
                            radius="md"
                        >
                            Check Available
                        </Button>
                    </Group>
                </Grid.Col>
            </Grid>
        </SectionCard>
    );
};
