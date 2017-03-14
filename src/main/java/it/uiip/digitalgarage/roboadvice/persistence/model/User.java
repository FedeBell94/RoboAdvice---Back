/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uiip.digitalgarage.roboadvice.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "User")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class User implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "registration", nullable = false)
    private Date registration;


    @Column(name = "newUser", nullable = false)
    private boolean newUser;

    public User(User user){
        this.id = user.getId();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.enabled = user.isEnabled();
        this.registration = user.getRegistration();
        this.newUser = user.isNewUser();
    }
}
