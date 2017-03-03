package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import lombok.*;

import java.sql.Date;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class UserDTO {

    private String email;

    private String password;

    private String username;

    private Date registration;

    public UserDTO(User user){
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.registration = user.getRegistration();
    }
}
