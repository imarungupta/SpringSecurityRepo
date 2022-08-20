package com.dailycodebuffer.client.controller;

import com.dailycodebuffer.client.entity.User;
import com.dailycodebuffer.client.event.RegistrationCompleteEvent;
import com.dailycodebuffer.client.model.PasswordModel;
import com.dailycodebuffer.client.repository.UserRepository;
import com.dailycodebuffer.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class PasswordController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel,
                                HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        log.info("resetPassword token user:- "+user);
        String token = null;
        if (user != null) {
            // Generate new token first
            token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            // Now we have saved the user and new token for password reset
        }
        // Now let's generate the URL and then create event and publish for password reset
       /* String url= applicationUrl(request)
                +"/saveResetPassword?token="
                +token;
        log.info("Click Link to Reset Password{}- "+url);*/

        // Or we can create a method to send url link for creating and publishing event
        String url = passwordResetTokenEmail(user, token, applicationUrl(request));
        return url;
    }

    // The above method will save the user and reset token in the DB and then send link to reset the password
    // In the above url link we have inbuilt the /saveResetPassword api. So let's create that
    // And it is taking token as Request Param and it will take extra info like email and new password from the request

    @PostMapping("/saveResetPassword")
    public String saveResetPassword(@RequestParam("token") String token,
                                    @RequestBody PasswordModel passwordModel){

        // First let's validate the token given in the link as a Request param
        String result= userService.validatePasswordResetToken(token);

        // Now check if token result is valid then reset the password
        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid Token";
        } else {
            Optional<User> user= userService.getUserByPasswordResetToken(token);
            if(user.isPresent()){
                userService.changeNewPasswordAndSave(user.get(), passwordModel.getNewPassword());

                return "Password Reset Successfully";
            }else
                return "Invalid Token";
        }
    }

    private String passwordResetTokenEmail(User user, String  token , String applicationUrl) {

        /*RegistrationCompleteEvent registrationCompleteEvent1=
         new RegistrationCompleteEvent(user,applicationUrl);

        eventPublisher.publishEvent(registrationCompleteEvent);*/


        String url = applicationUrl
                + "/saveResetPassword?token="
                + token;

        log.info("Click Link to Reset Password{}- "+url);

        return url;
    }

    private String applicationUrl(HttpServletRequest request) {
        String url = "http://"
                + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();

        log.info("Created Request URL: " + url);
        return url;
    }
    //-------------------------------------------------------------------
    @PostMapping("/changeOldPassword")
    public String changeNewPasswordAndSave(@RequestBody PasswordModel passwordModel){
        User user= userService.findUserByEmail(passwordModel.getEmail());
        // Check if the old password is valid
        if(!userService.checkValidOldPassword(user,passwordModel.getOldPassword())){

            return "Invalid Old Password";
        }else{

            userService.changeNewPasswordAndSave(user,passwordModel.getNewPassword());
            // Save New Password
            return "Password changed successfully";
        }
    }
    //----------------------Forgot Password----------------------
    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestBody PasswordModel passwordModel){
        User user = userService.findUserByEmail(passwordModel.getEmail());
        // check if email is valid
        if(!userService.checkValidEmail(user,passwordModel.getEmail())){
            return "Invalid Email";
        }else{

            //resetPassword(passwordModel, servletRequest);
             // Very similar to rest password
            // save the new password
            return "Password changed successfully";
        }
    }
}
