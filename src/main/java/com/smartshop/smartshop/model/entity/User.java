package com.smartshop.smartshop.model.entity;

import com.smartshop.smartshop.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends Auditable{
    @Column(nullable = false, unique = true)
    protected String username;

    @Column(nullable = false)
    protected String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected UserRole role;

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
