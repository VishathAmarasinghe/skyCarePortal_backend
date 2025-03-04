package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.*;
import com.skycareSolutions.skyCarePortal.utils.AuthenticationUtils;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class EmployeeService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "profilePhotos").toString();

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeePhoneNoRepository employeePhoneNoRepository;
    @Autowired
    private EmployeeJobRolesRepository employeeJobRolesRepository;
    @Autowired
    private EmployeeAddressRepository employeeAddressRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private JobAssignerService jobAssignerService;

    @Autowired
    private CareGiverService careGiverService;

    @Lazy
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ClientRepository clientRepository;

    AuthenticationUtils authenticationUtils= new AuthenticationUtils();

    @Value("${app.external.service.frontendUrl}")
    private String frontendUrl;

    AuthenticationUtils passwordUtil = new AuthenticationUtils();

    public EmployeeDTO loginUser(String email, String password) {
        Optional<Employee> userOptional = employeeRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Employee user = userOptional.get();
            if (passwordUtil.matchPassword(password, user.getPassword())) {
                return convertToDTO(user);
            }
        }else {
            Client clientOptional = clientRepository.findByEmail(email);
            if (clientOptional!=null){
                if (passwordUtil.matchPassword(password, clientOptional.getPassword())) {
                    EmployeeDTO employeeDTO = new EmployeeDTO();
                    employeeDTO.setEmployeeID(clientOptional.getClientID());
                    employeeDTO.setStatus(clientOptional.getStatus());
                    List<String> roles = new ArrayList<>();
                    roles.add("Client");
                    employeeDTO.setEmployeeJobRoles(roles);
                    employeeDTO.setEmail(clientOptional.getEmail());
                    employeeDTO.setAccessRole("Client");
                    return employeeDTO;
                }
            }
        }
        throw new RuntimeException("Invalid email or password");
    }

    private List<Employee_phoneNo> mapPhoneNumbersFromDTO(String employeeID, List<String> phoneNumbers) {
        List<Employee_phoneNo> empPhoneNoList = new ArrayList<>();
        for (String phone : phoneNumbers) {
            EmployeePhoneNoId phoneId = new EmployeePhoneNoId(employeeID, phone);
            empPhoneNoList.add(new Employee_phoneNo(phoneId, null));
        }
        return empPhoneNoList;
    }

    private List<Employee_job_Roles> mapJobRolesFromDTO(String employeeID, List<String> jobRoles) {
        List<Employee_job_Roles> empJobRolesList = new ArrayList<>();
        for (String role : jobRoles) {
            EmployeeJobRolesId jobRoleId = new EmployeeJobRolesId(employeeID, role);
            empJobRolesList.add(new Employee_job_Roles(jobRoleId, null));
        }
        return empJobRolesList;
    }

    private List<Employee_address> mapAddressesFromDTO(String employeeID, List<Employee_address_DTO> addresses) {
        List<Employee_address> empAddressesList = new ArrayList<>();
        for (Employee_address_DTO addressDTO : addresses) {
            Employee_addressId addressId = new Employee_addressId(employeeID, addressDTO.getLongitude(), addressDTO.getLatitude());
            empAddressesList.add(new Employee_address(addressId, null, addressDTO.getCity(), addressDTO.getAddress(), addressDTO.getState(), addressDTO.getCountry(), addressDTO.getPostal_code()));
        }
        return empAddressesList;
    }

    private Employee mapEmployeeFromDTO(EmployeeDTO employeeDTO){
      Employee employee = new Employee();
      employee.setEmployeeID(employeeDTO.getEmployeeID());
      employee.setFirstName(employeeDTO.getFirstName());
      employee.setLastName(employeeDTO.getLastName());
      employee.setPassword(employeeDTO.getPassword());
      employee.setEmail(employeeDTO.getEmail());
      employee.setAccessRole(employeeDTO.getAccessRole());
      employee.setJoinDate(employeeDTO.getJoinDate());
      employee.setEmergencyPhoneNo(employeeDTO.getEmergencyPhoneNo());
      employee.setEmergencyUser(employeeDTO.getEmergencyUser());
      employee.setProfile_photo(employeeDTO.getProfile_photo());
      employee.setStatus(employeeDTO.getStatus());
      List<Employee_phoneNo> empPhoneNo = new ArrayList<>();
      for (String phoe:employeeDTO.getEmployeePhoneNo()){
          EmployeePhoneNoId phoneId = new EmployeePhoneNoId(employee.getEmployeeID(),phoe);
          Optional<Employee_phoneNo> phone = employeePhoneNoRepository.findById(phoneId);
          if (phone.isEmpty()) {
              Employee_phoneNo newPhone = new Employee_phoneNo(phoneId,employee);
              empPhoneNo.add(newPhone);
          } else {
              System.out.println("Phone number already exists: " + phoe);
          }
      }
      employee.setEmployeePhoneNo(empPhoneNo);
      List<Employee_job_Roles> empJobRoles = new ArrayList<>();
      for (String roles:employeeDTO.getEmployeeJobRoles()){
            EmployeeJobRolesId role = new EmployeeJobRolesId(employee.getEmployeeID(),roles);
            Optional<Employee_job_Roles> jobRole = employeeJobRolesRepository.findById(role);
            if (jobRole.isEmpty()) {
                Employee_job_Roles newRole = new Employee_job_Roles(role,employee);
                empJobRoles.add(newRole);
            }
      }
      employee.setEmployeeJobRoles(empJobRoles);
      List<Employee_address> empAddress = new ArrayList<>();
      for (Employee_address_DTO address:employeeDTO.getEmployeeAddresses()){

          Employee_addressId addressId = new Employee_addressId(employee.getEmployeeID(),address.getLongitude(),address.getLatitude());
          Optional<Employee_address> adressVlues = employeeAddressRepository.findById(addressId);
          if (adressVlues.isEmpty()) {
              Employee_address newAddress = new Employee_address(addressId,employee,address.getCity(),address.getAddress(),address.getState(),address.getCountry(),address.getPostal_code());
              empAddress.add(newAddress);
          }
      }
      employee.setEmployeeAddresses(empAddress);
      return employee;

    };

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeID(employee.getEmployeeID());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPassword(employee.getPassword());
        dto.setAccessRole(employee.getAccessRole());
        dto.setProfile_photo(employee.getProfile_photo());
        dto.setEmergencyUser(employee.getEmergencyUser());
        dto.setEmergencyPhoneNo(employee.getEmergencyPhoneNo());
        dto.setJoinDate(employee.getJoinDate());
        dto.setStatus(employee.getStatus());

        // Convert phone numbers to simple list of strings
        List<String> phoneNumbers = new ArrayList<>();
        for (Employee_phoneNo phoneNo : employee.getEmployeePhoneNo()) {
            phoneNumbers.add(phoneNo.getId().getPhoneNo());
        }
        dto.setEmployeePhoneNo(phoneNumbers);

        // Convert job roles to simple list of strings
        List<String> jobRoles = new ArrayList<>();
        for (Employee_job_Roles jobRole : employee.getEmployeeJobRoles()) {
            jobRoles.add(jobRole.getId().getJobRole());
        }
        dto.setEmployeeJobRoles(jobRoles);

        // Convert addresses to a simplified DTO format
        List<Employee_address_DTO> addressDTOs = new ArrayList<>();
        for (Employee_address address : employee.getEmployeeAddresses()) {
            Employee_address_DTO addressDTO = new Employee_address_DTO();
            addressDTO.setLongitude(address.getId().getLongitude());
            addressDTO.setLatitude(address.getId().getLatitude());
            addressDTO.setAddress(address.getAddress());
            addressDTO.setCity(address.getCity());
            addressDTO.setState(address.getState());
            addressDTO.setCountry(address.getCountry());
            addressDTO.setPostal_code(address.getPostal_code());
            addressDTOs.add(addressDTO);
        }
        dto.setEmployeeAddresses(addressDTOs);

        return dto;
    }

    /**
     * This method is used for get All employees
     * @return List of employees
     */
    public List<EmployeeDTO> findAllEmployees(){
        List<Employee> employees;
        employees = employeeRepository.getAllEmployees();

        System.out.println("employees "+employees.toString());

        List<EmployeeDTO> empResult = new ArrayList<>();
        for (Employee emp:employees){
            System.out.println("ee "+emp.getEmail());
            empResult.add(convertToDTO(emp));
        }

        return empResult;
    }


    public List<EmployeeDTO> findCareGiversAsEmployeesAssignedWithClients(String clientID){
        List<EmployeeDTO> employeeList = new ArrayList<>();
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsbyClientID(clientID);
        Set<String> careGiverList = new HashSet<>();
        for (AppointmentDTO singleAppointment:appointments){
            List<String> careGivers = jobAssignerService.getAllCareGiverIDsAssignToAppointment(singleAppointment.getAppointmentID());
            careGiverList.addAll(careGivers);
        }
        for (String careGiverID: careGiverList){
            CareGiver careGiver = careGiverService.getCareGiverByIdWithEntity(careGiverID);
            employeeList.add(convertToDTO(careGiver.getEmployee()));
        }
        return employeeList;
    }

    public EmployeeDTO getSingleEmployee(String employeeID){
        Optional<Employee> employee = employeeRepository.findById(employeeID);
        if (employee.isPresent()){
            return convertToDTO(employee.get());
        }else{
            throw new RuntimeException();
        }
    }

    public Employee saveNewEmployee(EmployeeDTO employee, MultipartFile profilePhoto){

        String lastEmployeeID = employeeRepository.getLastIdOfEmployees();
        String newEmployeeID = Micro_utils.idGenerator("employee",lastEmployeeID);
        employee.setEmployeeID(newEmployeeID);

        if (profilePhoto!=null && !profilePhoto.isEmpty()) {
            try {

                String originalFileName = profilePhoto.getOriginalFilename();
                String fileExtension = originalFileName;
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                System.out.println("original file name " + originalFileName);
                File directory = new File(DIRECTORY);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File destinationFile = new File(DIRECTORY, newEmployeeID + fileExtension);
                profilePhoto.transferTo(destinationFile);
                employee.setProfile_photo(destinationFile.getAbsolutePath());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (employee.getStatus().equalsIgnoreCase("Activated")){
            String password = Micro_utils.generateRandomPassword(10);
            employee.setPassword(authenticationUtils.hashPassword(password));

            CompletableFuture.runAsync(()->emailService.generateWelcomeLoginEmail(employee.getFirstName(),employee.getEmail(),password,frontendUrl));
        }
        return employeeRepository.save(mapEmployeeFromDTO(employee));
    }

    public String updatePasswords(PasswordChangeDTO passwordChangeDTO){
        Optional<Employee> employeeDTO = employeeRepository.findById(passwordChangeDTO.getEmployeeID());
        if (employeeDTO.isPresent()){
            Employee exisitingEmployee = employeeDTO.get();
            if (passwordChangeDTO.getCurrentPassword()!=null && passwordChangeDTO.getCurrentPassword()!="") {
                if (!passwordUtil.matchPassword(passwordChangeDTO.getCurrentPassword(), exisitingEmployee.getPassword())) {
                    throw new RuntimeException("Old Password is incorrect");
                }

                String newPassword = passwordUtil.hashPassword(passwordChangeDTO.getNewPassword());
                exisitingEmployee.setPassword(newPassword);
                employeeRepository.save(exisitingEmployee);
            }
        }

        return "Password Changed Successfully";
    }

    public String updateEmployeeBasicInfoWithPanel(EmployeeBasicInfoUpdaterDTO employeeBasicInfoUpdaterDTO,MultipartFile profilePhoto){
        if (employeeBasicInfoUpdaterDTO.getEmployeeID().substring(0,2).equalsIgnoreCase("CU")){
            Optional<Client> client = clientRepository.findById(employeeBasicInfoUpdaterDTO.getEmployeeID());
            if (client.isPresent()){
                Client exisitingClient = client.get();
                if (!exisitingClient.getEmail().equalsIgnoreCase(employeeBasicInfoUpdaterDTO.getEmail())){
                    EmployeeDTO checkEmployee = findEmployeeByEmail(employeeBasicInfoUpdaterDTO.getEmail());
                    Client clientDTO = clientRepository.findByEmail(employeeBasicInfoUpdaterDTO.getEmail());
                    if (clientDTO!=null || checkEmployee!=null){
                        return "User exist with new email";
                    }
                }
                exisitingClient.setEmail(employeeBasicInfoUpdaterDTO.getEmail());
                exisitingClient.setFirstName(employeeBasicInfoUpdaterDTO.getFirstName());
                exisitingClient.setLastName(employeeBasicInfoUpdaterDTO.getLastName());
                clientRepository.save(exisitingClient);
                return "Account Updated Successfully";

            }else{
                throw new RuntimeException();
            }
        }else{
            EmployeeDTO employeeDTO = getSingleEmployee(employeeBasicInfoUpdaterDTO.getEmployeeID());
            if (!employeeDTO.getEmail().equalsIgnoreCase(employeeBasicInfoUpdaterDTO.getEmail())){
                EmployeeDTO checkEmployee = findEmployeeByEmail(employeeBasicInfoUpdaterDTO.getEmail());
                if (checkEmployee!=null){
                    return "User exist with new email";
                }
            }
            employeeDTO.setFirstName(employeeBasicInfoUpdaterDTO.getFirstName());
            employeeDTO.setLastName(employeeBasicInfoUpdaterDTO.getLastName());
            employeeDTO.setEmail(employeeBasicInfoUpdaterDTO.getEmail());

            updateEmployeeDetails(employeeDTO.getEmployeeID(),employeeDTO,profilePhoto);
            return "Account Updated Successfully";
        }


    }

    public EmployeeDTO updateEmployeeDetails(String employeeID, EmployeeDTO updatedEmployeeDTO, MultipartFile profilePhoto) {
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employeeID);
        if (existingEmployeeOpt.isEmpty()) {
            throw new RuntimeException("Employee with ID " + employeeID + " not found");
        }

        Employee existingEmployee = existingEmployeeOpt.get();
        System.out.println("functionality came here");


        // Update fields if they are provided in updatedEmployeeDTO
        if (updatedEmployeeDTO.getFirstName() != null) {
            System.out.println("employee "+updatedEmployeeDTO.getFirstName());
            existingEmployee.setFirstName(updatedEmployeeDTO.getFirstName());
        }
        System.out.println("updated employee "+updatedEmployeeDTO.getFirstName());
        if (updatedEmployeeDTO.getLastName() != null) {
            existingEmployee.setLastName(updatedEmployeeDTO.getLastName());
        }
        if (updatedEmployeeDTO.getEmail() != null) {
            existingEmployee.setEmail(updatedEmployeeDTO.getEmail());
        }
        if (updatedEmployeeDTO.getAccessRole() != null) {
            existingEmployee.setAccessRole(updatedEmployeeDTO.getAccessRole());
        }
        if (updatedEmployeeDTO.getJoinDate() != null) {
            existingEmployee.setJoinDate(updatedEmployeeDTO.getJoinDate());
        }
        if (updatedEmployeeDTO.getEmergencyPhoneNo()!=null){
            existingEmployee.setEmergencyPhoneNo(updatedEmployeeDTO.getEmergencyPhoneNo());
        }
        if (updatedEmployeeDTO.getEmergencyUser()!=null){
            existingEmployee.setEmergencyUser(updatedEmployeeDTO.getEmergencyUser());
        }

        if (updatedEmployeeDTO.getEmployeePhoneNo() != null) {
            List<Employee_phoneNo> newPhoneNumbers = mapPhoneNumbersFromDTO(employeeID, updatedEmployeeDTO.getEmployeePhoneNo());

            // Clear existing phone numbers but retain the collection reference
            existingEmployee.getEmployeePhoneNo().clear();

            // Add the new phone numbers to the existing collection
            existingEmployee.getEmployeePhoneNo().addAll(newPhoneNumbers);
        }
        if (updatedEmployeeDTO.getEmployeeJobRoles() != null) {
            existingEmployee.setEmployeeJobRoles(mapJobRolesFromDTO(employeeID, updatedEmployeeDTO.getEmployeeJobRoles()));
        }
        if (updatedEmployeeDTO.getEmployeeAddresses() != null) {
            List<Employee_address> newAddresses = mapAddressesFromDTO(employeeID, updatedEmployeeDTO.getEmployeeAddresses());

            // Clear the existing collection but maintain its reference
            existingEmployee.getEmployeeAddresses().clear();

            // Add all new addresses to the collection
            existingEmployee.getEmployeeAddresses().addAll(newAddresses);
        }

        // Update profile photo if a new one is provided
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                // Remove existing profile photo file if it exists
                String existingPhotoPath = existingEmployee.getProfile_photo();
                if (existingPhotoPath != null) {
                    File existingPhotoFile = new File(existingPhotoPath);
                    if (existingPhotoFile.exists()) {
                        existingPhotoFile.delete();
                    }
                }

                String originalFileName = profilePhoto.getOriginalFilename();
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                File directory = new File(DIRECTORY);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File destinationFile = new File(DIRECTORY, employeeID + fileExtension);
                profilePhoto.transferTo(destinationFile);
                existingEmployee.setProfile_photo(destinationFile.getAbsolutePath());
            } catch (Exception e) {
                throw new RuntimeException("Failed to update profile photo", e);
            }
        }

        if ((existingEmployee.getStatus().equalsIgnoreCase("Pending") && updatedEmployeeDTO.getStatus().equalsIgnoreCase("Activated")) ||
                (existingEmployee.getStatus().equalsIgnoreCase("Deactivated") && updatedEmployeeDTO.getStatus().equalsIgnoreCase("Activated"))){
            String password = Micro_utils.generateRandomPassword(10);
            existingEmployee.setPassword(authenticationUtils.hashPassword(password));

            CompletableFuture.runAsync(()->emailService.generateWelcomeLoginEmail(existingEmployee.getFirstName(),existingEmployee.getEmail(),password,frontendUrl));
        }
        existingEmployee.setStatus(updatedEmployeeDTO.getStatus());
        // Save and return the updated employee
        Employee updatedEmployee  = employeeRepository.save(existingEmployee);
        EmployeeDTO updatedEmployeeDTOData = convertToDTO(updatedEmployee);
        return updatedEmployeeDTOData;
    }

    public EmployeeDTO findOneEmployee(String employeeID){
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employeeID);
        EmployeeDTO employeeDetails = null;
        if (existingEmployeeOpt.isPresent()) {
            employeeDetails = convertToDTO(existingEmployeeOpt.get());
        }
        return employeeDetails;
    }

    public EmployeeDTO findEmployeeByEmail(String email){
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        EmployeeDTO employeeDTO = null;
        return employee.map(this::convertToDTO).orElse(employeeDTO);
    }

    public Employee findOneEmployeeWithEntity(String employeeID){
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employeeID);
        if (existingEmployeeOpt.isPresent()) {
            return existingEmployeeOpt.get();
        }else{
            throw new RuntimeException();
        }
    }

    public List<EmployeeDTO> getEmployeesByRole(String role){
        List<Employee> employees = employeeRepository.getEmployeesByRole(role);
        List<EmployeeDTO> employeeDTO = new ArrayList<>();
        for (Employee emp:employees){
            employeeDTO.add(convertToDTO(emp));
        }
        return employeeDTO;
    }

    public ApiResponse updateEmployeeState(String employeeID,String state){
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employeeID);
        if (existingEmployeeOpt.isEmpty()) {
            throw new RuntimeException();
        }
        Employee employeeData = existingEmployeeOpt.get();
        employeeData.setStatus(state);
        employeeRepository.save(employeeData);
        return new ApiResponse("User "+state+" successfully",true);
    }

    public List<MetaEmployeeDetailsDTO> getAllEmployees(){
        List<Employee> employees = employeeRepository.getAllEmployees();
        List<MetaEmployeeDetailsDTO> employeeDetailsDTOS = new ArrayList<>();
        for (Employee employee:employees){
            MetaEmployeeDetailsDTO singleEmployeeDetails = new MetaEmployeeDetailsDTO();
            singleEmployeeDetails.setEmployeeID(employee.getEmployeeID());
            singleEmployeeDetails.setStatus(employee.getStatus());
            singleEmployeeDetails.setEmail(employee.getEmail());
            singleEmployeeDetails.setProfile_photo(employee.getProfile_photo());
            singleEmployeeDetails.setFirstName(employee.getFirstName());
            singleEmployeeDetails.setLastName(employee.getLastName());
            singleEmployeeDetails.setAccessRole(employee.getAccessRole());

            employeeDetailsDTOS.add(singleEmployeeDetails);
        }
        return employeeDetailsDTOS;
    }

    public Map<String, String> getAllCareGiverEmployeeMapping() {
        List<CareGiverDTO> careGiverDTOs = careGiverService.getAllCareGivers();
        Map<String, String> employeeCareGiverMap = new HashMap<>();

        for (CareGiverDTO careGiver : careGiverDTOs) {
            // Assuming CareGiverDTO contains the mapping information
            String careGiverID = careGiver.getCareGiverID();
            String employeeID = careGiver.getEmployee().getEmployeeID(); // Adjust based on DTO

            // Add mapping to the map
            employeeCareGiverMap.put(employeeID, careGiverID);
        }

        return employeeCareGiverMap;
    }

    public Boolean resetPassword(PasswordResetDTO passwordResetDTO){
        if (passwordResetDTO.getEmployeeID().substring(0,2).equalsIgnoreCase("CU")){
            Client client = clientRepository.findByEmail(passwordResetDTO.getEmail());
            if (client!=null){
                client.setPassword(authenticationUtils.hashPassword(passwordResetDTO.getPassword()));
                clientRepository.save(client);
                return true;
            }else{
                throw new RuntimeException();
            }
        }else{
            EmployeeDTO employee = findEmployeeByEmail(passwordResetDTO.getEmail());
            employee.setPassword(authenticationUtils.hashPassword(passwordResetDTO.getPassword()));
            employeeRepository.save(mapEmployeeFromDTO(employee));
            return true;
        }
    }

}
