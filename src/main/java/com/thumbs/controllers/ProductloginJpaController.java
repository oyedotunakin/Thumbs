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
import com.thumbs.entities.Productlogin;
import com.thumbs.entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author akin
 */
public class ProductloginJpaController implements Serializable {

    public ProductloginJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Productlogin productlogin) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Product productId = productlogin.getProductId();
            if (productId != null) {
                productId = em.getReference(productId.getClass(), productId.getId());
                productlogin.setProductId(productId);
            }
            User userId = productlogin.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                productlogin.setUserId(userId);
            }
            em.persist(productlogin);
            if (productId != null) {
                productId.getProductloginCollection().add(productlogin);
                productId = em.merge(productId);
            }
            if (userId != null) {
                userId.getProductloginCollection().add(productlogin);
                userId = em.merge(userId);
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

    public void edit(Productlogin productlogin) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Productlogin persistentProductlogin = em.find(Productlogin.class, productlogin.getId());
            Product productIdOld = persistentProductlogin.getProductId();
            Product productIdNew = productlogin.getProductId();
            User userIdOld = persistentProductlogin.getUserId();
            User userIdNew = productlogin.getUserId();
            if (productIdNew != null) {
                productIdNew = em.getReference(productIdNew.getClass(), productIdNew.getId());
                productlogin.setProductId(productIdNew);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                productlogin.setUserId(userIdNew);
            }
            productlogin = em.merge(productlogin);
            if (productIdOld != null && !productIdOld.equals(productIdNew)) {
                productIdOld.getProductloginCollection().remove(productlogin);
                productIdOld = em.merge(productIdOld);
            }
            if (productIdNew != null && !productIdNew.equals(productIdOld)) {
                productIdNew.getProductloginCollection().add(productlogin);
                productIdNew = em.merge(productIdNew);
            }
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getProductloginCollection().remove(productlogin);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getProductloginCollection().add(productlogin);
                userIdNew = em.merge(userIdNew);
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
                Integer id = productlogin.getId();
                if (findProductlogin(id) == null) {
                    throw new NonexistentEntityException("The productlogin with id " + id + " no longer exists.");
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
            Productlogin productlogin;
            try {
                productlogin = em.getReference(Productlogin.class, id);
                productlogin.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productlogin with id " + id + " no longer exists.", enfe);
            }
            Product productId = productlogin.getProductId();
            if (productId != null) {
                productId.getProductloginCollection().remove(productlogin);
                productId = em.merge(productId);
            }
            User userId = productlogin.getUserId();
            if (userId != null) {
                userId.getProductloginCollection().remove(productlogin);
                userId = em.merge(userId);
            }
            em.remove(productlogin);
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

    public List<Productlogin> findProductloginEntities() {
        return findProductloginEntities(true, -1, -1);
    }

    public List<Productlogin> findProductloginEntities(int maxResults, int firstResult) {
        return findProductloginEntities(false, maxResults, firstResult);
    }

    private List<Productlogin> findProductloginEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Productlogin.class));
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

    public Productlogin findProductlogin(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Productlogin.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductloginCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Productlogin> rt = cq.from(Productlogin.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
