package com.bxb.sunduk_pay.logModel;

import com.bxb.sunduk_pay.model.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@Builder
public class UserActivityLog {
    @Id
    private String logId;
    private String uuid;
    private String email;
    private String fullName;
    private String action;
    private String description;
    private LocalDateTime localDateTime;
}
