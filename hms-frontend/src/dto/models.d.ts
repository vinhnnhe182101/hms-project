/* tslint:disable */
/* eslint-disable */

type AssignScheduleRequest = {
    staffId: number;
    shiftId: number;
    startDate: DateAsString;
    endDate: DateAsString;
}

type AssignTaskRequest = {
    roomId: number;
    staffId: number;
    taskType: HousekeepingTaskType;
}

type BookingRequestDTO = {
    checkIn: DateAsString;
    checkOut: DateAsString;
    nights: number;
    guests: number;
    rooms: BookingRequestDTORoomBookingRequest[];
    customer: BookingRequestDTOCustomerRequest;
}

type BookingRequestDTOCustomerRequest = {
    customerId: number;
    name: string;
    phone: string;
    identityCard: string;
    note: string;
}

type BookingRequestDTORoomBookingRequest = {
    id: number;
    name: string;
    quantity: number;
    pricePerNight: number;
    total: number;
}

type CreateRoomRequest = {
    roomNumber: string;
    roomClassId: number;
    status: RoomStatus;
    description: string;
}

type CreateServiceRequest = {
    name: string;
    serviceCategory: ServiceCategory;
    price: number;
}

type CustomerRequest = {
    customerId: number;
    identityCard: string;
    fullName: string;
    phoneNumber: string;
    email: string;
}

type DamageReportRequest = {
    roomId: number;
    reservationId: number;
    description: string;
    quantity: number;
    penaltyAmount: number;
    assetId: number;
}

type LoginRequest = {
    email: string;
    password: string;
}

type MinibarConsumptionRequest = {
    roomId: number;
    reservationId: number;
    items: MinibarConsumptionRequestMinibarItem[];
}

type MinibarConsumptionRequestMinibarItem = {
    roomAssetId: number;
    quantity: number;
}

type PaymentRequest = {
    folioItemIds: number[];
    paymentMethod: string;
    depositAmount: number;
    clientIp: string;
    returnUrl: string;
    note: string;
}

type RegisterRequest = {
    email: string;
    password: string;
    fullName: string;
    phoneNumber: string;
    identityCard: string;
    role: string;
}

type ReservationCheckInRequest = {
    autoAssign: boolean;
    roomAssignments: ReservationRoomCheckInRequest[];
}

type ReservationRequest = {
    customerRequest: CustomerRequest;
    roomClassQuantities: RoomClassQuantityRequest[];
    checkInDate: DateAsString;
    checkOutDate: DateAsString;
    numberOfMembers: number;
    note: string;
}

type ReservationRoomCheckInRequest = {
    reservationRoomId: number;
    roomId: number;
}

type ReservationSearchFilter = {
    guestName: string;
    status: ReservationStatus;
    checkInDateFrom: DateAsString;
    checkInDateTo: DateAsString;
}

type RoomChangeRequest = {
    newRoomId: number;
    changeType: RoomChangeType;
    note: string;
}

type RoomClassQuantityRequest = {
    roomClassId: number;
    numberOfPeople: number;
}

type RoomSearchFilter = {
    roomNumber: string;
    roomClassId: number;
    status: RoomStatus;
}

type ServiceBookingRequest = {
    serviceId: number;
    quantity: number;
    notes: string;
}

type ServiceBookingRequestDTO = {
    customerId: number;
    items: ServiceBookingRequestDTOServiceItem[];
}

type ServiceBookingRequestDTOServiceItem = {
    serviceId: number;
    allocationId: number;
    quantity: number;
    price: number;
}

type ServiceSearchFilter = {
    name: string;
    category: ServiceCategory;
    status: boolean;
}

type ShiftRequest = {
    shiftName: string;
    startTime: DateAsString;
    endTime: DateAsString;
}

type StaffAccountRequestDTO = {
    email: string;
    fullName: string;
    phoneNumber: string;
    department: string;
    status: string;
    isActive: boolean;
}

type StaffRequestDTO = {
    fullName: string;
    phoneNumber: string;
    department: string;
    status: string;
    isActive: boolean;
    userId: number;
}

type TaskStatusUpdateRequest = {
    taskId: number;
    status: string;
}

type UpdateServiceBookingRequest = {
    quantity: number;
    notes: string;
}

type UpdateTaskRequest = {
    status: HousekeepingTaskStatus;
    taskType: HousekeepingTaskType;
    staffId: number;
}

type UpsertRoomClassRequest = {
    name: string;
    standardCapacity: number;
    maxCapacity: number;
    basePrice: number;
    extraPersonFee: number;
}

type UpsertRoomTypeRequest = {
    typeName: string;
    standardOccupancy: number;
    maxOccupancy: number;
    baseRatePerNight: number;
}

type UserRequestDTO = {
    email: string;
    password: string;
    role: Role;
    provider: string;
    providerId: string;
    isActive: boolean;
}

type ActiveAllocationResponseDTO = {
    allocationId: number;
    reservationId: number;
    roomNumber: string;
    roomClassName: string;
}

type AdminRoomTypeResponse = {
    id: number;
    typeName: string;
    standardOccupancy: number;
    maxOccupancy: number;
    baseRatePerNight: number;
}

type AdminRoomTypeResponseAdminRoomTypeResponseBuilder = {
}

interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
    timestamp: DateAsString;
}

interface ApiResponseApiResponseBuilder<T> {
}

type AssetResponse = {
    id: number;
    name: string;
    categoryName: string;
    quantity: number;
    status: string;
}

type AssetResponseAssetResponseBuilder = {
}

type AvailableRoomResponse = {
    roomId: number;
    roomNumber: string;
}

type BookingResponseDTO = {
    reservationId: number;
    reservationCode: string;
    totalAmount: number;
    depositAmount: number;
    paymentUrl: string;
}

type BookingResponseDTOBookingResponseDTOBuilder = {
}

type CustomerResponse = {
    id: number;
    fullName: string;
    phoneNumber: string;
    identityCard: string;
    email: string;
    type: string;
}

type DamageReportResponse = {
    id: number;
    roomNumber: string;
    description: string;
    quantity: number;
    penaltyAmount: number;
    status: string;
    createdAt: DateAsString;
    reportedBy: string;
    statusDisplay: string;
    formattedPenalty: string;
    formattedCreatedAt: string;
}

type DamageReportResponseDamageReportResponseBuilder = {
}

type DashboardResponse = {
    taskCounts: TaskCountResponse;
    todayTasks: HousekeepingTaskResponse[];
    roomStatus: DashboardResponseRoomStatusResponse[];
    recentReports: DamageReportResponse[];
}

type DashboardResponseRoomStatusResponse = {
    id: number;
    roomNumber: string;
    status: RoomStatus;
    roomClassName: string;
    hasTaskToday: boolean;
    statusDisplay: string;
    statusColor: string;
}

type ErrorResponse = {
    code: string;
    message: string;
    status: number;
    path: string;
    timestamp: DateAsString;
}

type FolioItemResponse = {
    id: number;
    type: string;
    description: string;
    quantity: number;
    totalPrice: number;
    status: string;
}

type HousekeepingTaskResponse = {
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

type HousekeepingTaskResponseHousekeepingTaskResponseBuilder = {
}

type HousekeepingTaskResponses = {
    id: number;
    roomNumber: string;
    taskType: string;
    status: string;
    assignedAt: DateAsString;
    completedAt: DateAsString;
    roomStatus: RoomStatus;
    assigneeName: string;
    taskTypeDisplay: string;
    roomStatusColor: string;
    statusDisplay: string;
    statusColor: string;
    roomStatusDisplay: string;
    formattedCompletedAt: string;
    formattedAssignedAt: string;
}

type LoginResponse = {
    token: string;
    expiresIn: number;
}

type LoginResponseLoginResponseBuilder = {
}

type MinibarConsumptionResponse = {
    id: number;
    roomNumber: string;
    assetName: string;
    quantity: number;
    price: number;
    total: number;
    status: string;
    createdAt: DateAsString;
    formattedTotal: string;
}

type MinibarConsumptionResponseMinibarConsumptionResponseBuilder = {
}

type MinibarItemResponse = {
    id: number;
    assetName: string;
    categoryName: string;
    currentQuantity: number;
    price: number;
    status: string;
    priceDisplay: string;
}

type MinibarItemResponseMinibarItemResponseBuilder = {
}

type PaymentResponse = {
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

type PerformanceReportResponse = {
    staffName: string;
    periodStart: DateAsString;
    periodEnd: DateAsString;
    totalTasks: number;
    completedTasks: number;
    completionRate: number;
    avgTimePerTask: number;
    minibarRevenue: number;
    damageReports: number;
    damagePenalty: number;
    avgTimeDisplay: string;
    completionRateDisplay: string;
    minibarRevenueDisplay: string;
    damagePenaltyDisplay: string;
}

type PerformanceReportResponsePerformanceReportResponseBuilder = {
}

type RatingResponse = {
    id: number;
    name: string;
    avatar: string;
    rating: number;
    date: string;
    comment: string;
}

type RatingSummaryResponse = {
    averageRating: number;
    totalReviews: number;
    ratingDistribution: { [index: string]: number };
    content: RatingResponse[];
}

type RegisterResponse = {
    id: number;
    email: string;
    role: string;
    fullName: string;
    phoneNumber: string;
    identityCard: string;
    provider: string;
    customerId: number;
    message: string;
}

type RegisterResponseRegisterResponseBuilder = {
}

type ReservationResponse = {
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

type ReservationRoomCheckOutResponse = {
    reservationRoomId: number;
    status: string;
    message: string;
}

type ReservationRoomFolioResponse = {
    reservationRoomId: number;
    roomNumber: string;
    roomClassName: string;
    occupants: RoomOccupantResponse[];
    folioItems: FolioItemResponse[];
    totalCharges: number;
    totalPaid: number;
    balance: number;
}

type RoomAssetResponse = {
    id: number;
    roomNumber: string;
    assetName: string;
    categoryName: string;
    currentQuantity: number;
    initialQuantity: number;
    price: number;
    status: string;
    priceDisplay: string;
    statusDisplay: string;
    consumedQuantity: number;
}

type RoomClassAvailabilityResponse = {
    roomClass: RoomClassResponse;
    availableRooms: number;
}

type RoomClassAvailableRoomsResponse = {
    roomClass: RoomClassResponse;
    availableRooms: AvailableRoomResponse[];
}

type RoomClassDetailResponse = {
    id: number;
    name: string;
    standardCapacity: number;
    maxCapacity: number;
    basePrice: number;
    extraPersonFee: number;
    totalRooms: number;
    images: RoomImgResponse[];
    assets: AssetResponse[];
    averageRating: number;
}

type RoomClassDetailResponseRoomClassDetailResponseBuilder = {
}

type RoomClassQuantityResponse = {
    id: number;
    roomClassId: number;
    numberOfPeople: number;
}

type RoomClassResponse = {
    id: number;
    name: string;
    basePrice: number;
    standardCapacity: number;
    maxCapacity: number;
    extraPersonFee: number;
    primaryImage: RoomImgResponse;
    totalRooms: number;
    averageRating: number;
}

type RoomClassResponseRoomClassResponseBuilder = {
}

type RoomImgResponse = {
    id: number;
    dataUrl: string;
    imgType: string;
    isPrimary: boolean;
}

type RoomImgResponseRoomImgResponseBuilder = {
}

type RoomMatrixResponse = {
    id: number;
    roomNumber: string;
    status: string;
    roomClassName: string;
}

type RoomMatrixResponseRoomMatrixResponseBuilder = {
}

type RoomOccupantResponse = {
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

type ScheduleResponse = {
    id: number;
    date: DateAsString;
    shiftName: string;
    startTime: DateAsString;
    endTime: DateAsString;
    status: string;
    totalTasks: number;
    completedTasks: number;
    statusDisplay: string;
}

type ScheduleResponseScheduleResponseBuilder = {
}

type ServiceBookingResponse = {
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

type ServiceResponse = {
    id: number;
    name: string;
    serviceCategory: ServiceCategory;
    price: number;
    isActive: boolean;
}

type ServiceResponseServiceResponseBuilder = {
}

type ShiftResponse = {
    id: number;
    shiftName: string;
    startTime: DateAsString;
    endTime: DateAsString;
    isActive: boolean;
}

type ShiftResponseShiftResponseBuilder = {
}

type StaffResponse = {
    id: number;
    fullName: string;
    department: Department;
    status: string;
}

type StaffResponseStaffResponseBuilder = {
}

type StaffResponseDTO = {
    id: number;
    fullName: string;
    phoneNumber: string;
    department: string;
    status: string;
    isActive: boolean;
    userId: number;
    email: string;
}

type StaffResponseDTOStaffResponseDTOBuilder = {
}

type TaskCountResponse = {
    scheduled: number;
    inProgress: number;
    completed: number;
    total: number;
    completionRate: number;
}

type TaskCountResponseTaskCountResponseBuilder = {
}

type TaskResponse = {
    id: number;
    roomNumber: string;
    taskType: string;
    taskTypeDisplay: string;
    status: string;
    statusDisplay: string;
    statusColor: string;
    assignedAt: DateAsString;
    completedAt: DateAsString;
    roomStatus: string;
    roomStatusDisplay: string;
    roomStatusColor: string;
    assigneeName: string;
    formattedAssignedAt: string;
    formattedCompletedAt: string;
}

type TaskResponseTaskResponseBuilder = {
}

type UserResponseDTO = {
    id: number;
    fullName: string;
    phoneNumber: string;
    identityCard: string;
    email: string;
    role: Role;
    provider: string;
    isActive: boolean;
    reservations: ReservationResponse[];
    customerId: number;
}

type UserResponseDTOUserResponseDTOBuilder = {
}

type WorkScheduleResponse = {
    id: number;
    staffId: number;
    staffName: string;
    departmentName: string;
    shiftId: number;
    shiftName: string;
    workDate: DateAsString;
    shiftStart: DateAsString;
    shiftEnd: DateAsString;
    status: string;
}

type WorkScheduleResponseWorkScheduleResponseBuilder = {
}

type Serializable = {
}

type DateAsString = string;

type AuthProvider = "LOCAL" | "GOOGLE";

type CustomerType = "REGULAR" | "ADULT" | "CHILD" | "VIP" | "CORPORATE";

type DamageReportStatus = "OPEN" | "RESOLVED" | "CANCELLED";

type Department = "RECEPTIONIST" | "HOUSEKEEPING";

type FolioItemStatus = "UNPAID" | "PAID" | "VOID";

type FolioItemType = "ROOM_CHARGE" | "SERVICE_CHARGE" | "EARLY_CHECKIN_FEE" | "LATE_CHECKOUT_FEE" | "DAMAGE_PENALTY" | "ADJUSTMENT" | "MINIBAR_CHARGE" | "DISCOUNT" | "REFUND";

type FolioStatus = "OPEN" | "LOCKED" | "CLOSED" | "SETTLED";

type HousekeepingTaskStatus = "SCHEDULED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED";

type HousekeepingTaskType = "CLEANING" | "INSPECTION" | "MAINTENANCE_SUPPORT";

type ImageType = "THUMBNAIL" | "GALLERY" | "FLOORPLAN";

type OccupantRole = "PRIMARY" | "GUEST" | "CHILD";

type PaymentMethod = "CASH" | "CARD" | "BANK_TRANSFER" | "QR" | "VNPAY";

type PaymentTransactionStatus = "PENDING" | "SUCCESS" | "FAILED" | "CANCELLED";

type PaymentTransactionType = "DEPOSIT" | "PAYMENT" | "REFUND" | "ADJUSTMENT";

type RefundRequestStatus = "PENDING" | "APPROVED" | "REJECTED" | "FAILED";

type ReservationRoomStatus = "PENDING" | "ASSIGNED" | "CHECKED_IN" | "CHECKED_OUT" | "CANCELLED";

type ReservationStatus = "PENDING_DEPOSIT" | "CONFIRMED" | "CANCELLED" | "IN_HOUSE" | "CHECKED_OUT" | "FINISHED";

type Role = "ADMIN" | "STAFF" | "CUSTOMER";

type RoomAssetStatus = "GOOD" | "DAMAGED";

type RoomChangeType = "CUSTOMER_REQUEST" | "ROOM_ISSUE";

type RoomStatus = "AVAILABLE" | "RESERVED" | "CLEAN" | "DIRTY" | "OCCUPIED" | "MAINTENANCE";

type ServiceBookingStatus = "PENDING" | "CONFIRMED" | "IN_PROGRESS" | "FINISHED" | "CANCELLED";

type ServiceCategory = "SPA" | "MINIBAR" | "F_AND_B";

type StaffStatus = "ACTIVE" | "INACTIVE" | "SUSPENDED";

type WorkScheduleStatus = "SCHEDULED" | "ON_LEAVE" | "COMPLETED";
