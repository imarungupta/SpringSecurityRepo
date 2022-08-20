package com.dailycodebuffer.client.event;

import com.dailycodebuffer.client.entity.User;
import com.dailycodebuffer.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

// Step3- Once we publish the event during the registration, we have to listen it
@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        // Create the verification token for the user with link

        User user= event.getUser();
        String token= UUID.randomUUID().toString();

    /*   This token will be saved for the particular user,
         so whenever the link is hit by the user then that particular token will
         match with the token saved in the db to verify the user, so we need to save this token in DB as well
         - Let's create an Entity VerificationToken to save token.
    */
        userService.saveVerificationTokenForUser(user,token);
        // Now VerificationToken is saved in the DB
        // Now let's create link, and then we send email to user

        String url= event.getApplicationUrl()+"/verifyRegistration?token="+token;

        // The above line will get the url from application context,
        // now let's create the api verifyRegistration in controller class with get method and pass the token
        // http://localhost:8083/verifyRegistration?@RequestParam
        // sendVerificationEmail(url); to be implemented

       log.info("Click Link to verify account: {}"+url);
    }
}
