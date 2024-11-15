package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.EmployeeJobRolesId;
import com.skycareSolutions.skyCarePortal.model.Employee_job_Roles;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeJobRolesRepository extends CrudRepository<Employee_job_Roles, EmployeeJobRolesId> {

}
