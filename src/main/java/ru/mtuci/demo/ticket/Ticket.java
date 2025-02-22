package ru.mtuci.demo.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.License;

import java.util.Date;

@NoArgsConstructor
@Data
public class Ticket {

    private Date serverDate;
    private Long ticketLifetime;
    private Date activationDate;
    private Date expirationDate;
    private Long userId;
    private String deviceId;
    private String licenseBlocked;
    private String digitalSignature;
    @JsonIgnore
    private License license;
    @JsonIgnore
    private Device device;

    public Ticket(License license, Device device) {
        this.serverDate = new Date();
        this.serverDate.setTime(this.serverDate.getTime() + 3 * 60 * 60 * 1000);
        this.ticketLifetime = 604800L;
        this.activationDate = license.getActivationDate();
        this.activationDate.setTime(this.activationDate.getTime()+ 3 * 60 * 60 * 1000);
        this.expirationDate = license.getExpirationDate();
        this.expirationDate.setTime(this.expirationDate.getTime()+ 3 * 60 * 60 * 1000);
        this.userId = device.getUser() != null ? device.getUser().getId() : null;
        this.deviceId = device.getMac();
        this.licenseBlocked = license.getBlocked() != null ? license.getBlocked().toString() : "null";
        this.digitalSignature= TicketMethods.getInstance().generateDigitalSignature();
    }
}


