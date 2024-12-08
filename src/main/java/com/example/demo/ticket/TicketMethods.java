package com.example.demo.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@RequiredArgsConstructor
@Component
public class TicketMethods {

    private static TicketMethods instance;

    @PostConstruct
    private void init() {
        instance = this;
    }

    public static TicketMethods getInstance() {
        return instance;
    }

    private String HMAC_ALGORITHM = "HmacSHA512";

    @Value("${Key}")
    private String secretKey;

    private ObjectMapper objectMapper = new ObjectMapper();

    public String serialize() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации объекта", e);
        }
    }

    public String generateDigitalSignature() {
        try {
            String data = serialize();
            SecretKeySpec secretKey = new SecretKeySpec(this.secretKey.getBytes(), HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании цифровой подписи", e);
        }
    }
}