// src/test/java/com/buyflow/erp/DummyUserGen.java
package com.buyflow.erp;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.security.SecureRandom;

public class DummyUserGen {
    @Test
    void gen() {
        var encoder = new BCryptPasswordEncoder();
        var rnd = new SecureRandom();
        String chars = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        for (int i = 1; i <= 25; i++) {
            StringBuilder pw = new StringBuilder();
            for (int j = 0; j < 10; j++) pw.append(chars.charAt(rnd.nextInt(chars.length())));
            pw.append("!");                 // 특수문자 1개 보장
            String plain = pw.toString();
            String hash = encoder.encode(plain);
            // 평문은 따로 기록용, 해시는 DB용
            System.out.printf("user%02d\t%s\t%s%n", i, plain, hash);
        }
    }
}