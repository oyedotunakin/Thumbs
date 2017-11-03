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
import com.thumbs.entities.Thumbs;
import com.thumbs.entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author akin
 */
public class ThumbsJpaController implements Serializable {

    public ThumbsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Thumbs thumbs) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Product productId = thumbs.getProductId();
            if (productId != null) {
                productId = em.getReference(productId.getClass(), productId.getId());
                thumbs.setProductId(productId);
            }
            User userId = thumbs.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                thumbs.setUserId(userId);
            }
            em.persist(thumbs);
            if (productId != null) {
                productId.getThumbsCollection().add(thumbs);
                productId = em.merge(productId);
            }
            if (userId != null) {
                userId.getThumbsCollection().add(thumbs);
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

    public void edit(Thumbs thumbs) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Thumbs persistentThumbs = em.find(Thumbs.class, thumbs.getId());
            Product productIdOld = persistentThumbs.getProductId();
            Product productIdNew = thumbs.getProductId();
            User userIdOld = persistentThumbs.getUserId();
            User userIdNew = thumbs.getUserId();
            if (productIdNew != null) {
                productIdNew = em.getReference(productIdNew.getClass(), productIdNew.getId());
                thumbs.setProductId(productIdNew);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                thumbs.setUserId(userIdNew);
            }
            thumbs = em.merge(thumbs);
            if (productIdOld != null && !productIdOld.equals(productIdNew)) {
                productIdOld.getThumbsCollection().remove(thumbs);
                productIdOld = em.merge(productIdOld);
            }
            if (productIdNew != null && !productIdNew.equals(productIdOld)) {
                productIdNew.getThumbsCollection().add(thumbs);
                productIdNew = em.merge(productIdNew);
            }
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getThumbsCollection().remove(thumbs);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getThumbsCollection().add(thumbs);
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
                Integer id = thumbs.getId();
                if (findThumbs(id) == null) {
                    throw new NonexistentEntityException("The thumbs with id " + id + " no longer exists.");
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
            Thumbs thumbs;
            try {
                thumbs = em.getReference(Thumbs.class, id);
                thumbs.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The thumbs with id " + id + " no longer exists.", enfe);
            }
            Product productId = thumbs.getProductId();
            if (productId != null) {
                productId.getThumbsCollection().remove(thumbs);
                productId = em.merge(productId);
            }
            User userId = thumbs.getUserId();
            if (userId != null) {
                userId.getThumbsCollection().remove(thumbs);
                userId = em.merge(userId);
            }
            em.remove(thumbs);
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

    public List<Thumbs> findThumbsEntities() {
        return findThumbsEntities(true, -1, -1);
    }

    public List<Thumbs> findThumbsEntities(int maxResults, int firstResult) {
        return findThumbsEntities(false, maxResults, firstResult);
    }

    private List<Thumbs> findThumbsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Thumbs.class));
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

    public Thumbs findThumbs(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Thumbs.class, id);
        } finally {
            em.close();
        }
    }

    public int getThumbsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Thumbs> rt = cq.from(Thumbs.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
