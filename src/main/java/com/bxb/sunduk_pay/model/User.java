package com.bxb.sunduk_pay.model;



import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class User {
    @Id
    private String uuid;
    private String fullName;
//    private String firstName;
//    private String lastName;
    private String gender;
    private String email;
    private String phoneNumber;
    private String password;
    private Boolean isDeleted;
}
