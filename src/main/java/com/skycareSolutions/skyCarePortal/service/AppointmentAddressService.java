package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.model.AppointmentAddress;
import com.skycareSolutions.skyCarePortal.repository.AppointmentAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppointmentAddressService {

    @Autowired
    private AppointmentAddressRepository repository;

    /**
     * Save or update an AppointmentAddress.
     *
     * @param address the AppointmentAddress object to save.
     * @return the saved AppointmentAddress.
     */
    public AppointmentAddress save(AppointmentAddress address) {
        return repository.save(address);
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
