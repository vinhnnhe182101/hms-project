// components/ReservationSearch.jsx
// Filter bar: TextInput search + Select status + DateInput range + Search button

import {Button, Group, Select, TextInput} from "@mantine/core";
import {DateInput} from "@mantine/dates";
import {IconCalendar, IconSearch} from "@tabler/icons-react";
import {useObjectState} from "../../../hooks/common/use-object-state";
import {useReservationList} from "../../../hooks/common/list/reservation-list-provider.jsx";
import {STATUS_OPTIONS} from "../../../constants/reservation.jsx";

export const ReservationSearch = () => {
    const {searchParams, setSearchParams} = useReservationList();
    const {updateField, data: localSearchParams} = useObjectState(
            /**
             * @type {ReservationSearchParams}
             */
            (searchParams)
    );

    const searchHandler = () => {
        updateField("page", 0);
        setSearchParams(localSearchParams);
    };

    return (
            <Group gap="sm" mb="md" wrap="wrap">
                {/* Customer name */}
                <TextInput
                        placeholder="Customer name..."
                        leftSection={<IconSearch size={15}/>}
                        value={localSearchParams.guestName || ""}
                        onChange={(e) => updateField("guestName", e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && searchHandler()}
                        radius="md"
                        size="sm"
                        style={{flex: 1, minWidth: 200}}
                />

                {/* Customer Identity Card */}
                <TextInput
                        placeholder="Identity Card..."
                        leftSection={<IconSearch size={15}/>}
                        value={localSearchParams.identityCard || ""}
                        onChange={(e) => updateField("identityCard", e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && searchHandler()}
                        radius="md"
                        size="sm"
                        style={{flex: 1, minWidth: 200}}
                />

                {/* Status */}
                <Select
                        placeholder="Status"
                        data={STATUS_OPTIONS}
                        value={localSearchParams.status || null}
                        onChange={(val) => updateField("status", val)}
                        clearable
                        radius="md"
                        size="sm"
                        style={{width: 170}}
                />

                {/* Check-in from */}
                <DateInput
                        placeholder="Check-in from"
                        value={localSearchParams.checkInDateFrom ? new Date(localSearchParams.checkInDateFrom) : null}
                        onChange={(val) => updateField("checkInDateFrom", val?.toISOString())}
                        leftSection={<IconCalendar size={15}/>}
                        clearable
                        radius="md"
                        size="sm"
                        valueFormat="DD/MM/YYYY"
                        style={{width: 150}}
                />

                {/* To date */}
                <DateInput
                        placeholder="To date"
                        value={localSearchParams.checkInDateTo ? new Date(localSearchParams.checkInDateTo) : null}
                        onChange={(val) => updateField("checkInDateTo", val?.toISOString())}
                        leftSection={<IconCalendar size={15}/>}
                        clearable
                        radius="md"
                        size="sm"
                        valueFormat="DD/MM/YYYY"
                        style={{width: 150}}
                />

                {/* Search button */}
                <Button
                        leftSection={<IconSearch size={15}/>}
                        color="teal"
                        radius="md"
                        size="sm"
                        onClick={searchHandler}
                >
                    Search
                </Button>
            </Group>
    );
};