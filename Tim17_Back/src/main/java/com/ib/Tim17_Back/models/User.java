package com.ib.Tim17_Back.models;

import com.ib.Tim17_Back.enums.UserRole;
import lombok.*;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column
    @EqualsAndHashCode.Include
    private String firstName;
    @Column
    @EqualsAndHashCode.Include
    private String lastName;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column
    private UserRole role;
    @Column
    private String password;
    @Column
    private LocalDateTime passwordLastChanged;
    @Column
    private boolean isActivated;
//    @OneToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
//    private List<Certificate> certificates;
    @Column
    private LocalDateTime lastLogin;
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private ResetCode passwordResetCode;
//    @Column(name = "last_password_reset_date")
//    private Timestamp lastPasswordResetDate;

}
