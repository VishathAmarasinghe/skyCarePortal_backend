package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.EmployeePhoneNoId;
import com.skycareSolutions.skyCarePortal.model.Employee_address;
import com.skycareSolutions.skyCarePortal.model.Employee_addressId;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeAddressRepository extends CrudRepository<Employee_address, Employee_addressId> {
}
