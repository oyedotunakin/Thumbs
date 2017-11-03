/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author akin
 */
@Entity
@Table(name = "followers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Followers.findAll", query = "SELECT f FROM Followers f"),
    @NamedQuery(name = "Followers.findById", query = "SELECT f FROM Followers f WHERE f.id = :id"),
    @NamedQuery(name = "Followers.findByDateFollowed", query = "SELECT f FROM Followers f WHERE f.dateFollowed = :dateFollowed")})
public class Followers implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "DateFollowed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFollowed;
    @JoinColumn(name = "ProductId", referencedColumnName = "Id")
    @ManyToOne
    private Product productId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne
    private User userId;

    public Followers() {
    }

    public Followers(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateFollowed() {
        return dateFollowed;
    }

    public void setDateFollowed(Date dateFollowed) {
        this.dateFollowed = dateFollowed;
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
        if (!(object instanceof Followers)) {
            return false;
        }
        Followers other = (Followers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.thumbs.entities.Followers[ id=" + id + " ]";
    }
    
}
