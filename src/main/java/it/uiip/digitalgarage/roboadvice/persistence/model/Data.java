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

/**
 *
 * @author Simone
 */

@Entity
@Table(name = "Data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Data implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    /*@Column(name = "assetId", nullable = false)
    private int assetId;*/

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "asset", referencedColumnName = "asset_id")
    private Asset asset;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "value", nullable = false)
    private BigDecimal value;



}
