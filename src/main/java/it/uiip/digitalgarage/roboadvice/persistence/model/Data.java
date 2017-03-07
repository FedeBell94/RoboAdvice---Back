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
@Table(name = "Data")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Data implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "value", nullable = false, precision = 14, scale = 4)
    private BigDecimal value;

}
