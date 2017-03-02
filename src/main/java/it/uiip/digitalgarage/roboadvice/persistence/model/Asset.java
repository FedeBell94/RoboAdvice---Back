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
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Asset {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="asset_class_id")
	private AssetClass assetClass;

	@Column(name = "quandlId", nullable = false)
	private int quandlId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "quandlKey", nullable = false)
	private String quandlKey;

	@Column(name = "fixedPercentage", nullable = false)
	private BigDecimal fixedPercentage;

	@Column(name = "column", nullable = false)
	private String column;

}
