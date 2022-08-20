package com.dailycodebuffer.client.service;

import com.dailycodebuffer.client.entity.PasswordResetToken;
import com.dailycodebuffer.client.entity.User;
import com.dailycodebuffer.client.entity.VerificationToken;
import com.dailycodebuffer.client.model.UserModel;
import com.dailycodebuffer.client.repository.PasswordResetTokenRepository;
import com.dailycodebuffer.client.repository.UserRepository;
import com.dailycodebuffer.client.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User registerUser(UserModel userModel) {

        User user = User.builder()
                .email(userModel.getEmail())
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .role("USER").build();

        user.setPassword(passwordEncoder.encode(userModel.getPassword()));

        userRepository.save(user);
        return user;
    }
    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken= new VerificationToken(user,token);
        verificationTokenRepository.save(verificationToken);

    }
    @Override
    public String validVerificationToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        log.info("verificationToken returned from DB::{}"+verificationToken);

        if (verificationToken == null) {
            return "invalid";
        }
        // Now Get the user from verificationToken and calculate the token expiry time
        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if (verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "Token Expired";
        }
        // Now if above both the condition fails then it means user is valid and now we have to enable the user
        // and save the user back with this enabled flag
        user.setEnabled(true);
        userRepository.save(user);
        return "Valid";
    }
    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken= verificationTokenRepository.findByToken(oldToken);
        // Now generate the new token and set New token using setToken and save in the DB
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
    @Override
    public User findUserByEmail(String email) {
        User userByEmail = userRepository.findUserByEmail(email);
        return userByEmail;
    }
    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        // For passwordReset token as well we have to create entity
        PasswordResetToken passwordResetToken= new PasswordResetToken(user,token);
        passwordResetTokenRepository.save(passwordResetToken);

    }

    @Override
    public String validatePasswordResetToken(String token) {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        log.info("passwordResetToken returned from DB::{}- "+ passwordResetToken);

        if (passwordResetToken == null) {
            return "invalid";
        }
        // Now Get the user from verificationToken and calculate the token expiry time
        User user = passwordResetToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if (passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "Token Expired";
        }
        // Now if above both the condition fails then it means user is valid and now we have to enable the user
        // and save the user back with this enabled flag
        /*
        user.setEnabled(true);
        userRepository.save(user);
        */
        // Don't need the above line becuse user is already enabled only need to resent password
        return "Valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {

        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changeNewPasswordAndSave(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword,user.getPassword());
    }

    @Override
    public boolean checkValidEmail(User user, String email) {
        if(email.equalsIgnoreCase(user.getEmail())){
            return true;
        }else
            return false;
    }

}
