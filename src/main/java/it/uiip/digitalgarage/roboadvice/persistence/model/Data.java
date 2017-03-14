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
@Table(name = "Data", indexes = {@Index(name = "DATE_KEY", columnList = "date")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Data implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "value", nullable = false, precision = 14, scale = 4)
    private BigDecimal value;

    @Override
    public String toString() {
        return "Data(id=" + this.id + ", assetId=" + this.asset.getId() + ", date=" + this.date.toLocalDate() +
                ", value=" + value + ")";
    }

}
