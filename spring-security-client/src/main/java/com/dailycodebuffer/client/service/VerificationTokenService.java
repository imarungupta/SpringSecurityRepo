package com.dailycodebuffer.client.service;

import com.dailycodebuffer.client.entity.User;

public interface VerificationTokenService {

    String validVerificationToken(String token);
    void saveVerificationTokenForUser(User user, String token);
}
