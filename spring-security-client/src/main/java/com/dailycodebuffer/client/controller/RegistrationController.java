package com.dailycodebuffer.client.controller;

import com.dailycodebuffer.client.entity.User;
import com.dailycodebuffer.client.entity.VerificationToken;
import com.dailycodebuffer.client.event.RegistrationCompleteEvent;
import com.dailycodebuffer.client.model.UserModel;
import com.dailycodebuffer.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class RegistrationController {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // Step1- This Method will save the user in DB and initially user will be disabled
    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {

        //Step1- First save the user
        User user = userService.registerUser(userModel);

        //Step2- Publish the event using eventPublisher
        RegistrationCompleteEvent registrationCompleteEvent =
                new RegistrationCompleteEvent(user, applicationUrl(request));

        eventPublisher.publishEvent(registrationCompleteEvent); //"passTheEvent"

        // Step3- After publishing the event we have to listen the event and let's create the listener for that
        return "Success!";
    }
    // Step2- Now let's send the email link to the user so that using that can click on that link to be activated
    // For this we have to create event, which will handle creating event and send the eamil to the user
    // Once the user gets created we can send event to the user
    private String applicationUrl(HttpServletRequest request) {
        String url = "http://"
                + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();

        log.info("Created Request URL: " + url);
        return url;
    }
    // Step-3: Verifying the registration by clicking the send link.
    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {

        String result = userService.validVerificationToken(token);
        if (result.equalsIgnoreCase("valid"))
            return "User Verified Successfully";
        else
            return "Bad User";
    }
    // The above method is for verifying the token using the Link Url. But some time we don't get the URL then we need
    // resend the link: Let's implement the resendUrlLink.
    // 1-so here we will send the oldToken as a parameter to generate new token
    // HttpServletRequest for generating URL

    // Method to generate and publish the event.

    //Step-4: Resend the token to verify if token does not receive
    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          HttpServletRequest request) {

        // Get or generate the new verification token based on oldToken
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        // Get the user from this token
        User user = verificationToken.getUser();

    /*    // Now Generate an event
        RegistrationCompleteEvent registrationCompleteEvent1 =
                new RegistrationCompleteEvent(user, applicationUrl(request));
        // Publish the same event
        eventPublisher.publishEvent(registrationCompleteEvent);
     */
        // OR we can refactor the above code and generated on method for generating and publishing event
        // Now send these detail to email using below method
        resendVerificationTokenEmail(user, applicationUrl(request), verificationToken);
        return "Verification Link Sent";
    }
    // Generating and publishing the event for token to resend with URL.
    private void resendVerificationTokenEmail(User user, String applicationUrl, VerificationToken verificationToken) {

        // Generating and publishing the event
        RegistrationCompleteEvent registrationCompleteEvent =
                new RegistrationCompleteEvent(user, applicationUrl);

        eventPublisher.publishEvent(registrationCompleteEvent);

       /*

       String url= applicationUrl+"/verifyRegistration?token="+verificationToken.getToken();
        log.info("Click on the link to verify account:- "+url);

        */
    }
}
