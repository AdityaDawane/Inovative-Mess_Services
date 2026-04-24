package com.campusmenu.mess_management.dto.response;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class VendorResponse {
    private Long vendorId;
    private String fullName;
    private String messName;
    private String email;
    private String phone;
    private Boolean isActive;
}
