/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author akin
 */
@Entity
@Table(name = "product")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p"),
    @NamedQuery(name = "Product.findById", query = "SELECT p FROM Product p WHERE p.id = :id"),
    @NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE p.name = :name"),
    @NamedQuery(name = "Product.findByPhone", query = "SELECT p FROM Product p WHERE p.phone = :phone"),
    @NamedQuery(name = "Product.findByAddress", query = "SELECT p FROM Product p WHERE p.address = :address"),
    @NamedQuery(name = "Product.findByPlaceId", query = "SELECT p FROM Product p WHERE p.placeId = :placeId"),
    @NamedQuery(name = "Product.findByDescription", query = "SELECT p FROM Product p WHERE p.description = :description"),
    @NamedQuery(name = "Product.findByQrCode", query = "SELECT p FROM Product p WHERE p.qrCode = :qrCode"),
    @NamedQuery(name = "Product.findByDateRegistered", query = "SELECT p FROM Product p WHERE p.dateRegistered = :dateRegistered")})

@SqlResultSetMapping(name = "TrendingResult", classes = {
    @ConstructorResult(targetClass = Trending.class,
            columns = {
                @ColumnResult(name = "name", type = String.class),
                 @ColumnResult(name = "productId", type = Integer.class),
                @ColumnResult(name = "thumbsUp", type = Long.class),
                @ColumnResult(name = "thumbsDown", type = Long.class)})
})

public class Product implements Serializable {
    @Size(max = 45)
    @Column(name = "OpenDays")
    private String openDays;
    @Size(max = 45)
    @Column(name = "OpenHours")
    private String openHours;
    @OneToMany(mappedBy = "productId")
    private Collection<Comment> commentCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 45)
    @Column(name = "Name")
    private String name;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 45)
    @Column(name = "Phone")
    private String phone;
    @Size(max = 100)
    @Column(name = "PlaceId")
    private String placeId;
    @Size(max = 256)
    @Column(name = "Address")
    private String address;
    @Size(max = 256)
    @Column(name = "Description")
    private String description;
    @Size(max = 256)
    @Column(name = "QrCode")
    private String qrCode;
    @Column(name = "DateRegistered")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRegistered;
    @JoinColumn(name = "CityId", referencedColumnName = "Id")
    @ManyToOne
    private City cityId;
    @JoinColumn(name = "GeoPointId", referencedColumnName = "Id")
    @ManyToOne
    private Geopoint geoPointId;
    @JoinColumn(name = "ProductTypeId", referencedColumnName = "Id")
    @ManyToOne
    private Producttype productTypeId;
    @OneToMany(mappedBy = "productId")
    private Collection<Followers> followersCollection;
    @OneToMany(mappedBy = "productId")
    private Collection<Productlogin> productloginCollection;
    @OneToMany(mappedBy = "productId")
    private Collection<Thumbs> thumbsCollection;

    public Product() {
    }

    public Product(Integer id) {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Date getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(Date dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public City getCityId() {
        return cityId;
    }

    public void setCityId(City cityId) {
        this.cityId = cityId;
    }

    public Geopoint getGeoPointId() {
        return geoPointId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setGeoPointId(Geopoint geoPointId) {
        this.geoPointId = geoPointId;
    }

    public Producttype getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Producttype productTypeId) {
        this.productTypeId = productTypeId;
    }

    @XmlTransient
    public Collection<Followers> getFollowersCollection() {
        return followersCollection;
    }

    public void setFollowersCollection(Collection<Followers> followersCollection) {
        this.followersCollection = followersCollection;
    }

    @XmlTransient
    public Collection<Productlogin> getProductloginCollection() {
        return productloginCollection;
    }

    public void setProductloginCollection(Collection<Productlogin> productloginCollection) {
        this.productloginCollection = productloginCollection;
    }

    @XmlTransient
    public Collection<Thumbs> getThumbsCollection() {
        return thumbsCollection;
    }

    public void setThumbsCollection(Collection<Thumbs> thumbsCollection) {
        this.thumbsCollection = thumbsCollection;
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
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.thumbs.entities.Product[ id=" + id + " ]";
    }

    public String getOpenDays() {
        return openDays;
    }

    public void setOpenDays(String openDays) {
        this.openDays = openDays;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

}
