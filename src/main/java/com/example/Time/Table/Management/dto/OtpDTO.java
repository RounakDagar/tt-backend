package com.example.Time.Table.Management.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OtpDTO {
    private String email;
    private String otp;
    private String password;


    public OtpDTO() {}

    public OtpDTO(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
    public OtpDTO(String email,String otp,String password){
        this.email = email;
        this.otp=otp;
        this.password=password;
    }

}
