package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ShiftNoteRepository extends JpaRepository<ShiftNotes,String> {

    @Query(value = "SELECT r.noteid FROM shift_notes r ORDER BY CAST(SUBSTRING(r.noteid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfShiftNotes();

    @Query(value = "SELECT r from ShiftNotes r WHERE r.recurrentAppointment=:recurrentAppointment AND r.careGiver=:careGiver")
    ShiftNotes getShiftNoteByRecurrentAppointmentAndCareGiver(RecurrentAppointment recurrentAppointment, CareGiver careGiver);

    @Query("SELECT r from ShiftNotes r ORDER BY shiftEndDate DESC")
    List<ShiftNotes> getAllShiftNotes();

    @Query("SELECT r from ShiftNotes r WHERE r.recurrentAppointment=:recurrentAppointment AND r.employee=:employee")
    ShiftNotes getShiftNotesByRecurrentAppointmentAndEmployee(RecurrentAppointment recurrentAppointment,Employee employee);

    @Query("SELECT r from ShiftNotes r WHERE r.employee=:employee ORDER BY shiftEndDate DESC")
    List<ShiftNotes> getAllShiftNotesByEmployee(Employee employee);

    @Query("SELECT r from ShiftNotes r WHERE r.careGiver=:careGiver ORDER BY shiftEndDate DESC")
    List<ShiftNotes> getAllShiftNotesByCareGiver(CareGiver careGiver);

    @Query("SELECT COUNT(r) FROM ShiftNotes r WHERE r.state = 'Completed' AND r.employee = :employee")
    long countByStateCompletedAndEmployee(@Param("employee") Employee employee);

    @Query("SELECT r FROM ShiftNotes r " +
            "WHERE r.employee = :createdById " +
            "AND r.shiftStartDate >= :startDate " +
            "AND r.shiftEndDate <= :endDate " +
            "AND r.state = 'Completed'")
    List<ShiftNotes> getShiftNotesByCreatorAndDateRangeAndState(
             Employee createdById,
             LocalDate startDate,
             LocalDate endDate
    );

    @Query("SELECT s FROM ShiftNotes s " +
            "WHERE s.shiftStartDate >= :startDate AND s.shiftEndDate <= :endDate")
    List<ShiftNotes> findShiftNotesBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT DISTINCT r.employee.employeeID " +
            "FROM ShiftNotes r " +
            "WHERE r.shiftStartDate >= :startDate " +
            "AND r.shiftEndDate <= :endDate " +
            "AND r.state = 'Completed'")
    List<String> getDistinctCreatedByIdsByDateRangeAndState(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<ShiftNotes> findByClientAndPaymentState(Client client, String paymentState);

    @Query("SELECT COUNT(s) FROM ShiftNotes s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM ShiftNotes s " +
            "WHERE s.paymentState = :paymentState " +
            "AND s.createdAt BETWEEN :startDate AND :endDate")
    long countByPaymentStateAndCreatedAtBetween(@Param("paymentState") String paymentState,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);


}
