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
@Table(name = "User", indexes = {@Index(name = "USERNAME_KEY", columnList = "username", unique = true)})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class User implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "registration", nullable = false)
    private Date registration;

    @Column(name = "isNewUser", nullable = false)
    private Boolean isNewUser;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "lastPortfolioComputation")
    private Date lastPortfolioComputation;


    public User(User user){
        this.id = user.getId();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.enabled = user.getEnabled();
        this.registration = user.getRegistration();
        this.isNewUser = user.getIsNewUser();
        this.lastPortfolioComputation = user.getLastPortfolioComputation();
    }
}
