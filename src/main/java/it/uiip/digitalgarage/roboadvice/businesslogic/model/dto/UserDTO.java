package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.sql.Date;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class UserDTO {

    private String username;

    private String password;

    private String nickname;

    private Date registration;

    private Boolean isNewUser;
}
