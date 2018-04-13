/**
 * 
 */
package net.formula97.webapp.pims.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ユーザー
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = Users.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8140112793498442361L;

    public static final String TABLE_NAME = "USERS";

    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_DISPLAY_NAME = "DISPLAY_NAME";
    public static final String COLUMN_MAIL_ADDRESS = "MAIL_ADDRESS";
    public static final String COLUMN_ENABLED = "ENABLED";
    public static final String COLUMN_AUTHORITY = "AUTHORITY";

    @Id
    @Column(name = COLUMN_USERNAME, length = 32)
    private String username;

    @JsonIgnore
    @Column(name = COLUMN_PASSWORD, length = 128, nullable = false)
    private String password;

    @Column(name = COLUMN_DISPLAY_NAME, length = 128)
    private String displayName;

    @Column(name = COLUMN_MAIL_ADDRESS, length = 128)
    private String mailAddress;

    @Column(name = COLUMN_ENABLED)
    private Boolean enabled;

    @Column(name = COLUMN_AUTHORITY, length = 64, nullable = false)
    private String authority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.authority));

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
