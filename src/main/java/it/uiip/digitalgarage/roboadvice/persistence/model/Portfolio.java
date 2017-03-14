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
@Table(name = "Portfolio", indexes = {@Index(name = "USER_DATE_KEY", columnList = "userId,date")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Portfolio implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="userId")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assetClassId")
    private AssetClass assetClass;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assetId")
    private Asset asset;

    @Column(name = "unit", nullable = false, precision = 14, scale = 4)
    private BigDecimal unit;

    @Column(name = "value", nullable = false, precision = 14, scale = 4)
    private BigDecimal value;

    @Column(name = "date", nullable = false)
    private Date date;

}
