package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.AppointmentAddressDTO;
import com.skycareSolutions.skyCarePortal.model.AppointmentAddress;
import com.skycareSolutions.skyCarePortal.repository.AppointmentAddressRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppointmentAddressService {

    @Autowired
    private AppointmentAddressRepository repository;

    private AppointmentAddress mapToEntitiy(AppointmentAddressDTO address){
        AppointmentAddress addressEntity = new AppointmentAddress();
        addressEntity.setAddress(address.getAddress());
        addressEntity.setCity(address.getCity());
        addressEntity.setState(address.getState());
        addressEntity.setPostalCode(address.getPostalCode());
        addressEntity.setAppointmentAddressID(address.getAppointmentAddressID());

        return addressEntity;
    }

    /**
     * Save or update an AppointmentAddress .
     *
     * @param address the AppointmentAddress object to save.
     * @return the saved AppointmentAddress.
     */
    public AppointmentAddress save(AppointmentAddressDTO address) {
        String lastAppointmentAddressID = repository.getLastIdOfAppointmentAddress();
        String newAppointmentAddressID = Micro_utils.idGenerator("appointmentAddress",lastAppointmentAddressID);
        address.setAppointmentAddressID(newAppointmentAddressID);
        AppointmentAddress appointmentAddress = mapToEntitiy(address);
        return repository.save(appointmentAddress);
    }

    /**
     * Find an AppointmentAddress by its ID.
     *
     * @param id the ID of the AppointmentAddress.
     * @return an Optional containing the AppointmentAddress if found.
     */
    public Optional<AppointmentAddress> findById(String id) {
        return repository.findById(id);
    }

    public AppointmentAddress findByIDWithEntity(String id){
        Optional<AppointmentAddress> appointmentAddress = repository.findById(id);
        if (appointmentAddress.isPresent()){
            return appointmentAddress.get();
        }else{
            throw new RuntimeException();
        }
    }
    public AppointmentAddressDTO findAppointmentAddressByID(String id){
        Optional<AppointmentAddress> appointmentAddress = repository.findById(id);
        if (appointmentAddress.isPresent()){
            AppointmentAddress appointmentAddress1 = appointmentAddress.get();
            AppointmentAddressDTO appointmentAddressDTO = new AppointmentAddressDTO();
            appointmentAddressDTO.setAppointmentAddressID(appointmentAddress1.getAppointmentAddressID());
            appointmentAddressDTO.setAddress(appointmentAddress1.getAddress());
            appointmentAddressDTO.setCity(appointmentAddress1.getCity());
            appointmentAddressDTO.setState(appointmentAddress1.getState());
            appointmentAddressDTO.setPostalCode(appointmentAddress1.getPostalCode());
            return appointmentAddressDTO;
        }else{
            throw new RuntimeException();
        }
    }

    /**
     * Retrieve all AppointmentAddresses.
     *
     * @return an Iterable of all AppointmentAddresses.
     */
    public Iterable<AppointmentAddress> findAll() {
        return repository.findAll();
    }

    /**
     * Delete an AppointmentAddress by its ID.
     *
     * @param id the ID of the AppointmentAddress to delete.
     */
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    /**
     * Get the last AppointmentAddress ID.
     *
     * @return the last ID as a String, or null if no records are present.
     */
    public String getLastAppointmentAddressId() {
        return repository.getLastIdOfAppointmentAddress();
    }
}
