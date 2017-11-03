/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.controllers;

import com.thumbs.controllers.exceptions.NonexistentEntityException;
import com.thumbs.controllers.exceptions.RollbackFailureException;
import com.thumbs.entities.Followers;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.thumbs.entities.Product;
import com.thumbs.entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author akin
 */
public class FollowersJpaController implements Serializable {

    public FollowersJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Followers followers) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Product productId = followers.getProductId();
            if (productId != null) {
                productId = em.getReference(productId.getClass(), productId.getId());
                followers.setProductId(productId);
            }
            User userId = followers.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                followers.setUserId(userId);
            }
            em.persist(followers);
            if (productId != null) {
                productId.getFollowersCollection().add(followers);
                productId = em.merge(productId);
            }
            if (userId != null) {
                userId.getFollowersCollection().add(followers);
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

    public void edit(Followers followers) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Followers persistentFollowers = em.find(Followers.class, followers.getId());
            Product productIdOld = persistentFollowers.getProductId();
            Product productIdNew = followers.getProductId();
            User userIdOld = persistentFollowers.getUserId();
            User userIdNew = followers.getUserId();
            if (productIdNew != null) {
                productIdNew = em.getReference(productIdNew.getClass(), productIdNew.getId());
                followers.setProductId(productIdNew);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                followers.setUserId(userIdNew);
            }
            followers = em.merge(followers);
            if (productIdOld != null && !productIdOld.equals(productIdNew)) {
                productIdOld.getFollowersCollection().remove(followers);
                productIdOld = em.merge(productIdOld);
            }
            if (productIdNew != null && !productIdNew.equals(productIdOld)) {
                productIdNew.getFollowersCollection().add(followers);
                productIdNew = em.merge(productIdNew);
            }
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getFollowersCollection().remove(followers);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getFollowersCollection().add(followers);
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
                Integer id = followers.getId();
                if (findFollowers(id) == null) {
                    throw new NonexistentEntityException("The followers with id " + id + " no longer exists.");
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
            Followers followers;
            try {
                followers = em.getReference(Followers.class, id);
                followers.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The followers with id " + id + " no longer exists.", enfe);
            }
            Product productId = followers.getProductId();
            if (productId != null) {
                productId.getFollowersCollection().remove(followers);
                productId = em.merge(productId);
            }
            User userId = followers.getUserId();
            if (userId != null) {
                userId.getFollowersCollection().remove(followers);
                userId = em.merge(userId);
            }
            em.remove(followers);
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

    public List<Followers> findFollowersEntities() {
        return findFollowersEntities(true, -1, -1);
    }

    public List<Followers> findFollowersEntities(int maxResults, int firstResult) {
        return findFollowersEntities(false, maxResults, firstResult);
    }

    private List<Followers> findFollowersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Followers.class));
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

    public Followers findFollowers(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Followers.class, id);
        } finally {
            em.close();
        }
    }

    public int getFollowersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Followers> rt = cq.from(Followers.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
