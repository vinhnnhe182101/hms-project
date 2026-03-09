# Business Rules

## Mục lục

- [BR-01 — Booking Confirmation Conditions](#br-01--booking-confirmation-conditions)
- [BR-02 — Deposit value regulations](#br-02--deposit-value-regulations)
- [BR-03 — Deposit payment term](#br-03--deposit-payment-term)
- [BR-04 — Capacity Control & Surcharge](#br-04--capacity-control--surcharge)
- [BR-05 — Deposit Refund Policy](#br-05--deposit-refund-policy)
- [BR-06 — Identification Requirements](#br-06--identification-requirements)
- [BR-07 — Early Check-in Policy](#br-07--early-check-in-policy)
- [BR-08 — Room change calculation formula](#br-08--room-change-calculation-formula)
- [BR-09 — Late Check-out Policy](#br-09--late-check-out-policy)
- [BR-10 — Final Settlement Formula](#br-10--final-settlement-formula)
- [BR-11 — Booking Adjustment Policy](#br-11--booking-adjustment-policy)
- [BR-12 — Deadline for closing booking information](#br-12--deadline-for-closing-booking-information)
- [BR-13 — Regulations on Modification/Cancellation of Service Orders](#br-13--regulations-on-modificationcancellation-of-service-orders)
- [BR-14 — Booking Completion Rules](#br-14--booking-completion-rules)

---

## BR-01 — Booking Confirmation Conditions

**Summary:** Booking Confirmation Conditions  
**Detail:**

- A Booking can only be changed from `PENDING` to `CONFIRMED` **after** the system records that the customer has paid
  the **full deposit** as required.

---

## BR-02 — Deposit value regulations

**Summary:** Deposit value regulations  
**Detail:**

- Guests are required to pay a deposit equal to **20%** of the total value of the booking to reserve the seat.

---

## BR-03 — Deposit payment term

**Summary:** Deposit payment term  
**Detail:**

- If the customer does not complete the deposit payment within **2 hours** from the time of creating the order (*
  *Created Time**), the system will automatically cancel the order and change the status to `CANCELLED`.

---

## BR-04 — Capacity Control & Surcharge

**Summary:** Capacity Control & Surcharge  
**Detail:**  
Each room type has **Standard** and **Maximum Occupancy** regulations:

1. The system blocks data entry if the number of guests exceeds the **Maximum Capacity**.
2. If the number of guests is greater than **Standard Occupancy** (but `<= Maximum`), the system will automatically add
   the **Extra Person Fee** to the total room rate.

---

## BR-05 — Deposit Refund Policy

**Summary:** Deposit Refund Policy  
**Detail:**

- Customers are only entitled to a refund of the deposit when canceling the reservation **at least 1 day before** the
  specified check-in time.

---

## BR-06 — Identification Requirements

**Summary:** Identification Requirements  
**Detail:**

- At the time of check-in, the Front Desk is required to enter the **ID card or passport number** of at least **1 guest
  ** representing that room **before** the system activates the **"Check-in"** function button.

---

## BR-07 — Early Check-in Policy

**Summary:** Early Check-in  
**Detail:**  
Standard check-in time is **14:00**. Early check-in surcharge:

- Before **05:00**: **100%** of the room rate (considered as an extra **1 night**).
- From **05:00 – 09:00**: **50%** of the room rate.
- From **09:00 – 14:00**: **Free** (subject to availability).

---

## BR-08 — Room change calculation formula

**Summary:** Room change calculation formula  
**Detail:**  
When a customer requests to change to another room type, the system calculates the difference to be paid (or refunded)
according to the formula:

- **Difference = (New Room Rate - Old Room Rate) × Days Remaining**

---

## BR-09 — Late Check-out Policy

**Summary:** Late Check-out  
**Detail:**  
Standard check-out time is **12:00**. Late payment surcharge:

- From **12:00 – 15:00**: **30%** of the room rate will be charged.
- From **15:00 – 18:00**: **50%** of the room rate will be charged.
- After **18:00**: **100%** of the room rate (considered as an extra **1 night**).

---

## BR-10 — Final Settlement Formula

**Summary:** Final Settlement Formula  
**Detail:**  
The total amount to be paid upon check-out is calculated as follows:

- **Total amount = (Room charge + Service charge + Damage/late surcharge) − Paid deposit**

If the result is negative:

- The system will generate an **excess refund request** for the guest.

---

## BR-11 — Booking Adjustment Policy

**Summary:** Booking Adjustment Policy  
**Detail:**  
When modifying a booking (before check-in) resulting in a change in the total value:

1. **Spread Calculation System = Total New Funds − Total Funds Closed**
2. If the **Difference > 0**: The Guest must pay this amount **immediately** to complete the amendment.
3. If the **Spread < 0**: The system creates an **excess cashback transaction** immediately.
4. If the **Spread = 0**: Update the information, **no trades are incurred**.

---

## BR-12 — Deadline for closing booking information

**Summary:** Deadline for closing booking information  
**Detail:**  
The system only allows changes to Room information (Check-in/out date, Room type, Quantity) **24 hours before** the
standard check-in time (**14:00 on the day of arrival**).

After this deadline (within 24 hours before arrival):

- **Lock:** Change of Date, Room Type, Number is **not allowed**.
- **Open:** Only change of Customer Information (Name, Phone, CCCD, Notes) is allowed.

---

## BR-13 — Regulations on Modification/Cancellation of Service Orders

**Summary:** Regulations on Modification/Cancellation of Service Orders  
**Detail:**

1. It is only allowed to **Amend** (Quantity, Notes) or **Cancel** a service order when it is in `PENDING` status (
   Pending/Preparation).
2. The system locks absolutely (**no modification/cancellation allowed**) when the service order has changed to the
   `FINISHED` status (Completed/Handed over to the customer).
3. Customers are required to pay **100%** of the value of this service to the **Folio Invoice** at check-out.

---

## BR-14 — Booking Completion Rules

**Summary:** Booking Completion Rules  
**Detail:**  
The check-out lifecycle is divided into 2 mandatory stages:

1. **CHECKED-OUT**
    - Occurs when the guest hands over the room and closes the debt.
    - The system immediately releases the room (switches to the `DIRTY` state for the Chamber to be cleaned).
    - The system **freezes (locks)** the Folio Bill, not incurring additional charges.

2. **FINISHED**
    - The final state of a Reservation.
    - Bookings can only be converted to `FINISHED` when and only when the system records that the customer has
      successfully paid **100%** of the debt on Folio (**Balance = 0**).