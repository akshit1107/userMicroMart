package dev.akshit.usermicromart.models;

import dev.akshit.usermicromart.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
public class Session extends BaseModel{

    private String token;

    @Column(name = "expiring_at")
    private Date expiringAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.ORDINAL)
    private SessionStatus status;

}
