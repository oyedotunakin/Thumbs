/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.controllers;

import com.thumbs.controllers.exceptions.NonexistentEntityException;
import com.thumbs.controllers.exceptions.RollbackFailureException;
import com.thumbs.entities.Sociallogins;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.thumbs.entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author akin
 */
public class SocialloginsJpaController implements Serializable {

    public SocialloginsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sociallogins sociallogins) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User userId = sociallogins.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                sociallogins.setUserId(userId);
            }
            em.persist(sociallogins);
            if (userId != null) {
                userId.getSocialloginsCollection().add(sociallogins);
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

    public void edit(Sociallogins sociallogins) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Sociallogins persistentSociallogins = em.find(Sociallogins.class, sociallogins.getId());
            User userIdOld = persistentSociallogins.getUserId();
            User userIdNew = sociallogins.getUserId();
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                sociallogins.setUserId(userIdNew);
            }
            sociallogins = em.merge(sociallogins);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getSocialloginsCollection().remove(sociallogins);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getSocialloginsCollection().add(sociallogins);
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
                Integer id = sociallogins.getId();
                if (findSociallogins(id) == null) {
                    throw new NonexistentEntityException("The sociallogins with id " + id + " no longer exists.");
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
            Sociallogins sociallogins;
            try {
                sociallogins = em.getReference(Sociallogins.class, id);
                sociallogins.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sociallogins with id " + id + " no longer exists.", enfe);
            }
            User userId = sociallogins.getUserId();
            if (userId != null) {
                userId.getSocialloginsCollection().remove(sociallogins);
                userId = em.merge(userId);
            }
            em.remove(sociallogins);
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

    public List<Sociallogins> findSocialloginsEntities() {
        return findSocialloginsEntities(true, -1, -1);
    }

    public List<Sociallogins> findSocialloginsEntities(int maxResults, int firstResult) {
        return findSocialloginsEntities(false, maxResults, firstResult);
    }

    private List<Sociallogins> findSocialloginsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sociallogins.class));
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

    public Sociallogins findSociallogins(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sociallogins.class, id);
        } finally {
            em.close();
        }
    }

    public int getSocialloginsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sociallogins> rt = cq.from(Sociallogins.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
