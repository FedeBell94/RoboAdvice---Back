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

@Entity
@Table(name = "Asset")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Asset implements Serializable {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_class_id")
    private AssetClass assetClass;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "quandlKey", nullable = false)
    private String quandlKey;

    @Column(name = "quandlId", nullable = false)
    private Integer quandlId;

    @Column(name = "quandlColumn", nullable = false)
    private String quandlColumn;

    @Column(name = "fixedPercentage", nullable = false)
    private BigDecimal fixedPercentage;


}
