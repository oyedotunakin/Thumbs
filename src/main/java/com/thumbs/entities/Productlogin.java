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
@Table(name = "productlogin")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Productlogin.findAll", query = "SELECT p FROM Productlogin p"),
    @NamedQuery(name = "Productlogin.findById", query = "SELECT p FROM Productlogin p WHERE p.id = :id"),
    @NamedQuery(name = "Productlogin.findByUserName", query = "SELECT p FROM Productlogin p WHERE p.userName = :userName"),
    @NamedQuery(name = "Productlogin.findByPassword", query = "SELECT p FROM Productlogin p WHERE p.password = :password"),
    @NamedQuery(name = "Productlogin.findByGeneratePassword", query = "SELECT p FROM Productlogin p WHERE p.generatePassword = :generatePassword"),
    @NamedQuery(name = "Productlogin.findByStatus", query = "SELECT p FROM Productlogin p WHERE p.status = :status")})
public class Productlogin implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 100)
    @Column(name = "UserName")
    private String userName;
    @Size(max = 100)
    @Column(name = "Password")
    private String password;
    @Column(name = "GeneratePassword")
    private Boolean generatePassword;
    @Column(name = "Status")
    private Integer status;
    @JoinColumn(name = "ProductId", referencedColumnName = "Id")
    @ManyToOne
    private Product productId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne
    private User userId;

    public Productlogin() {
    }

    public Productlogin(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getGeneratePassword() {
        return generatePassword;
    }

    public void setGeneratePassword(Boolean generatePassword) {
        this.generatePassword = generatePassword;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Product getProductId() {
        return productId;
    }

    public void setProductId(Product productId) {
        this.productId = productId;
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
        if (!(object instanceof Productlogin)) {
            return false;
        }
        Productlogin other = (Productlogin) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.thumbs.entities.Productlogin[ id=" + id + " ]";
    }
    
}
