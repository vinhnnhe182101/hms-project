import {
    ActionIcon,
    Badge,
    Box,
    Button,
    Center,
    Group,
    Pagination,
    Paper,
    Select,
    Table,
    Text,
    TextInput,
    Title,
    Tooltip,
} from "@mantine/core";
import {IconEye, IconSearch} from "@tabler/icons-react";
import {useCallback, useEffect, useMemo, useState} from "react";
import {roomApi} from "../../../apis/receptionist/roomApi.js";
import {ROOM_STATUS} from "../../../constants/housekeeping.js";
import {roomClassApi} from "../../../apis/receptionist/roomClassApi.js";

const PAGE_SIZE = 10;

export const ListRoomOccupantPage = ({onNavigate}) => {
    // --- States ---
    const [roomNumber, setRoomNumber] = useState("");
    const [roomClassId, setRoomClassId] = useState(null);

    /**
     * @type {[RoomClassResponse[], React.Dispatch<React.SetStateAction<RoomClassResponse[]>>]}
     */
    const [roomClasses, setRoomClasses] = useState(
            /** @type {RoomClassResponse[]} */
            []
    );

    const [page, setPage] = useState(1);
    const [data, setData] = useState([]);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(false);

    // --- Biến đổi dữ liệu cho Select ---
    // Tách riêng để đảm bảo luôn có thuộc tính 'value' (string) và 'label'
    const roomClassOptions = useMemo(() => {
        if (!Array.isArray(roomClasses)) return [];
        return roomClasses.map((item) => ({
            value: String(item.id),
            label: item.name || `Class ${item.id}`,
        }));
    }, [roomClasses]);

    // --- API Calls ---
    const fetchClasses = useCallback(async () => {
        try {
            const res = await roomClassApi.getAll();
            setRoomClasses(res || []);
        } catch (err) {
            console.error("Failed to fetch room classes", err);
            setRoomClasses([]);
        }
    }, []);

    const fetchData = useCallback(async (rNum, rcId, p) => {
        setLoading(true);
        try {
            const params = {
                roomNumber: rNum || undefined,
                roomClassId: rcId || undefined,
                status: ROOM_STATUS.OCCUPIED.value,
                page: p - 1,
                size: PAGE_SIZE,
            };

            const res = await roomApi.getRooms(params);
            setData(res.content ?? []);
            setTotal(res.totalElements ?? 0);
        } catch (err) {
            console.error("Fetch occupants error:", err);
        } finally {
            setLoading(false);
        }
    }, []);

    // Initial load
    useEffect(() => {
        fetchClasses();
    }, [fetchClasses]);

    useEffect(() => {
        fetchData(roomNumber, roomClassId, page);
    }, [page, fetchData]);

    const handleSearch = () => {
        setPage(1);
        fetchData(roomNumber, roomClassId, 1);
    };

    const totalPages = Math.ceil(total / PAGE_SIZE);
    const from = total === 0 ? 0 : (page - 1) * PAGE_SIZE + 1;
    const to = Math.min(page * PAGE_SIZE, total);

    return (
            <Box>
                <Title order={2} fw={600} c="gray.8" mb="lg">Room Occupants</Title>

                {/* Filter Section */}
                <Group gap="sm" mb="md" align="flex-end">
                    <TextInput
                            label="Room Number"
                            placeholder="E.g. 101"
                            leftSection={<IconSearch size={15}/>}
                            value={roomNumber}
                            onChange={(e) => setRoomNumber(e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                            radius="md" size="sm" style={{width: 180}}
                    />

                    <Select
                            label="Room Class"
                            placeholder="All classes"
                            data={roomClassOptions} // Sử dụng biến đã tách riêng
                            value={roomClassId}
                            onChange={setRoomClassId}
                            clearable
                            searchable
                            nothingFoundMessage="No classes found"
                            radius="md" size="sm" style={{width: 200}}
                    />

                    <Button
                            leftSection={<IconSearch size={15}/>}
                            color="teal"
                            radius="md" size="sm"
                            loading={loading}
                            onClick={handleSearch}
                    >
                        Search
                    </Button>
                </Group>

                {/* Table */}
                <Paper radius="md" shadow="xs" withBorder
                       style={{borderColor: "var(--mantine-color-gray-2)", overflowX: "auto"}}>
                    <Table horizontalSpacing="md" verticalSpacing="sm" highlightOnHover
                           styles={{
                               thead: {backgroundColor: "var(--mantine-color-gray-0)"},
                               th: {color: "var(--mantine-color-gray-6)", fontWeight: 500, fontSize: 13},
                           }}>
                        <Table.Thead>
                            <Table.Tr>
                                <Table.Th>Room</Table.Th>
                                <Table.Th>Class</Table.Th>
                                <Table.Th>Booking Code</Table.Th>
                                <Table.Th>Guest</Table.Th>
                                <Table.Th>Stay Dates</Table.Th>
                                <Table.Th>Status</Table.Th>
                                <Table.Th/>
                            </Table.Tr>
                        </Table.Thead>
                        <Table.Tbody>
                            {data.length > 0 ? data.map((r) => (
                                    <Table.Tr key={r.id}>
                                        <Table.Td><Text fw={700} size="sm" c="teal">{r.roomNumber}</Text></Table.Td>
                                        <Table.Td><Text size="sm">{r.roomClass?.className || "N/A"}</Text></Table.Td>
                                        <Table.Td><Text fw={600}
                                                        size="sm">{r.currentReservation?.bookingCode || "N/A"}</Text></Table.Td>
                                        <Table.Td>
                                            <Text size="sm">{r.currentReservation?.customer?.fullName || "No Guest"}</Text>
                                            <Text size="xs"
                                                  c="dimmed">{r.currentReservation?.customer?.phoneNumber}</Text>
                                        </Table.Td>
                                        <Table.Td>
                                            <Text size="xs"
                                                  fw={500}>In: {r.currentReservation?.checkInDate?.slice(0, 10)}</Text>
                                            <Text size="xs"
                                                  c="dimmed">Out: {r.currentReservation?.checkOutDate?.slice(0, 10)}</Text>
                                        </Table.Td>
                                        <Table.Td>
                                            <Badge color="teal" variant="light" size="sm" radius="xl">Occupied</Badge>
                                        </Table.Td>
                                        <Table.Td>
                                            <Tooltip label="View detail">
                                                <ActionIcon variant="subtle" color="gray" size="sm"
                                                            onClick={() => onNavigate?.("occupant-detail", r)}>
                                                    <IconEye size={15}/>
                                                </ActionIcon>
                                            </Tooltip>
                                        </Table.Td>
                                    </Table.Tr>
                            )) : (
                                    <Table.Tr>
                                        <Table.Td colSpan={7}>
                                            <Center py="xl">
                                                <Text c="dimmed" size="sm">
                                                    {loading ? "Loading..." : "No occupied rooms found"}
                                                </Text>
                                            </Center>
                                        </Table.Td>
                                    </Table.Tr>
                            )}
                        </Table.Tbody>
                    </Table>
                </Paper>

                {/* Pagination */}
                {total > 0 && (
                        <Group justify="space-between" align="center" mt="md" wrap="wrap" gap="sm">
                            <Text size="xs" c="dimmed">Showing {from}–{to} of {total} records</Text>
                            {totalPages > 1 && (
                                    <Pagination value={page} onChange={setPage} total={totalPages} color="teal"
                                                radius="md" size="sm" withEdges/>
                            )}
                        </Group>
                )}
            </Box>
    );
};