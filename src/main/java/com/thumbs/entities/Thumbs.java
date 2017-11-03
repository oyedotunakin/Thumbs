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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author akin
 */
@Entity
@Table(name = "thumbs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Thumbs.findAll", query = "SELECT t FROM Thumbs t"),
    @NamedQuery(name = "Thumbs.findById", query = "SELECT t FROM Thumbs t WHERE t.id = :id"),
    @NamedQuery(name = "Thumbs.findByDateEntered", query = "SELECT t FROM Thumbs t WHERE t.dateEntered = :dateEntered"),
    @NamedQuery(name = "Thumbs.findByVote", query = "SELECT t FROM Thumbs t WHERE t.vote = :vote"),
    @NamedQuery(name = "Thumbs.findByProductId", query = "SELECT t FROM Thumbs t WHERE t.productId = :productId"),
    @NamedQuery(name = "Thumbs.findByProductIdUserId", query = "SELECT t FROM Thumbs t WHERE t.productId = :productId AND t.userId = :userId"),
    @NamedQuery(name = "Thumbs.findUpVoteByProductId", query = "SELECT COUNT(t) FROM Thumbs t WHERE t.vote = TRUE AND t.productId = :productId"),
    @NamedQuery(name = "Thumbs.findDownVoteByProductId", query = "SELECT COUNT(t) FROM Thumbs t WHERE t.vote = FALSE AND t.productId = :productId"),
    @NamedQuery(name = "Thumbs.findByComment", query = "SELECT t FROM Thumbs t WHERE t.comment = :comment")})
public class Thumbs implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "DateEntered")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEntered;
    @Column(name = "Vote")
    private Boolean vote;
    @Size(max = 256)
    @Column(name = "Comment")
    private String comment;
    @JoinColumn(name = "ProductId", referencedColumnName = "Id")
    @ManyToOne
    private Product productId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne
    private User userId;

    public Thumbs() {
    }

    public Thumbs(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    public Boolean getVote() {
        return vote;
    }

    public void setVote(Boolean vote) {
        this.vote = vote;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        if (!(object instanceof Thumbs)) {
            return false;
        }
        Thumbs other = (Thumbs) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.thumbs.entities.Thumbs[ id=" + id + " ]";
    }
    
}
