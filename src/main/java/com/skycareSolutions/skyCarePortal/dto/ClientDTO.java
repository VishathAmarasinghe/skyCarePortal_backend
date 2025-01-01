package com.skycareSolutions.skyCarePortal.dto;

import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.ClientStatusRepository;
import com.skycareSolutions.skyCarePortal.repository.ClientTypeRepository;
import com.skycareSolutions.skyCarePortal.repository.LanguageRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


@Data
public class ClientDTO {


    private String clientID;
    private String firstName;
    private String lastName;
    private String preferredName;
    private String email;
    private String gender;
    private Date birthday;
    private String joinDate;
    private String aboutMe;
    private String interests;
    private String dislikes;
    private String profilePhoto;
    private String referenceNo;
    private ClientPhysicalAddressDTO physicalAddress;
    private ClientPostalAddressDTO postalAddress;
    private List<String> clientLanguages;
    private List<String> phoneNumbers;
    private List<String> clientClassifications;
    private String clientType;
    private String clientStatus;
}

