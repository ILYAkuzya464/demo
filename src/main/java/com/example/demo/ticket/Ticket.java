package com.example.demo.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.demo.model.Device;
import com.example.demo.model.License;

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
        this.ticketLifetime = license.getLicenseType().getDefaultDuration().longValue() * 30 * 24 * 60 * 60;
        this.activationDate = license.getActivationDate();
        this.expirationDate = license.getExpirationDate();
        this.userId = device.getUser() != null ? device.getUser().getId() : null;
        this.deviceId = device.getMac();
        this.licenseBlocked = license.getBlocked() != null ? license.getBlocked().toString() : "null";
        this.digitalSignature= TicketMethods.getInstance().generateDigitalSignature();
    }
}


