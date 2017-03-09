package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import lombok.*;

import java.sql.Date;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class UserDTO {

    private String username;

    private String password;

    private String nickname;

    private Date registration;

    private boolean autoBalancing;

    public UserDTO(User user){
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.registration = user.getRegistration();
        this.autoBalancing = user.isAutoBalancing();
    }
}
