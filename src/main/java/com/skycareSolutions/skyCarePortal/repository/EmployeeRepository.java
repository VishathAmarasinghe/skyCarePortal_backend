package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<Employee,String> {

    @Query("SELECT e FROM Employee e ORDER BY e.joinDate DESC")
    List<Employee> getAllEmployees();

    @Query("SELECT e FROM Employee e where accessRole=:role ORDER BY e.joinDate DESC")
    List<Employee> getEmployeesByRole(String role);

    @Query(value = "SELECT e.employeeID FROM Employee e ORDER BY CAST(SUBSTRING(e.employeeID, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfEmployees();
}
