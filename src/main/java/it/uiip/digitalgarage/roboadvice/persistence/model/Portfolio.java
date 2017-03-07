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
@Table(name = "Portfolio")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Portfolio implements Serializable{

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="asset_class_id")
    private AssetClass assetClass;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="asset_id")
    private Asset asset;

    @Column(name = "unit", nullable = false, precision = 18, scale = 8)
    private BigDecimal unit;

    @Column(name = "value", nullable = false, precision = 14, scale = 4)
    private BigDecimal value;

    @Column(name = "date", nullable = false)
    private Date date;

}
