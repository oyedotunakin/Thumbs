/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.controllers;

import com.thumbs.controllers.exceptions.NonexistentEntityException;
import com.thumbs.controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.thumbs.entities.City;
import com.thumbs.entities.Geopoint;
import com.thumbs.entities.Product;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author akin
 */
public class GeopointJpaController implements Serializable {

    public GeopointJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Geopoint geopoint) throws RollbackFailureException, Exception {
        if (geopoint.getProductCollection() == null) {
            geopoint.setProductCollection(new ArrayList<Product>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            City cityId = geopoint.getCityId();
            if (cityId != null) {
                cityId = em.getReference(cityId.getClass(), cityId.getId());
                geopoint.setCityId(cityId);
            }
            Collection<Product> attachedProductCollection = new ArrayList<Product>();
            for (Product productCollectionProductToAttach : geopoint.getProductCollection()) {
                productCollectionProductToAttach = em.getReference(productCollectionProductToAttach.getClass(), productCollectionProductToAttach.getId());
                attachedProductCollection.add(productCollectionProductToAttach);
            }
            geopoint.setProductCollection(attachedProductCollection);
            em.persist(geopoint);
            if (cityId != null) {
                cityId.getGeopointCollection().add(geopoint);
                cityId = em.merge(cityId);
            }
            for (Product productCollectionProduct : geopoint.getProductCollection()) {
                Geopoint oldGeoPointIdOfProductCollectionProduct = productCollectionProduct.getGeoPointId();
                productCollectionProduct.setGeoPointId(geopoint);
                productCollectionProduct = em.merge(productCollectionProduct);
                if (oldGeoPointIdOfProductCollectionProduct != null) {
                    oldGeoPointIdOfProductCollectionProduct.getProductCollection().remove(productCollectionProduct);
                    oldGeoPointIdOfProductCollectionProduct = em.merge(oldGeoPointIdOfProductCollectionProduct);
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

    public void edit(Geopoint geopoint) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Geopoint persistentGeopoint = em.find(Geopoint.class, geopoint.getId());
            City cityIdOld = persistentGeopoint.getCityId();
            City cityIdNew = geopoint.getCityId();
            Collection<Product> productCollectionOld = persistentGeopoint.getProductCollection();
            Collection<Product> productCollectionNew = geopoint.getProductCollection();
            if (cityIdNew != null) {
                cityIdNew = em.getReference(cityIdNew.getClass(), cityIdNew.getId());
                geopoint.setCityId(cityIdNew);
            }
            Collection<Product> attachedProductCollectionNew = new ArrayList<Product>();
            for (Product productCollectionNewProductToAttach : productCollectionNew) {
                productCollectionNewProductToAttach = em.getReference(productCollectionNewProductToAttach.getClass(), productCollectionNewProductToAttach.getId());
                attachedProductCollectionNew.add(productCollectionNewProductToAttach);
            }
            productCollectionNew = attachedProductCollectionNew;
            geopoint.setProductCollection(productCollectionNew);
            geopoint = em.merge(geopoint);
            if (cityIdOld != null && !cityIdOld.equals(cityIdNew)) {
                cityIdOld.getGeopointCollection().remove(geopoint);
                cityIdOld = em.merge(cityIdOld);
            }
            if (cityIdNew != null && !cityIdNew.equals(cityIdOld)) {
                cityIdNew.getGeopointCollection().add(geopoint);
                cityIdNew = em.merge(cityIdNew);
            }
            for (Product productCollectionOldProduct : productCollectionOld) {
                if (!productCollectionNew.contains(productCollectionOldProduct)) {
                    productCollectionOldProduct.setGeoPointId(null);
                    productCollectionOldProduct = em.merge(productCollectionOldProduct);
                }
            }
            for (Product productCollectionNewProduct : productCollectionNew) {
                if (!productCollectionOld.contains(productCollectionNewProduct)) {
                    Geopoint oldGeoPointIdOfProductCollectionNewProduct = productCollectionNewProduct.getGeoPointId();
                    productCollectionNewProduct.setGeoPointId(geopoint);
                    productCollectionNewProduct = em.merge(productCollectionNewProduct);
                    if (oldGeoPointIdOfProductCollectionNewProduct != null && !oldGeoPointIdOfProductCollectionNewProduct.equals(geopoint)) {
                        oldGeoPointIdOfProductCollectionNewProduct.getProductCollection().remove(productCollectionNewProduct);
                        oldGeoPointIdOfProductCollectionNewProduct = em.merge(oldGeoPointIdOfProductCollectionNewProduct);
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
                Integer id = geopoint.getId();
                if (findGeopoint(id) == null) {
                    throw new NonexistentEntityException("The geopoint with id " + id + " no longer exists.");
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
            Geopoint geopoint;
            try {
                geopoint = em.getReference(Geopoint.class, id);
                geopoint.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The geopoint with id " + id + " no longer exists.", enfe);
            }
            City cityId = geopoint.getCityId();
            if (cityId != null) {
                cityId.getGeopointCollection().remove(geopoint);
                cityId = em.merge(cityId);
            }
            Collection<Product> productCollection = geopoint.getProductCollection();
            for (Product productCollectionProduct : productCollection) {
                productCollectionProduct.setGeoPointId(null);
                productCollectionProduct = em.merge(productCollectionProduct);
            }
            em.remove(geopoint);
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

    public List<Geopoint> findGeopointEntities() {
        return findGeopointEntities(true, -1, -1);
    }

    public List<Geopoint> findGeopointEntities(int maxResults, int firstResult) {
        return findGeopointEntities(false, maxResults, firstResult);
    }

    private List<Geopoint> findGeopointEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Geopoint.class));
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

    public Geopoint findGeopoint(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Geopoint.class, id);
        } finally {
            em.close();
        }
    }

    public int getGeopointCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Geopoint> rt = cq.from(Geopoint.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
