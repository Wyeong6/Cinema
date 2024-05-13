package com.busanit.entity;

import com.busanit.constant.Role;
import com.busanit.domain.MemberRegFormDTO;
import com.busanit.domain.PaymentDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name="payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String amount;

    // DTO -> Entity
    public static Payment toEntity(PaymentDTO dto){
        return Payment.builder()
                .amount(dto.getAmount())
                .build();
    }
}
