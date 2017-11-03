/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.controllers;

import com.thumbs.controllers.exceptions.NonexistentEntityException;
import com.thumbs.controllers.exceptions.PreexistingEntityException;
import com.thumbs.controllers.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.thumbs.entities.City;
import com.thumbs.entities.Geopoint;
import com.thumbs.entities.Producttype;
import com.thumbs.entities.Followers;
import com.thumbs.entities.Product;
import java.util.ArrayList;
import java.util.Collection;
import com.thumbs.entities.Productlogin;
import com.thumbs.entities.Thumbs;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author akin
 */
public class ProductJpaController implements Serializable {

    public ProductJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Product product) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (product.getFollowersCollection() == null) {
            product.setFollowersCollection(new ArrayList<Followers>());
        }
        if (product.getProductloginCollection() == null) {
            product.setProductloginCollection(new ArrayList<Productlogin>());
        }
        if (product.getThumbsCollection() == null) {
            product.setThumbsCollection(new ArrayList<Thumbs>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            City cityId = product.getCityId();
            if (cityId != null) {
                cityId = em.getReference(cityId.getClass(), cityId.getId());
                product.setCityId(cityId);
            }
            Geopoint geoPointId = product.getGeoPointId();
            if (geoPointId != null) {
                geoPointId = em.getReference(geoPointId.getClass(), geoPointId.getId());
                product.setGeoPointId(geoPointId);
            }
            Producttype productTypeId = product.getProductTypeId();
            if (productTypeId != null) {
                productTypeId = em.getReference(productTypeId.getClass(), productTypeId.getId());
                product.setProductTypeId(productTypeId);
            }
            Collection<Followers> attachedFollowersCollection = new ArrayList<Followers>();
            for (Followers followersCollectionFollowersToAttach : product.getFollowersCollection()) {
                followersCollectionFollowersToAttach = em.getReference(followersCollectionFollowersToAttach.getClass(), followersCollectionFollowersToAttach.getId());
                attachedFollowersCollection.add(followersCollectionFollowersToAttach);
            }
            product.setFollowersCollection(attachedFollowersCollection);
            Collection<Productlogin> attachedProductloginCollection = new ArrayList<Productlogin>();
            for (Productlogin productloginCollectionProductloginToAttach : product.getProductloginCollection()) {
                productloginCollectionProductloginToAttach = em.getReference(productloginCollectionProductloginToAttach.getClass(), productloginCollectionProductloginToAttach.getId());
                attachedProductloginCollection.add(productloginCollectionProductloginToAttach);
            }
            product.setProductloginCollection(attachedProductloginCollection);
            Collection<Thumbs> attachedThumbsCollection = new ArrayList<Thumbs>();
            for (Thumbs thumbsCollectionThumbsToAttach : product.getThumbsCollection()) {
                thumbsCollectionThumbsToAttach = em.getReference(thumbsCollectionThumbsToAttach.getClass(), thumbsCollectionThumbsToAttach.getId());
                attachedThumbsCollection.add(thumbsCollectionThumbsToAttach);
            }
            product.setThumbsCollection(attachedThumbsCollection);
            em.persist(product);
            if (cityId != null) {
                cityId.getProductCollection().add(product);
                cityId = em.merge(cityId);
            }
            if (geoPointId != null) {
                geoPointId.getProductCollection().add(product);
                geoPointId = em.merge(geoPointId);
            }
            if (productTypeId != null) {
                productTypeId.getProductCollection().add(product);
                productTypeId = em.merge(productTypeId);
            }
            for (Followers followersCollectionFollowers : product.getFollowersCollection()) {
                Product oldProductIdOfFollowersCollectionFollowers = followersCollectionFollowers.getProductId();
                followersCollectionFollowers.setProductId(product);
                followersCollectionFollowers = em.merge(followersCollectionFollowers);
                if (oldProductIdOfFollowersCollectionFollowers != null) {
                    oldProductIdOfFollowersCollectionFollowers.getFollowersCollection().remove(followersCollectionFollowers);
                    oldProductIdOfFollowersCollectionFollowers = em.merge(oldProductIdOfFollowersCollectionFollowers);
                }
            }
            for (Productlogin productloginCollectionProductlogin : product.getProductloginCollection()) {
                Product oldProductIdOfProductloginCollectionProductlogin = productloginCollectionProductlogin.getProductId();
                productloginCollectionProductlogin.setProductId(product);
                productloginCollectionProductlogin = em.merge(productloginCollectionProductlogin);
                if (oldProductIdOfProductloginCollectionProductlogin != null) {
                    oldProductIdOfProductloginCollectionProductlogin.getProductloginCollection().remove(productloginCollectionProductlogin);
                    oldProductIdOfProductloginCollectionProductlogin = em.merge(oldProductIdOfProductloginCollectionProductlogin);
                }
            }
            for (Thumbs thumbsCollectionThumbs : product.getThumbsCollection()) {
                Product oldProductIdOfThumbsCollectionThumbs = thumbsCollectionThumbs.getProductId();
                thumbsCollectionThumbs.setProductId(product);
                thumbsCollectionThumbs = em.merge(thumbsCollectionThumbs);
                if (oldProductIdOfThumbsCollectionThumbs != null) {
                    oldProductIdOfThumbsCollectionThumbs.getThumbsCollection().remove(thumbsCollectionThumbs);
                    oldProductIdOfThumbsCollectionThumbs = em.merge(oldProductIdOfThumbsCollectionThumbs);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findProduct(product.getId()) != null) {
                throw new PreexistingEntityException("Product " + product + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Product product) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Product persistentProduct = em.find(Product.class, product.getId());
            City cityIdOld = persistentProduct.getCityId();
            City cityIdNew = product.getCityId();
            Geopoint geoPointIdOld = persistentProduct.getGeoPointId();
            Geopoint geoPointIdNew = product.getGeoPointId();
            Producttype productTypeIdOld = persistentProduct.getProductTypeId();
            Producttype productTypeIdNew = product.getProductTypeId();
            Collection<Followers> followersCollectionOld = persistentProduct.getFollowersCollection();
            Collection<Followers> followersCollectionNew = product.getFollowersCollection();
            Collection<Productlogin> productloginCollectionOld = persistentProduct.getProductloginCollection();
            Collection<Productlogin> productloginCollectionNew = product.getProductloginCollection();
            Collection<Thumbs> thumbsCollectionOld = persistentProduct.getThumbsCollection();
            Collection<Thumbs> thumbsCollectionNew = product.getThumbsCollection();
            if (cityIdNew != null) {
                cityIdNew = em.getReference(cityIdNew.getClass(), cityIdNew.getId());
                product.setCityId(cityIdNew);
            }
            if (geoPointIdNew != null) {
                geoPointIdNew = em.getReference(geoPointIdNew.getClass(), geoPointIdNew.getId());
                product.setGeoPointId(geoPointIdNew);
            }
            if (productTypeIdNew != null) {
                productTypeIdNew = em.getReference(productTypeIdNew.getClass(), productTypeIdNew.getId());
                product.setProductTypeId(productTypeIdNew);
            }
            Collection<Followers> attachedFollowersCollectionNew = new ArrayList<Followers>();
            for (Followers followersCollectionNewFollowersToAttach : followersCollectionNew) {
                followersCollectionNewFollowersToAttach = em.getReference(followersCollectionNewFollowersToAttach.getClass(), followersCollectionNewFollowersToAttach.getId());
                attachedFollowersCollectionNew.add(followersCollectionNewFollowersToAttach);
            }
            followersCollectionNew = attachedFollowersCollectionNew;
            product.setFollowersCollection(followersCollectionNew);
            Collection<Productlogin> attachedProductloginCollectionNew = new ArrayList<Productlogin>();
            for (Productlogin productloginCollectionNewProductloginToAttach : productloginCollectionNew) {
                productloginCollectionNewProductloginToAttach = em.getReference(productloginCollectionNewProductloginToAttach.getClass(), productloginCollectionNewProductloginToAttach.getId());
                attachedProductloginCollectionNew.add(productloginCollectionNewProductloginToAttach);
            }
            productloginCollectionNew = attachedProductloginCollectionNew;
            product.setProductloginCollection(productloginCollectionNew);
            Collection<Thumbs> attachedThumbsCollectionNew = new ArrayList<Thumbs>();
            for (Thumbs thumbsCollectionNewThumbsToAttach : thumbsCollectionNew) {
                thumbsCollectionNewThumbsToAttach = em.getReference(thumbsCollectionNewThumbsToAttach.getClass(), thumbsCollectionNewThumbsToAttach.getId());
                attachedThumbsCollectionNew.add(thumbsCollectionNewThumbsToAttach);
            }
            thumbsCollectionNew = attachedThumbsCollectionNew;
            product.setThumbsCollection(thumbsCollectionNew);
            product = em.merge(product);
            if (cityIdOld != null && !cityIdOld.equals(cityIdNew)) {
                cityIdOld.getProductCollection().remove(product);
                cityIdOld = em.merge(cityIdOld);
            }
            if (cityIdNew != null && !cityIdNew.equals(cityIdOld)) {
                cityIdNew.getProductCollection().add(product);
                cityIdNew = em.merge(cityIdNew);
            }
            if (geoPointIdOld != null && !geoPointIdOld.equals(geoPointIdNew)) {
                geoPointIdOld.getProductCollection().remove(product);
                geoPointIdOld = em.merge(geoPointIdOld);
            }
            if (geoPointIdNew != null && !geoPointIdNew.equals(geoPointIdOld)) {
                geoPointIdNew.getProductCollection().add(product);
                geoPointIdNew = em.merge(geoPointIdNew);
            }
            if (productTypeIdOld != null && !productTypeIdOld.equals(productTypeIdNew)) {
                productTypeIdOld.getProductCollection().remove(product);
                productTypeIdOld = em.merge(productTypeIdOld);
            }
            if (productTypeIdNew != null && !productTypeIdNew.equals(productTypeIdOld)) {
                productTypeIdNew.getProductCollection().add(product);
                productTypeIdNew = em.merge(productTypeIdNew);
            }
            for (Followers followersCollectionOldFollowers : followersCollectionOld) {
                if (!followersCollectionNew.contains(followersCollectionOldFollowers)) {
                    followersCollectionOldFollowers.setProductId(null);
                    followersCollectionOldFollowers = em.merge(followersCollectionOldFollowers);
                }
            }
            for (Followers followersCollectionNewFollowers : followersCollectionNew) {
                if (!followersCollectionOld.contains(followersCollectionNewFollowers)) {
                    Product oldProductIdOfFollowersCollectionNewFollowers = followersCollectionNewFollowers.getProductId();
                    followersCollectionNewFollowers.setProductId(product);
                    followersCollectionNewFollowers = em.merge(followersCollectionNewFollowers);
                    if (oldProductIdOfFollowersCollectionNewFollowers != null && !oldProductIdOfFollowersCollectionNewFollowers.equals(product)) {
                        oldProductIdOfFollowersCollectionNewFollowers.getFollowersCollection().remove(followersCollectionNewFollowers);
                        oldProductIdOfFollowersCollectionNewFollowers = em.merge(oldProductIdOfFollowersCollectionNewFollowers);
                    }
                }
            }
            for (Productlogin productloginCollectionOldProductlogin : productloginCollectionOld) {
                if (!productloginCollectionNew.contains(productloginCollectionOldProductlogin)) {
                    productloginCollectionOldProductlogin.setProductId(null);
                    productloginCollectionOldProductlogin = em.merge(productloginCollectionOldProductlogin);
                }
            }
            for (Productlogin productloginCollectionNewProductlogin : productloginCollectionNew) {
                if (!productloginCollectionOld.contains(productloginCollectionNewProductlogin)) {
                    Product oldProductIdOfProductloginCollectionNewProductlogin = productloginCollectionNewProductlogin.getProductId();
                    productloginCollectionNewProductlogin.setProductId(product);
                    productloginCollectionNewProductlogin = em.merge(productloginCollectionNewProductlogin);
                    if (oldProductIdOfProductloginCollectionNewProductlogin != null && !oldProductIdOfProductloginCollectionNewProductlogin.equals(product)) {
                        oldProductIdOfProductloginCollectionNewProductlogin.getProductloginCollection().remove(productloginCollectionNewProductlogin);
                        oldProductIdOfProductloginCollectionNewProductlogin = em.merge(oldProductIdOfProductloginCollectionNewProductlogin);
                    }
                }
            }
            for (Thumbs thumbsCollectionOldThumbs : thumbsCollectionOld) {
                if (!thumbsCollectionNew.contains(thumbsCollectionOldThumbs)) {
                    thumbsCollectionOldThumbs.setProductId(null);
                    thumbsCollectionOldThumbs = em.merge(thumbsCollectionOldThumbs);
                }
            }
            for (Thumbs thumbsCollectionNewThumbs : thumbsCollectionNew) {
                if (!thumbsCollectionOld.contains(thumbsCollectionNewThumbs)) {
                    Product oldProductIdOfThumbsCollectionNewThumbs = thumbsCollectionNewThumbs.getProductId();
                    thumbsCollectionNewThumbs.setProductId(product);
                    thumbsCollectionNewThumbs = em.merge(thumbsCollectionNewThumbs);
                    if (oldProductIdOfThumbsCollectionNewThumbs != null && !oldProductIdOfThumbsCollectionNewThumbs.equals(product)) {
                        oldProductIdOfThumbsCollectionNewThumbs.getThumbsCollection().remove(thumbsCollectionNewThumbs);
                        oldProductIdOfThumbsCollectionNewThumbs = em.merge(oldProductIdOfThumbsCollectionNewThumbs);
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
                Integer id = product.getId();
                if (findProduct(id) == null) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
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
            Product product;
            try {
                product = em.getReference(Product.class, id);
                product.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            City cityId = product.getCityId();
            if (cityId != null) {
                cityId.getProductCollection().remove(product);
                cityId = em.merge(cityId);
            }
            Geopoint geoPointId = product.getGeoPointId();
            if (geoPointId != null) {
                geoPointId.getProductCollection().remove(product);
                geoPointId = em.merge(geoPointId);
            }
            Producttype productTypeId = product.getProductTypeId();
            if (productTypeId != null) {
                productTypeId.getProductCollection().remove(product);
                productTypeId = em.merge(productTypeId);
            }
            Collection<Followers> followersCollection = product.getFollowersCollection();
            for (Followers followersCollectionFollowers : followersCollection) {
                followersCollectionFollowers.setProductId(null);
                followersCollectionFollowers = em.merge(followersCollectionFollowers);
            }
            Collection<Productlogin> productloginCollection = product.getProductloginCollection();
            for (Productlogin productloginCollectionProductlogin : productloginCollection) {
                productloginCollectionProductlogin.setProductId(null);
                productloginCollectionProductlogin = em.merge(productloginCollectionProductlogin);
            }
            Collection<Thumbs> thumbsCollection = product.getThumbsCollection();
            for (Thumbs thumbsCollectionThumbs : thumbsCollection) {
                thumbsCollectionThumbs.setProductId(null);
                thumbsCollectionThumbs = em.merge(thumbsCollectionThumbs);
            }
            em.remove(product);
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

    public List<Product> findProductEntities() {
        return findProductEntities(true, -1, -1);
    }

    public List<Product> findProductEntities(int maxResults, int firstResult) {
        return findProductEntities(false, maxResults, firstResult);
    }

    private List<Product> findProductEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
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

    public Product findProduct(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(Product.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
