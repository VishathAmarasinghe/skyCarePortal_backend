package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.dto.CareGiverPaymentsDTO;
import com.skycareSolutions.skyCarePortal.model.CareGiverPayments;
import com.skycareSolutions.skyCarePortal.model.CareGiverPaymentIDs;
import com.skycareSolutions.skyCarePortal.repository.CareGiverPaymentsRepository;
import com.skycareSolutions.skyCarePortal.utils.Micro_utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CareGiverPaymentService {

    @Autowired
    private CareGiverPaymentsRepository careGiverPaymentsRepository;

    public List<CareGiverPaymentsDTO> getAllPayments() {
        return careGiverPaymentsRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CareGiverPaymentsDTO> getPaymentsByCareGiverId(String careGiverID) {
        return careGiverPaymentsRepository.findAll()
                .stream()
                .filter(payment -> payment.getId().getCareGiverID().equals(careGiverID))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // add payments
    public void addPayment(List<CareGiverPaymentsDTO> payments) {
        for (CareGiverPaymentsDTO singlePayment:payments){
            CareGiverPayments payment = mapToEntity(singlePayment);
            careGiverPaymentsRepository.save(payment);
        }
    }

    public CareGiverPaymentsDTO updatePayment(String careGiverID, String paymentTypeID, CareGiverPaymentsDTO dto) {
        CareGiverPaymentIDs ids = new CareGiverPaymentIDs(careGiverID, paymentTypeID);
        if (careGiverPaymentsRepository.existsById(ids)) {
            CareGiverPayments payment = mapToEntity(dto);
            payment.setId(ids);
            return mapToDTO(careGiverPaymentsRepository.save(payment));
        } else {
            throw new RuntimeException("Payment not found for the given IDs");
        }
    }

    public void deletePayment(String careGiverID, String paymentTypeID) {
        CareGiverPaymentIDs ids = new CareGiverPaymentIDs(careGiverID, paymentTypeID);
        careGiverPaymentsRepository.deleteById(ids);
    }

    private CareGiverPaymentsDTO mapToDTO(CareGiverPayments payment) {
        CareGiverPaymentsDTO dto = new CareGiverPaymentsDTO();
        dto.setCareGiverID(payment.getId().getCareGiverID());
        dto.setPaymentTypeID(payment.getId().getPaymentTypeID());
        dto.setAmount(payment.getAmount());
        return dto;
    }

    private CareGiverPayments mapToEntity(CareGiverPaymentsDTO dto) {
        CareGiverPayments payment = new CareGiverPayments();
        CareGiverPaymentIDs ids = new CareGiverPaymentIDs(dto.getCareGiverID(), dto.getPaymentTypeID());
        payment.setId(ids);
        payment.setAmount(dto.getAmount());
        return payment;
    }
}
