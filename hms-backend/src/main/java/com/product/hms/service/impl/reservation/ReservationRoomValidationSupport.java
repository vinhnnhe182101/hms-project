package com.product.hms.service.impl.reservation;


import com.product.hms.entity.*;
import com.product.hms.enums.*;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.repository.ReservationRoomRepository;
import com.product.hms.repository.ServiceBookingRepository;

import java.math.BigDecimal;

/**
 * Hỗ trợ kiểm tra hợp lệ (validate) các điều kiện liên quan đến ReservationRoom để tránh lặp code và dễ tái sử dụng.
 */
public class ReservationRoomValidationSupport {

	/**
	 * Kiểm tra reservation và reservationRoom có đủ điều kiện để đổi phòng (reservation phải IN_HOUSE, reservationRoom phải CHECKED_IN).
	 *
	 * @param reservation Đặt phòng cần kiểm tra
	 * @param reservationRoom Phòng cần kiểm tra
	 * @throws BusinessException Nếu không hợp lệ
	 */
	public static void validateReservationRoomForChange(ReservationEntity reservation, ReservationRoomEntity reservationRoom) {
		if (reservation.getStatus() != ReservationStatus.IN_HOUSE) {
			throw new BusinessException(
					ErrorCode.ROOM_CHANGE_NOT_ALLOWED,
					"Room change only allowed when reservation status is IN_HOUSE. Current: " + reservation.getStatus()
			);
		}
		if (reservationRoom.getStatus() != ReservationRoomStatus.CHECKED_IN) {
			throw new BusinessException(
					ErrorCode.ROOM_CHANGE_NOT_ALLOWED,
					"Room change only allowed when room status is CHECKED_IN. Current: " + reservationRoom.getStatus()
			);
		}
	}

	/**
	 * Kiểm tra phòng mới có hợp lệ để đổi (phải ACTIVE, AVAILABLE, chưa được gán trong reservation này).
	 *
	 * @param newRoom Phòng mới
	 * @param reservation Đặt phòng hiện tại
	 * @param reservationRoomRepository Repository để kiểm tra phòng đã gán chưa
	 * @throws BusinessException Nếu không hợp lệ
	 */
	public static void validateNewRoomForChange(RoomEntity newRoom, ReservationEntity reservation, ReservationRoomRepository reservationRoomRepository) {
		if (!Boolean.TRUE.equals(newRoom.getIsActive())) {
			throw new BusinessException(
					ErrorCode.ROOM_NOT_AVAILABLE,
					"New room must be active. Room ID: " + newRoom.getId()
			);
		}
		if (newRoom.getStatus() != RoomStatus.AVAILABLE) {
			throw new BusinessException(
					ErrorCode.ROOM_NOT_AVAILABLE,
					"New room must be AVAILABLE. Current: " + newRoom.getStatus()
			);
		}
		boolean isAssigned = reservationRoomRepository.existsByRoomEntityAndReservationEntityAndIsActiveTrue(newRoom, reservation);
		if (isAssigned) {
			throw new BusinessException(
					ErrorCode.ROOM_ALREADY_ASSIGNED,
					"Room already assigned in this reservation. Room ID: " + newRoom.getId()
			);
		}
	}

	/**
	 * Kiểm tra reservation phải IN_HOUSE mới được check-out.
	 *
	 * @param reservation Đặt phòng cần kiểm tra
	 * @throws BusinessException Nếu không hợp lệ
	 */
	public static void validateReservationInHouseForCheckOut(ReservationEntity reservation) {
		if (reservation.getStatus() != ReservationStatus.IN_HOUSE) {
			throw new BusinessException(
					ErrorCode.RESERVATION_CHECKOUT_NOT_ALLOWED,
					"Check-out only allowed when reservation status is IN_HOUSE. Current: " + reservation.getStatus()
			);
		}
	}

	/**
	 * Kiểm tra reservationRoom phải CHECKED_IN mới được check-out.
	 *
	 * @param reservationRoom Phòng cần kiểm tra
	 * @throws BusinessException Nếu không hợp lệ
	 */
	public static void validateReservationRoomCheckedInForCheckOut(ReservationRoomEntity reservationRoom) {
		if (reservationRoom.getStatus() != ReservationRoomStatus.CHECKED_IN) {
			throw new BusinessException(
					ErrorCode.RESERVATION_ROOM_NOT_CHECKED_IN,
					"Room must be in CHECKED_IN status to check out. Room ID: " + reservationRoom.getId()
			);
		}
	}

	/**
	 * Kiểm tra reservationRoom không có dịch vụ đang chờ xử lý trước khi check-out.
	 *
	 * @param reservationRoom Phòng cần kiểm tra
	 * @param serviceBookingRepository Repository dịch vụ phòng
	 * @throws BusinessException Nếu còn dịch vụ đang chờ
	 */
	public static void validateNoPendingServicesForCheckOut(ReservationRoomEntity reservationRoom, ServiceBookingRepository serviceBookingRepository) {
		boolean hasPendingServices = serviceBookingRepository.existsByReservationRoomEntityAndStatus(
				reservationRoom,
				ServiceBookingStatus.PENDING
		);
		if (hasPendingServices) {
			throw new BusinessException(
					ErrorCode.RESERVATION_ROOM_HAS_PENDING_SERVICES,
					"Cannot check out room with pending services. Room ID: " + reservationRoom.getId()
			);
		}
	}

	/**
	 * Kiểm tra folio chưa bị đóng (CLOSED) trước khi thanh toán.
	 *
	 * @param folio Folio cần kiểm tra
	 * @throws BusinessException Nếu folio đã đóng
	 */
	public static void validateFolioNotClosed(FolioEntity folio) {
		if (folio.getStatus() == FolioStatus.CLOSED) {
			throw new BusinessException(
					ErrorCode.FOLIO_ALREADY_CLOSED,
					"Cannot process payment for closed folio"
			);
		}
	}

	/**
	 * Kiểm tra số tiền đặt cọc hợp lệ (không âm, không vượt quá số dư hiện có).
	 *
	 * @param depositRequested Số tiền yêu cầu rút
	 * @param depositAvailable Số dư đặt cọc hiện có
	 * @throws BusinessException Nếu không hợp lệ
	 */
	public static void validateDepositAmount(BigDecimal depositRequested, BigDecimal depositAvailable) {
		if (depositRequested.signum() < 0) {
			throw new BusinessException(ErrorCode.INVALID_REQUEST, "Deposit amount must be >= 0");
		}
		if (depositRequested.compareTo(depositAvailable) > 0) {
			throw new BusinessException(
					ErrorCode.INSUFFICIENT_DEPOSIT,
					String.format("Insufficient deposit. Available: %s, Requested: %s", depositAvailable, depositRequested)
			);
		}
	}
}
