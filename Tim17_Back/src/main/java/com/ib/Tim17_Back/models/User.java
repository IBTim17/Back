package com.ib.Tim17_Back.models;

import com.ib.Tim17_Back.enums.UserRole;
import lombok.*;
import java.util.List;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column
    public String firstName;
    @Column
    public String lastName;
    @Column(nullable = false, unique = true)
    public String phoneNumber;
    @Column(nullable = false, unique = true)
    public String email;
    @Enumerated(EnumType.STRING)
    @Column
    public UserRole role;
    @Column
    public String password;
    @Column
    public LocalDateTime passwordLastChanged;
    @Column
    public boolean isActivated;
//    @OneToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
//    public List<Certificate> certificates;

}
