/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uiip.digitalgarage.roboadvice.persistence.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AssetClass")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class AssetClass {

    @Id
    private int id;

    @Column(name = "name", nullable = false)
    private String name;


    @Override
    public boolean equals(Object other) {
        if (other instanceof AssetClass) {
            if (((AssetClass) other).getId() == this.id) {
                return true;
            }
        }
        return false;
    }
}
