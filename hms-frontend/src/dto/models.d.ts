/* tslint:disable */
/* eslint-disable */

interface AssignScheduleRequest {
    staffId: number;
    shiftId: number;
    startDate: DateAsString;
    endDate: DateAsString;
}

interface AssignTaskRequest {
    roomId: number;
    staffId: number;
    taskType: HousekeepingTaskType;
}

interface CustomerRequest {
    customerId: number;
    identityCard: string;
    fullName: string;
    phoneNumber: string;
    email: string;
}

interface DamageReportRequest {
    roomId: number;
    reservationId: number;
    description: string;
    quantity: number;
    assetId: number;
    penaltyAmount: number;
}

interface LoginRequest {
    email: string;
    password: string;
}

interface MinibarConsumptionRequest {
    roomId: number;
    items: MinibarItemRequest[];
}

interface PaymentRequest {
    folioItemIds: number[];
    paymentMethod: string;
    depositAmount: number;
    clientIp: string;
    returnUrl: string;
    note: string;
}

interface RegisterRequest {
    email: string;
    password: string;
    fullName: string;
    phoneNumber: string;
    identityCard: string;
    role: string;
}

interface ReservationCheckInRequest {
    autoAssign: boolean;
    roomAssignments: ReservationRoomCheckInRequest[];
}

interface ReservationRequest {
    customerRequest: CustomerRequest;
    roomClassQuantities: RoomClassQuantityRequest[];
    checkInDate: DateAsString;
    checkOutDate: DateAsString;
    numberOfMembers: number;
    note: string;
}

interface ReservationRoomCheckInRequest {
    reservationRoomId: number;
    roomId: number;
}

interface ReservationSearchFilter {
    guestName: string;
    status: ReservationStatus;
    checkInDateFrom: DateAsString;
    checkInDateTo: DateAsString;
}

interface RoomChangeRequest {
    newRoomId: number;
    changeType: RoomChangeType;
    note: string;
}

interface RoomClassQuantityRequest {
    roomClassId: number;
    numberOfPeople: number;
}

interface RoomSearchFilter {
    roomNumber: string;
    roomClassId: number;
    status: RoomStatus;
}

interface ServiceBookingRequest {
    serviceId: number;
    quantity: number;
    notes: string;
}

interface ServiceSearchFilter {
    name: string;
    category: ServiceCategory;
    status: boolean;
}

interface ShiftRequest {
    shiftName: string;
    startTime: DateAsString;
    endTime: DateAsString;
}

interface StaffAccountRequestDTO {
    email: string;
    fullName: string;
    phoneNumber: string;
    department: string;
    status: string;
    isActive: boolean;
}

interface StaffRequestDTO {
    fullName: string;
    phoneNumber: string;
    department: string;
    status: string;
    isActive: boolean;
    userId: number;
}

interface TaskStatusUpdateRequest {
    taskId: number;
    status: string;
}

interface UpdateServiceBookingRequest {
    quantity: number;
    notes: string;
}

interface UpdateTaskRequest {
    status: HousekeepingTaskStatus;
    taskType: HousekeepingTaskType;
    staffId: number;
}

interface UserRequestDTO {
    email: string;
    password: string;
    role: Role;
    provider: string;
    providerId: string;
    isActive: boolean;
}

interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
    timestamp: DateAsString;
}

interface AvailableRoomResponse {
    roomId: number;
    roomNumber: string;
}

interface CustomerResponse {
    id: number;
    fullName: string;
    phoneNumber: string;
    identityCard: string;
    email: string;
    type: string;
}

interface DamageReportResponse {
    id: number;
    roomNumber: string;
    reportedBy: string;
    description: string;
    quantity: number;
    penaltyAmount: number;
    status: string;
    createdAt: DateAsString;
    assetName: string;
    reservationCode: string;
    formattedCreatedAt: string;
    statusDisplay: string;
    statusColor: string;
    penaltyDisplay: string;
}

interface DashboardResponse {
    taskCounts: TaskCountResponse;
    todayTasks: HousekeepingTaskResponse[];
    roomStatus: RoomStatusResponse[];
    recentReports: DamageReportResponse[];
}

interface ErrorResponse {
    code: string;
    message: string;
    status: number;
    path: string;
    timestamp: DateAsString;
}

interface FolioItemResponse {
    id: number;
    type: string;
    description: string;
    quantity: number;
    totalPrice: number;
    status: string;
}

interface HousekeepingTaskResponse {
    id: number;
    roomId: number;
    roomNumber: string;
    assigneeId: number;
    assigneeName: string;
    taskType: HousekeepingTaskType;
    status: HousekeepingTaskStatus;
    assignedAt: DateAsString;
    completedAt: DateAsString;
}

interface HousekeepingTaskResponses {
    id: number;
    roomNumber: string;
    taskType: string;
    status: string;
    assignedAt: DateAsString;
    completedAt: DateAsString;
    roomStatus: RoomStatus;
    assigneeName: string;
    roomStatusDisplay: string;
    formattedCompletedAt: string;
    formattedAssignedAt: string;
    statusDisplay: string;
    statusColor: string;
    taskTypeDisplay: string;
    roomStatusColor: string;
}

interface LoginResponse {
    token: string;
    expiresIn: number;
}

interface MinibarConsumptionResponse {
    id: number;
    roomNumber: string;
    assetName: string;
    categoryName: string;
    quantityConsumed: number;
    priceAtTime: number;
    totalPrice: number;
    reportedBy: string;
    createdAt: DateAsString;
    remainingQuantity: number;
    formattedCreatedAt: string;
    totalPriceDisplay: string;
}

interface PaymentResponse {
    paymentId: number;
    paymentCode: string;
    paymentMethod: string;
    selectedItemsTotal: number;
    depositApplied: number;
    cashCollected: number;
    status: string;
    paymentUrl: string;
    createdAt: DateAsString;
}

interface RegisterResponse {
    id: number;
    email: string;
    role: string;
    fullName: string;
    phoneNumber: string;
    identityCard: string;
    provider: string;
    message: string;
}

interface ReservationResponse {
    bookingId: number;
    bookingCode: string;
    customer: CustomerResponse;
    allocations: RoomClassQuantityResponse[];
    checkInDate: DateAsString;
    checkOutDate: DateAsString;
    status: string;
    numberOfMembers: number;
    note: string;
    createdAt: DateAsString;
}

interface ReservationRoomCheckOutResponse {
    reservationRoomId: number;
    status: string;
    message: string;
}

interface ReservationRoomFolioResponse {
    reservationRoomId: number;
    roomNumber: string;
    roomClassName: string;
    occupants: RoomOccupantResponse[];
    folioItems: FolioItemResponse[];
    totalCharges: number;
    totalPaid: number;
    balance: number;
}

interface RoomAssetResponse {
    id: number;
    roomNumber: string;
    assetName: string;
    categoryName: string;
    currentQuantity: number;
    initialQuantity: number;
    price: number;
    status: string;
    statusDisplay: string;
    consumedQuantity: number;
    priceDisplay: string;
}

interface RoomClassAvailabilityResponse {
    roomClass: RoomClassResponse;
    availableRooms: number;
}

interface RoomClassAvailableRoomsResponse {
    roomClass: RoomClassResponse;
    availableRooms: AvailableRoomResponse[];
}

interface RoomClassQuantityResponse {
    id: number;
    roomClassId: number;
    numberOfPeople: number;
}

interface RoomClassResponse {
    id: number;
    name: string;
    basePrice: number;
    standardCapacity: number;
    maxCapacity: number;
    extraPersonFee: number;
}

interface RoomMatrixResponse {
    id: number;
    roomNumber: string;
    status: string;
    roomClassName: string;
}

interface RoomOccupantResponse {
    customer: CustomerResponse;
    role: string;
}

interface RoomResponse extends Serializable {
    id: number;
    roomNumber: string;
    roomClassId: number;
    roomClassName: string;
    status: RoomStatus;
    isActive: boolean;
}

interface ServiceBookingResponse {
    id: number;
    reservationRoomId: number;
    serviceId: number;
    serviceName: string;
    quantity: number;
    unitPrice: number;
    totalAmount: number;
    status: string;
    notes: string;
    createdAt: DateAsString;
}

interface ServiceResponse {
    id: number;
    name: string;
    serviceCategory: ServiceCategory;
    price: number;
    isActive: boolean;
}

interface ShiftResponse {
    id: number;
    shiftName: string;
    startTime: DateAsString;
    endTime: DateAsString;
    isActive: boolean;
}

interface StaffResponse {
    id: number;
    fullName: string;
    department: Department;
    status: string;
}

interface StaffResponseDTO {
    id: number;
    fullName: string;
    phoneNumber: string;
    department: string;
    status: string;
    isActive: boolean;
    userId: number;
    email: string;
}

interface TaskCountResponse {
    scheduled: number;
    inProgress: number;
    completed: number;
    total: number;
    completionRate: number;
    summaryText: string;
}

interface UserResponseDTO {
    id: number;
    email: string;
    role: Role;
    provider: string;
    isActive: boolean;
    staffId: number;
    customerId: number;
}

interface MinibarItemRequest {
    roomAssetId: number;
    quantity: number;
}

interface RoomStatusResponse {
    id: number;
    roomNumber: string;
    status: RoomStatus;
    roomClassName: string;
    hasTaskToday: boolean;
    statusDisplay: string;
    statusColor: string;
}

interface Serializable {
}

type DateAsString = string;

type HousekeepingTaskType = "CLEANING" | "INSPECTION" | "MAINTENANCE_SUPPORT";

type ReservationStatus = "PENDING_DEPOSIT" | "CONFIRMED" | "CANCELLED" | "IN_HOUSE" | "CHECKED_OUT" | "FINISHED";

type RoomChangeType = "CUSTOMER_REQUEST" | "ROOM_ISSUE";

type RoomStatus = "AVAILABLE" | "RESERVED" | "CLEAN" | "DIRTY" | "OCCUPIED" | "MAINTENANCE";

type ServiceCategory = "SPA" | "MINIBAR" | "F_AND_B";

type HousekeepingTaskStatus = "SCHEDULED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED" | "ASSIGNED";

type Role = "ADMIN" | "STAFF" | "CUSTOMER";

type Department = "RECEPTIONIST" | "HOUSEKEEPING";
