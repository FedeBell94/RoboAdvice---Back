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
public class Portfolio implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assetClassId")
    private AssetClass assetClass;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assetId")
    private Asset asset;

    @Column(name = "unit", nullable = false, precision = 14, scale = 4)
    private BigDecimal unit;

    @Column(name = "value", nullable = false, precision = 14, scale = 4)
    private BigDecimal value;

    @Column(name = "date", nullable = false)
    private Date date;

    /**
     * Transient field - it is not to saved into the database - used during the computation of the portfolio. This value
     * represents the global influence of the asset that this portfolio is composed, in the strategy (i.e. The strategy
     * for this asset class is 70%, the fixed percentage of this asset is 30%, so the value of this filed is 0.70 * 0.30
     * = 0,21 that means that this asset influences for the 21% of the total worth.
     */
    @Transient
    private BigDecimal globalInfluence;

}
