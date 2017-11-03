/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author akin
 */
@Entity
@Table(name = "sociallogins")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sociallogins.findAll", query = "SELECT s FROM Sociallogins s"),
    @NamedQuery(name = "Sociallogins.findById", query = "SELECT s FROM Sociallogins s WHERE s.id = :id"),
    @NamedQuery(name = "Sociallogins.findByName", query = "SELECT s FROM Sociallogins s WHERE s.name = :name"),
    @NamedQuery(name = "Sociallogins.findByToken", query = "SELECT s FROM Sociallogins s WHERE s.token = :token")})
public class Sociallogins implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 100)
    @Column(name = "Name")
    private String name;
    @Size(max = 45)
    @Column(name = "Token")
    private String token;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne
    private User userId;

    public Sociallogins() {
    }

    public Sociallogins(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sociallogins)) {
            return false;
        }
        Sociallogins other = (Sociallogins) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.thumbs.entities.Sociallogins[ id=" + id + " ]";
    }
    
}
