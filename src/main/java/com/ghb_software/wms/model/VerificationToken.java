package com.ghb_software.wms.model;

import com.ghb_software.wms.util.WmsConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class VerificationToken extends BaseEntity {
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "token")
    private String token;
   
    @Column(name = "expiry_date")
    private Date expiryDate;

    public VerificationToken(final User user, final int tokenAvailabilityHours) {
        this.user = user;
        expiryDate = calculateExpiryDate(tokenAvailabilityHours);
        token = generateTokenForUser(this.user);
    }

    private Date calculateExpiryDate(final int tokenAvailabilityHours) {
        return Date.from(LocalDateTime.now().plusHours(tokenAvailabilityHours).atZone(ZoneId.systemDefault()).toInstant());
    }

    private String generateTokenForUser(final User user) {
        return MessageFormat.format("{1}{0}{2}{0}{3}", WmsConstants.NARROW_STRING_SEPARATOR,
                WmsConstants.APPLICATION_NAME, user.getId(),UUID.randomUUID().toString());
    }
}