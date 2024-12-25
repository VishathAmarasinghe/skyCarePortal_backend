package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CareGiver;
import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.RecurrentAppointment;
import com.skycareSolutions.skyCarePortal.model.ShiftNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShiftNoteRepository extends JpaRepository<ShiftNotes,String> {

    @Query(value = "SELECT r.noteid FROM shift_notes r ORDER BY CAST(SUBSTRING(r.noteid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfShiftNotes();

    @Query(value = "SELECT r from ShiftNotes r WHERE r.recurrentAppointment=:recurrentAppointment AND r.careGiver=:careGiver")
    ShiftNotes getShiftNoteByRecurrentAppointmentAndCareGiver(RecurrentAppointment recurrentAppointment, CareGiver careGiver);

    @Query("SELECT r from ShiftNotes r ORDER BY shiftEndDate DESC")
    List<ShiftNotes> getAllShiftNotes();

    @Query("SELECT r from ShiftNotes r WHERE r.careGiver=:careGiver ORDER BY shiftEndDate DESC")
    List<ShiftNotes> getAllShiftNotesByCareGiver(CareGiver careGiver);

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


    @Query("SELECT DISTINCT r.employee.employeeID " +
            "FROM ShiftNotes r " +
            "WHERE r.shiftStartDate >= :startDate " +
            "AND r.shiftEndDate <= :endDate " +
            "AND r.state = 'Completed'")
    List<String> getDistinctCreatedByIdsByDateRangeAndState(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
