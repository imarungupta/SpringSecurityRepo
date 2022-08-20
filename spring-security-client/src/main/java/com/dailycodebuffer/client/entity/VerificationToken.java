package com.dailycodebuffer.client.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {


    private static final int EXPIRATION_TIME=10; // 10 sec

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    private String token;
    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_VERIFY_TOKEN"))
    private User user;

    public VerificationToken(User user, String token){
        this.user=user;
        this.token=token;
        this.expirationTime= calculateExpirationTokenTime(EXPIRATION_TIME);
    }
    public VerificationToken(String token){
        super();
        this.token=token;
        this.expirationTime=calculateExpirationTokenTime(EXPIRATION_TIME);
    }

    private Date calculateExpirationTokenTime(int expirationTime) {
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE,expirationTime);

        return new Date(calendar.getTime().getTime());
    }
}
