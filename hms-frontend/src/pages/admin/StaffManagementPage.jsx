import { useState, useEffect } from 'react';
import {
    Title, Paper, Group, Button, TextInput, Select,
    Table, ActionIcon, Badge, Pagination, Text, LoadingOverlay
} from '@mantine/core';
import { useDisclosure, useDebouncedValue } from '@mantine/hooks';
import { modals } from '@mantine/modals';
import { notifications } from '@mantine/notifications';
import { IconPlus, IconEye, IconTrash, IconSearch, IconFilter } from '@tabler/icons-react';

import { staffApi } from '../../apis/admin/staffApi';
import { StaffCreateModal } from '../../components/admin/staff/StaffCreateModal';
import { StaffDetailModal } from '../../components/admin/staff/StaffDetailModal';

export default function StaffManagementPage() {
    // States
    const [staffs, setStaffs] = useState([]);
    const [loading, setLoading] = useState(false);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [page, setPage] = useState(1); // Mantine UI dùng page từ 1, Spring Boot dùng từ 0

    // Filters
    const [searchName, setSearchName] = useState('');
    const [debouncedSearchName] = useDebouncedValue(searchName, 500);
    const [filterDept, setFilterDept] = useState(null);

    // Modals
    const [createOpened, { open: openCreate, close: closeCreate }] = useDisclosure(false);
    const [detailOpened, { open: openDetail, close: closeDetail }] = useDisclosure(false);
    const [selectedStaff, setSelectedStaff] = useState(null);

    // Lấy dữ liệu
    const fetchStaffs = async () => {
        setLoading(true);
        try {
            const params = {
                page: page - 1, // Convert sang zero-based cho Backend
                size: 6,        // Yêu cầu: 6 nhân viên 1 trang
                name: debouncedSearchName || undefined,
                department: filterDept || undefined
            };
            const res = await staffApi.getStaffs(params);

            setStaffs(res.content || []);
            setTotalPages(res.totalPages || 1);
            setTotalElements(res.totalElements || 0);
        } catch (error) {
            notifications.show({ title: 'Lỗi', message: 'Không thể tải danh sách nhân viên', color: 'red' });
        } finally {
            setLoading(false);
        }
    };

    // Gọi lại API khi page hoặc filter thay đổi
    useEffect(() => {
        fetchStaffs();
    }, [page, debouncedSearchName, filterDept]);

    // Handle View
    const handleViewDetail = (staff) => {
        setSelectedStaff(staff);
        openDetail();
    };

    // Handle Delete (Disable)
    const handleDelete = (id) => {
        modals.openConfirmModal({
            title: 'Xác nhận xóa nhân viên',
            children: <Text size="sm">Bạn có chắc chắn muốn vô hiệu hóa/xóa nhân viên này không?</Text>,
            labels: { confirm: 'Xóa', cancel: 'Hủy' },
            confirmProps: { color: 'red' },
            onConfirm: async () => {
                try {
                    await staffApi.deleteStaff(id);
                    notifications.show({ title: 'Thành công', message: 'Đã xóa nhân viên', color: 'green' });
                    fetchStaffs();
                } catch (error) {
                    notifications.show({ title: 'Lỗi', message: 'Xóa thất bại', color: 'red' });
                }
            },
        });
    };

    return (
        <div style={{ position: 'relative', minHeight: '400px' }}>
            <LoadingOverlay visible={loading} zIndex={1000} overlayProps={{ radius: "sm", blur: 2 }} />

            <Group justify="space-between" mb="lg">
                <Title order={2}>Quản lý nhân viên</Title>
                <Button leftSection={<IconPlus size={16} />} onClick={openCreate}>
                    Tạo tài khoản
                </Button>
            </Group>

            <Paper shadow="sm" p="md" mb="xl" radius="md" withBorder>
                <Group mb="md" justify="space-between">
                    <Group>
                        <TextInput
                            placeholder="Tìm theo tên..."
                            leftSection={<IconSearch size={16} />}
                            value={searchName}
                            onChange={(e) => {
                                setSearchName(e.currentTarget.value);
                                setPage(1); // Reset về trang 1 khi search
                            }}
                            w={250}
                        />
                        <Select
                            placeholder="Lọc theo bộ phận"
                            leftSection={<IconFilter size={16} />}
                            data={[
                                { value: 'HOUSEKEEPING', label: 'Buồng phòng' },
                                { value: 'RECEPTION', label: 'Lễ tân' },
                                { value: 'MANAGER', label: 'Quản lý' },
                            ]}
                            value={filterDept}
                            onChange={(val) => {
                                setFilterDept(val);
                                setPage(1);
                            }}
                            clearable
                            w={200}
                        />
                    </Group>
                    <Text size="sm" c="dimmed">Tổng số: {totalElements} nhân viên</Text>
                </Group>

                <Table striped highlightOnHover verticalSpacing="sm">
                    <Table.Thead>
                        <Table.Tr>
                            <Table.Th>ID</Table.Th>
                            <Table.Th>Họ và tên</Table.Th>
                            <Table.Th>Email</Table.Th>
                            <Table.Th>Bộ phận</Table.Th>
                            <Table.Th>Trạng thái</Table.Th>
                            <Table.Th style={{ textAlign: 'center' }}>Thao tác</Table.Th>
                        </Table.Tr>
                    </Table.Thead>
                    <Table.Tbody>
                        {staffs.length > 0 ? (
                            staffs.map((staff) => (
                                <Table.Tr key={staff.id}>
                                    <Table.Td>{staff.id}</Table.Td>
                                    <Table.Td fw={500}>{staff.fullName}</Table.Td>
                                    <Table.Td>{staff.email}</Table.Td>
                                    <Table.Td>
                                        <Badge color="blue" variant="light">{staff.department}</Badge>
                                    </Table.Td>
                                    <Table.Td>
                                        <Badge color={staff.isActive ? 'green' : 'red'}>
                                            {staff.isActive ? 'Active' : 'Disabled'}
                                        </Badge>
                                    </Table.Td>
                                    <Table.Td style={{ textAlign: 'center' }}>
                                        <Group justify="center" gap="xs">
                                            <ActionIcon variant="subtle" color="blue" onClick={() => handleViewDetail(staff)}>
                                                <IconEye size={18} />
                                            </ActionIcon>
                                            <ActionIcon variant="subtle" color="red" onClick={() => handleDelete(staff.id)}>
                                                <IconTrash size={18} />
                                            </ActionIcon>
                                        </Group>
                                    </Table.Td>
                                </Table.Tr>
                            ))
                        ) : (
                            <Table.Tr>
                                <Table.Td colSpan={6} style={{ textAlign: 'center', padding: '2rem' }}>
                                    <Text c="dimmed">Không tìm thấy nhân viên nào</Text>
                                </Table.Td>
                            </Table.Tr>
                        )}
                    </Table.Tbody>
                </Table>

                {/* Phân trang */}
                {totalPages > 1 && (
                    <Group justify="center" mt="md">
                        <Pagination
                            total={totalPages}
                            value={page}
                            onChange={setPage}
                            color="blue"
                            withEdges
                        />
                    </Group>
                )}
            </Paper>

            {/* Khai báo các Modals */}
            <StaffCreateModal
                opened={createOpened}
                onClose={closeCreate}
                onSuccess={fetchStaffs}
            />

            <StaffDetailModal
                opened={detailOpened}
                onClose={closeDetail}
                staff={selectedStaff}
            />
        </div>
    );
}