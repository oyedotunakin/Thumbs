/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.entities;

import java.io.Serializable;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author akin
 */
@Entity
@Table(name = "geopoint")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Geopoint.findAll", query = "SELECT g FROM Geopoint g"),
    @NamedQuery(name = "Geopoint.findById", query = "SELECT g FROM Geopoint g WHERE g.id = :id"),
    @NamedQuery(name = "Geopoint.findByName", query = "SELECT g FROM Geopoint g WHERE g.name = :name"),
    @NamedQuery(name = "Geopoint.findByPointX", query = "SELECT g FROM Geopoint g WHERE g.pointX = :pointX"),
    @NamedQuery(name = "Geopoint.findByPointY", query = "SELECT g FROM Geopoint g WHERE g.pointY = :pointY"),
    @NamedQuery(name = "Geopoint.findByAreaName", query = "SELECT g FROM Geopoint g WHERE g.areaName = :areaName")})
public class Geopoint implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 100)
    @Column(name = "Name")
    private String name;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PointX")
    private Double pointX;
    @Column(name = "PointY")
    private Double pointY;
    @Size(max = 100)
    @Column(name = "AreaName")
    private String areaName;
    @OneToMany(mappedBy = "geoPointId")
    private Collection<Product> productCollection;
    @JoinColumn(name = "CityId", referencedColumnName = "Id")
    @ManyToOne
    private City cityId;

    public Geopoint() {
    }

    public Geopoint(Integer id) {
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

    public Double getPointX() {
        return pointX;
    }

    public void setPointX(Double pointX) {
        this.pointX = pointX;
    }

    public Double getPointY() {
        return pointY;
    }

    public void setPointY(Double pointY) {
        this.pointY = pointY;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @XmlTransient
    public Collection<Product> getProductCollection() {
        return productCollection;
    }

    public void setProductCollection(Collection<Product> productCollection) {
        this.productCollection = productCollection;
    }

    public City getCityId() {
        return cityId;
    }

    public void setCityId(City cityId) {
        this.cityId = cityId;
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
        if (!(object instanceof Geopoint)) {
            return false;
        }
        Geopoint other = (Geopoint) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.thumbs.entities.Geopoint[ id=" + id + " ]";
    }
    
}
