/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.controllers;

import com.thumbs.controllers.exceptions.NonexistentEntityException;
import com.thumbs.controllers.exceptions.RollbackFailureException;
import com.thumbs.entities.City;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.thumbs.entities.Country;
import com.thumbs.entities.Product;
import java.util.ArrayList;
import java.util.Collection;
import com.thumbs.entities.Geopoint;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author akin
 */
public class CityJpaController implements Serializable {

    public CityJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(City city) throws RollbackFailureException, Exception {
        if (city.getProductCollection() == null) {
            city.setProductCollection(new ArrayList<Product>());
        }
        if (city.getGeopointCollection() == null) {
            city.setGeopointCollection(new ArrayList<Geopoint>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Country countryId = city.getCountryId();
            if (countryId != null) {
                countryId = em.getReference(countryId.getClass(), countryId.getId());
                city.setCountryId(countryId);
            }
            Collection<Product> attachedProductCollection = new ArrayList<Product>();
            for (Product productCollectionProductToAttach : city.getProductCollection()) {
                productCollectionProductToAttach = em.getReference(productCollectionProductToAttach.getClass(), productCollectionProductToAttach.getId());
                attachedProductCollection.add(productCollectionProductToAttach);
            }
            city.setProductCollection(attachedProductCollection);
            Collection<Geopoint> attachedGeopointCollection = new ArrayList<Geopoint>();
            for (Geopoint geopointCollectionGeopointToAttach : city.getGeopointCollection()) {
                geopointCollectionGeopointToAttach = em.getReference(geopointCollectionGeopointToAttach.getClass(), geopointCollectionGeopointToAttach.getId());
                attachedGeopointCollection.add(geopointCollectionGeopointToAttach);
            }
            city.setGeopointCollection(attachedGeopointCollection);
            em.persist(city);
            if (countryId != null) {
                countryId.getCityCollection().add(city);
                countryId = em.merge(countryId);
            }
            for (Product productCollectionProduct : city.getProductCollection()) {
                City oldCityIdOfProductCollectionProduct = productCollectionProduct.getCityId();
                productCollectionProduct.setCityId(city);
                productCollectionProduct = em.merge(productCollectionProduct);
                if (oldCityIdOfProductCollectionProduct != null) {
                    oldCityIdOfProductCollectionProduct.getProductCollection().remove(productCollectionProduct);
                    oldCityIdOfProductCollectionProduct = em.merge(oldCityIdOfProductCollectionProduct);
                }
            }
            for (Geopoint geopointCollectionGeopoint : city.getGeopointCollection()) {
                City oldCityIdOfGeopointCollectionGeopoint = geopointCollectionGeopoint.getCityId();
                geopointCollectionGeopoint.setCityId(city);
                geopointCollectionGeopoint = em.merge(geopointCollectionGeopoint);
                if (oldCityIdOfGeopointCollectionGeopoint != null) {
                    oldCityIdOfGeopointCollectionGeopoint.getGeopointCollection().remove(geopointCollectionGeopoint);
                    oldCityIdOfGeopointCollectionGeopoint = em.merge(oldCityIdOfGeopointCollectionGeopoint);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(City city) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            City persistentCity = em.find(City.class, city.getId());
            Country countryIdOld = persistentCity.getCountryId();
            Country countryIdNew = city.getCountryId();
            Collection<Product> productCollectionOld = persistentCity.getProductCollection();
            Collection<Product> productCollectionNew = city.getProductCollection();
            Collection<Geopoint> geopointCollectionOld = persistentCity.getGeopointCollection();
            Collection<Geopoint> geopointCollectionNew = city.getGeopointCollection();
            if (countryIdNew != null) {
                countryIdNew = em.getReference(countryIdNew.getClass(), countryIdNew.getId());
                city.setCountryId(countryIdNew);
            }
            Collection<Product> attachedProductCollectionNew = new ArrayList<Product>();
            for (Product productCollectionNewProductToAttach : productCollectionNew) {
                productCollectionNewProductToAttach = em.getReference(productCollectionNewProductToAttach.getClass(), productCollectionNewProductToAttach.getId());
                attachedProductCollectionNew.add(productCollectionNewProductToAttach);
            }
            productCollectionNew = attachedProductCollectionNew;
            city.setProductCollection(productCollectionNew);
            Collection<Geopoint> attachedGeopointCollectionNew = new ArrayList<Geopoint>();
            for (Geopoint geopointCollectionNewGeopointToAttach : geopointCollectionNew) {
                geopointCollectionNewGeopointToAttach = em.getReference(geopointCollectionNewGeopointToAttach.getClass(), geopointCollectionNewGeopointToAttach.getId());
                attachedGeopointCollectionNew.add(geopointCollectionNewGeopointToAttach);
            }
            geopointCollectionNew = attachedGeopointCollectionNew;
            city.setGeopointCollection(geopointCollectionNew);
            city = em.merge(city);
            if (countryIdOld != null && !countryIdOld.equals(countryIdNew)) {
                countryIdOld.getCityCollection().remove(city);
                countryIdOld = em.merge(countryIdOld);
            }
            if (countryIdNew != null && !countryIdNew.equals(countryIdOld)) {
                countryIdNew.getCityCollection().add(city);
                countryIdNew = em.merge(countryIdNew);
            }
            for (Product productCollectionOldProduct : productCollectionOld) {
                if (!productCollectionNew.contains(productCollectionOldProduct)) {
                    productCollectionOldProduct.setCityId(null);
                    productCollectionOldProduct = em.merge(productCollectionOldProduct);
                }
            }
            for (Product productCollectionNewProduct : productCollectionNew) {
                if (!productCollectionOld.contains(productCollectionNewProduct)) {
                    City oldCityIdOfProductCollectionNewProduct = productCollectionNewProduct.getCityId();
                    productCollectionNewProduct.setCityId(city);
                    productCollectionNewProduct = em.merge(productCollectionNewProduct);
                    if (oldCityIdOfProductCollectionNewProduct != null && !oldCityIdOfProductCollectionNewProduct.equals(city)) {
                        oldCityIdOfProductCollectionNewProduct.getProductCollection().remove(productCollectionNewProduct);
                        oldCityIdOfProductCollectionNewProduct = em.merge(oldCityIdOfProductCollectionNewProduct);
                    }
                }
            }
            for (Geopoint geopointCollectionOldGeopoint : geopointCollectionOld) {
                if (!geopointCollectionNew.contains(geopointCollectionOldGeopoint)) {
                    geopointCollectionOldGeopoint.setCityId(null);
                    geopointCollectionOldGeopoint = em.merge(geopointCollectionOldGeopoint);
                }
            }
            for (Geopoint geopointCollectionNewGeopoint : geopointCollectionNew) {
                if (!geopointCollectionOld.contains(geopointCollectionNewGeopoint)) {
                    City oldCityIdOfGeopointCollectionNewGeopoint = geopointCollectionNewGeopoint.getCityId();
                    geopointCollectionNewGeopoint.setCityId(city);
                    geopointCollectionNewGeopoint = em.merge(geopointCollectionNewGeopoint);
                    if (oldCityIdOfGeopointCollectionNewGeopoint != null && !oldCityIdOfGeopointCollectionNewGeopoint.equals(city)) {
                        oldCityIdOfGeopointCollectionNewGeopoint.getGeopointCollection().remove(geopointCollectionNewGeopoint);
                        oldCityIdOfGeopointCollectionNewGeopoint = em.merge(oldCityIdOfGeopointCollectionNewGeopoint);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = city.getId();
                if (findCity(id) == null) {
                    throw new NonexistentEntityException("The city with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            City city;
            try {
                city = em.getReference(City.class, id);
                city.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The city with id " + id + " no longer exists.", enfe);
            }
            Country countryId = city.getCountryId();
            if (countryId != null) {
                countryId.getCityCollection().remove(city);
                countryId = em.merge(countryId);
            }
            Collection<Product> productCollection = city.getProductCollection();
            for (Product productCollectionProduct : productCollection) {
                productCollectionProduct.setCityId(null);
                productCollectionProduct = em.merge(productCollectionProduct);
            }
            Collection<Geopoint> geopointCollection = city.getGeopointCollection();
            for (Geopoint geopointCollectionGeopoint : geopointCollection) {
                geopointCollectionGeopoint.setCityId(null);
                geopointCollectionGeopoint = em.merge(geopointCollectionGeopoint);
            }
            em.remove(city);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<City> findCityEntities() {
        return findCityEntities(true, -1, -1);
    }

    public List<City> findCityEntities(int maxResults, int firstResult) {
        return findCityEntities(false, maxResults, firstResult);
    }

    private List<City> findCityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(City.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public City findCity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(City.class, id);
        } finally {
            em.close();
        }
    }

    public int getCityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<City> rt = cq.from(City.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
