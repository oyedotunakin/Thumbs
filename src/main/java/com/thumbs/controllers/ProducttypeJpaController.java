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
import com.thumbs.entities.Product;
import com.thumbs.entities.Producttype;
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
public class ProducttypeJpaController implements Serializable {

    public ProducttypeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Producttype producttype) throws RollbackFailureException, Exception {
        if (producttype.getProductCollection() == null) {
            producttype.setProductCollection(new ArrayList<Product>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Product> attachedProductCollection = new ArrayList<Product>();
            for (Product productCollectionProductToAttach : producttype.getProductCollection()) {
                productCollectionProductToAttach = em.getReference(productCollectionProductToAttach.getClass(), productCollectionProductToAttach.getId());
                attachedProductCollection.add(productCollectionProductToAttach);
            }
            producttype.setProductCollection(attachedProductCollection);
            em.persist(producttype);
            for (Product productCollectionProduct : producttype.getProductCollection()) {
                Producttype oldProductTypeIdOfProductCollectionProduct = productCollectionProduct.getProductTypeId();
                productCollectionProduct.setProductTypeId(producttype);
                productCollectionProduct = em.merge(productCollectionProduct);
                if (oldProductTypeIdOfProductCollectionProduct != null) {
                    oldProductTypeIdOfProductCollectionProduct.getProductCollection().remove(productCollectionProduct);
                    oldProductTypeIdOfProductCollectionProduct = em.merge(oldProductTypeIdOfProductCollectionProduct);
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

    public void edit(Producttype producttype) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Producttype persistentProducttype = em.find(Producttype.class, producttype.getId());
            Collection<Product> productCollectionOld = persistentProducttype.getProductCollection();
            Collection<Product> productCollectionNew = producttype.getProductCollection();
            Collection<Product> attachedProductCollectionNew = new ArrayList<Product>();
            for (Product productCollectionNewProductToAttach : productCollectionNew) {
                productCollectionNewProductToAttach = em.getReference(productCollectionNewProductToAttach.getClass(), productCollectionNewProductToAttach.getId());
                attachedProductCollectionNew.add(productCollectionNewProductToAttach);
            }
            productCollectionNew = attachedProductCollectionNew;
            producttype.setProductCollection(productCollectionNew);
            producttype = em.merge(producttype);
            for (Product productCollectionOldProduct : productCollectionOld) {
                if (!productCollectionNew.contains(productCollectionOldProduct)) {
                    productCollectionOldProduct.setProductTypeId(null);
                    productCollectionOldProduct = em.merge(productCollectionOldProduct);
                }
            }
            for (Product productCollectionNewProduct : productCollectionNew) {
                if (!productCollectionOld.contains(productCollectionNewProduct)) {
                    Producttype oldProductTypeIdOfProductCollectionNewProduct = productCollectionNewProduct.getProductTypeId();
                    productCollectionNewProduct.setProductTypeId(producttype);
                    productCollectionNewProduct = em.merge(productCollectionNewProduct);
                    if (oldProductTypeIdOfProductCollectionNewProduct != null && !oldProductTypeIdOfProductCollectionNewProduct.equals(producttype)) {
                        oldProductTypeIdOfProductCollectionNewProduct.getProductCollection().remove(productCollectionNewProduct);
                        oldProductTypeIdOfProductCollectionNewProduct = em.merge(oldProductTypeIdOfProductCollectionNewProduct);
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
                Integer id = producttype.getId();
                if (findProducttype(id) == null) {
                    throw new NonexistentEntityException("The producttype with id " + id + " no longer exists.");
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
            Producttype producttype;
            try {
                producttype = em.getReference(Producttype.class, id);
                producttype.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The producttype with id " + id + " no longer exists.", enfe);
            }
            Collection<Product> productCollection = producttype.getProductCollection();
            for (Product productCollectionProduct : productCollection) {
                productCollectionProduct.setProductTypeId(null);
                productCollectionProduct = em.merge(productCollectionProduct);
            }
            em.remove(producttype);
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

    public List<Producttype> findProducttypeEntities() {
        return findProducttypeEntities(true, -1, -1);
    }

    public List<Producttype> findProducttypeEntities(int maxResults, int firstResult) {
        return findProducttypeEntities(false, maxResults, firstResult);
    }

    private List<Producttype> findProducttypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Producttype.class));
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

    public Producttype findProducttype(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producttype.class, id);
        } finally {
            em.close();
        }
    }

    public int getProducttypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Producttype> rt = cq.from(Producttype.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
