package io.andrea.demorequestonhold.request;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "REQUEST_ON_HOLD")
public class RequestOnHold {

    @Id
    @Column(name = "IDENTIFIER")
    private String identifier;

    @Column(name = "EXPIRE_ON", columnDefinition = "TIMESTAMP")
    private Timestamp expireOn;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private RequestOnHoldStatus status = RequestOnHoldStatus.WAITING;
}
