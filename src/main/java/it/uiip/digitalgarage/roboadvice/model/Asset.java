/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uiip.digitalgarage.roboadvice.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Simone
 */
public class Asset implements Serializable {

    private int id, assetClassId, quandlId;
    private String name, quandlKey, column;
    private BigDecimal fixedPercentage;

    public Asset() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssetClassId() {
        return assetClassId;
    }

    public void setAssetClassId(int assetClassId) {
        this.assetClassId = assetClassId;
    }

    public int getQuandlId() {
        return quandlId;
    }

    public void setQuandlId(int quandlId) {
        this.quandlId = quandlId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuandlKey() {
        return quandlKey;
    }

    public void setQuandlKey(String quandlKey) {
        this.quandlKey = quandlKey;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public BigDecimal getFixedPercentage() {
        return fixedPercentage;
    }

    public void setFixedPercentage(BigDecimal fixedPercentage) {
        this.fixedPercentage = fixedPercentage;
    }

}
