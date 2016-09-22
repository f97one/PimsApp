/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.formula97.webapp.pims.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author f97one
 */
@Entity
@Table(name = LedgerRefUser.TABLE_NAME)
public class LedgerRefUser implements Serializable {
    
    private static final long serialVersionUID = 958929577010643134L;

    public static final String TABLE_NAME = "LEDGER_REF_USER";
    
    public static final String COLUMN_LEDGER_ID = "LEDGER_ID";
    public static final String COLUMN_USER_ID = "USER_ID";
    
    @Id
    @Column(name = COLUMN_LEDGER_ID)
    private Integer ledgerId;
    
    @Id
    @Column(name = COLUMN_USER_ID, length = 32)
    private String userId;

    public LedgerRefUser() {
        
    }
    
    public Integer getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(Integer ledgerId) {
        this.ledgerId = ledgerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    
}
