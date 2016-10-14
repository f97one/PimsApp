/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.formula97.webapp.pims.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 *
 * @author f97one
 */
@Entity
@Table(name = LedgerRefUser.TABLE_NAME)
@IdClass(LedgerRefUserPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerRefUser implements Serializable {
    
    private static final long serialVersionUID = 958929577010643134L;

    public static final String TABLE_NAME = "LEDGER_REF_USER";
    
    @Id
    private Integer ledgerId;
    
    @Id
    private String userId;
}
