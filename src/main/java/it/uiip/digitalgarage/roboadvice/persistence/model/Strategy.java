/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uiip.digitalgarage.roboadvice.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "Strategy", indexes = {@Index(name = "USER_KEY", columnList = "userId")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Strategy implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="userId")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assetClassId")
    private AssetClass assetClass;

    @Column(name = "percentage", nullable = false, precision = 14, scale = 4)
    private BigDecimal percentage;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "startingDate", nullable = false)
    private Date startingDate;

}
