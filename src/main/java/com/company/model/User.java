package com.company.model;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.company.enums.UserStatusEnum.UNCONFIRMED;

@SuppressWarnings(value = "ALL")
@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true,nullable = false)
    private String emailorusername;
    private String password;
    private String activationCode;
    private Date expiredDate;
    @Column(length = 6)
    private String sixDigitCode;
    private Date forgetPasswordExpiredDate;
    private Date updateDate;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_status_id",referencedColumnName = "id")
    private UserStatus status = new UserStatus();


    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date createdDate;

    @PrePersist
    public void persist() {
        getStatus().setId(UNCONFIRMED.getStatusId());
        setCreatedDate(new Date());
    }
}
