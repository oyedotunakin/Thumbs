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
import com.thumbs.entities.Followers;
import java.util.ArrayList;
import java.util.Collection;
import com.thumbs.entities.Productlogin;
import com.thumbs.entities.Sociallogins;
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
public class UserJpaController implements Serializable {

    public UserJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws RollbackFailureException, Exception {
        if (user.getFollowersCollection() == null) {
            user.setFollowersCollection(new ArrayList<Followers>());
        }
        if (user.getProductloginCollection() == null) {
            user.setProductloginCollection(new ArrayList<Productlogin>());
        }
        if (user.getSocialloginsCollection() == null) {
            user.setSocialloginsCollection(new ArrayList<Sociallogins>());
        }
        if (user.getThumbsCollection() == null) {
            user.setThumbsCollection(new ArrayList<Thumbs>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Followers> attachedFollowersCollection = new ArrayList<Followers>();
            for (Followers followersCollectionFollowersToAttach : user.getFollowersCollection()) {
                followersCollectionFollowersToAttach = em.getReference(followersCollectionFollowersToAttach.getClass(), followersCollectionFollowersToAttach.getId());
                attachedFollowersCollection.add(followersCollectionFollowersToAttach);
            }
            user.setFollowersCollection(attachedFollowersCollection);
            Collection<Productlogin> attachedProductloginCollection = new ArrayList<Productlogin>();
            for (Productlogin productloginCollectionProductloginToAttach : user.getProductloginCollection()) {
                productloginCollectionProductloginToAttach = em.getReference(productloginCollectionProductloginToAttach.getClass(), productloginCollectionProductloginToAttach.getId());
                attachedProductloginCollection.add(productloginCollectionProductloginToAttach);
            }
            user.setProductloginCollection(attachedProductloginCollection);
            Collection<Sociallogins> attachedSocialloginsCollection = new ArrayList<Sociallogins>();
            for (Sociallogins socialloginsCollectionSocialloginsToAttach : user.getSocialloginsCollection()) {
                socialloginsCollectionSocialloginsToAttach = em.getReference(socialloginsCollectionSocialloginsToAttach.getClass(), socialloginsCollectionSocialloginsToAttach.getId());
                attachedSocialloginsCollection.add(socialloginsCollectionSocialloginsToAttach);
            }
            user.setSocialloginsCollection(attachedSocialloginsCollection);
            Collection<Thumbs> attachedThumbsCollection = new ArrayList<Thumbs>();
            for (Thumbs thumbsCollectionThumbsToAttach : user.getThumbsCollection()) {
                thumbsCollectionThumbsToAttach = em.getReference(thumbsCollectionThumbsToAttach.getClass(), thumbsCollectionThumbsToAttach.getId());
                attachedThumbsCollection.add(thumbsCollectionThumbsToAttach);
            }
            user.setThumbsCollection(attachedThumbsCollection);
            em.persist(user);
            for (Followers followersCollectionFollowers : user.getFollowersCollection()) {
                User oldUserIdOfFollowersCollectionFollowers = followersCollectionFollowers.getUserId();
                followersCollectionFollowers.setUserId(user);
                followersCollectionFollowers = em.merge(followersCollectionFollowers);
                if (oldUserIdOfFollowersCollectionFollowers != null) {
                    oldUserIdOfFollowersCollectionFollowers.getFollowersCollection().remove(followersCollectionFollowers);
                    oldUserIdOfFollowersCollectionFollowers = em.merge(oldUserIdOfFollowersCollectionFollowers);
                }
            }
            for (Productlogin productloginCollectionProductlogin : user.getProductloginCollection()) {
                User oldUserIdOfProductloginCollectionProductlogin = productloginCollectionProductlogin.getUserId();
                productloginCollectionProductlogin.setUserId(user);
                productloginCollectionProductlogin = em.merge(productloginCollectionProductlogin);
                if (oldUserIdOfProductloginCollectionProductlogin != null) {
                    oldUserIdOfProductloginCollectionProductlogin.getProductloginCollection().remove(productloginCollectionProductlogin);
                    oldUserIdOfProductloginCollectionProductlogin = em.merge(oldUserIdOfProductloginCollectionProductlogin);
                }
            }
            for (Sociallogins socialloginsCollectionSociallogins : user.getSocialloginsCollection()) {
                User oldUserIdOfSocialloginsCollectionSociallogins = socialloginsCollectionSociallogins.getUserId();
                socialloginsCollectionSociallogins.setUserId(user);
                socialloginsCollectionSociallogins = em.merge(socialloginsCollectionSociallogins);
                if (oldUserIdOfSocialloginsCollectionSociallogins != null) {
                    oldUserIdOfSocialloginsCollectionSociallogins.getSocialloginsCollection().remove(socialloginsCollectionSociallogins);
                    oldUserIdOfSocialloginsCollectionSociallogins = em.merge(oldUserIdOfSocialloginsCollectionSociallogins);
                }
            }
            for (Thumbs thumbsCollectionThumbs : user.getThumbsCollection()) {
                User oldUserIdOfThumbsCollectionThumbs = thumbsCollectionThumbs.getUserId();
                thumbsCollectionThumbs.setUserId(user);
                thumbsCollectionThumbs = em.merge(thumbsCollectionThumbs);
                if (oldUserIdOfThumbsCollectionThumbs != null) {
                    oldUserIdOfThumbsCollectionThumbs.getThumbsCollection().remove(thumbsCollectionThumbs);
                    oldUserIdOfThumbsCollectionThumbs = em.merge(oldUserIdOfThumbsCollectionThumbs);
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

    public void edit(User user) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User persistentUser = em.find(User.class, user.getId());
            Collection<Followers> followersCollectionOld = persistentUser.getFollowersCollection();
            Collection<Followers> followersCollectionNew = user.getFollowersCollection();
            Collection<Productlogin> productloginCollectionOld = persistentUser.getProductloginCollection();
            Collection<Productlogin> productloginCollectionNew = user.getProductloginCollection();
            Collection<Sociallogins> socialloginsCollectionOld = persistentUser.getSocialloginsCollection();
            Collection<Sociallogins> socialloginsCollectionNew = user.getSocialloginsCollection();
            Collection<Thumbs> thumbsCollectionOld = persistentUser.getThumbsCollection();
            Collection<Thumbs> thumbsCollectionNew = user.getThumbsCollection();
            Collection<Followers> attachedFollowersCollectionNew = new ArrayList<Followers>();
            for (Followers followersCollectionNewFollowersToAttach : followersCollectionNew) {
                followersCollectionNewFollowersToAttach = em.getReference(followersCollectionNewFollowersToAttach.getClass(), followersCollectionNewFollowersToAttach.getId());
                attachedFollowersCollectionNew.add(followersCollectionNewFollowersToAttach);
            }
            followersCollectionNew = attachedFollowersCollectionNew;
            user.setFollowersCollection(followersCollectionNew);
            Collection<Productlogin> attachedProductloginCollectionNew = new ArrayList<Productlogin>();
            for (Productlogin productloginCollectionNewProductloginToAttach : productloginCollectionNew) {
                productloginCollectionNewProductloginToAttach = em.getReference(productloginCollectionNewProductloginToAttach.getClass(), productloginCollectionNewProductloginToAttach.getId());
                attachedProductloginCollectionNew.add(productloginCollectionNewProductloginToAttach);
            }
            productloginCollectionNew = attachedProductloginCollectionNew;
            user.setProductloginCollection(productloginCollectionNew);
            Collection<Sociallogins> attachedSocialloginsCollectionNew = new ArrayList<Sociallogins>();
            for (Sociallogins socialloginsCollectionNewSocialloginsToAttach : socialloginsCollectionNew) {
                socialloginsCollectionNewSocialloginsToAttach = em.getReference(socialloginsCollectionNewSocialloginsToAttach.getClass(), socialloginsCollectionNewSocialloginsToAttach.getId());
                attachedSocialloginsCollectionNew.add(socialloginsCollectionNewSocialloginsToAttach);
            }
            socialloginsCollectionNew = attachedSocialloginsCollectionNew;
            user.setSocialloginsCollection(socialloginsCollectionNew);
            Collection<Thumbs> attachedThumbsCollectionNew = new ArrayList<Thumbs>();
            for (Thumbs thumbsCollectionNewThumbsToAttach : thumbsCollectionNew) {
                thumbsCollectionNewThumbsToAttach = em.getReference(thumbsCollectionNewThumbsToAttach.getClass(), thumbsCollectionNewThumbsToAttach.getId());
                attachedThumbsCollectionNew.add(thumbsCollectionNewThumbsToAttach);
            }
            thumbsCollectionNew = attachedThumbsCollectionNew;
            user.setThumbsCollection(thumbsCollectionNew);
            user = em.merge(user);
            for (Followers followersCollectionOldFollowers : followersCollectionOld) {
                if (!followersCollectionNew.contains(followersCollectionOldFollowers)) {
                    followersCollectionOldFollowers.setUserId(null);
                    followersCollectionOldFollowers = em.merge(followersCollectionOldFollowers);
                }
            }
            for (Followers followersCollectionNewFollowers : followersCollectionNew) {
                if (!followersCollectionOld.contains(followersCollectionNewFollowers)) {
                    User oldUserIdOfFollowersCollectionNewFollowers = followersCollectionNewFollowers.getUserId();
                    followersCollectionNewFollowers.setUserId(user);
                    followersCollectionNewFollowers = em.merge(followersCollectionNewFollowers);
                    if (oldUserIdOfFollowersCollectionNewFollowers != null && !oldUserIdOfFollowersCollectionNewFollowers.equals(user)) {
                        oldUserIdOfFollowersCollectionNewFollowers.getFollowersCollection().remove(followersCollectionNewFollowers);
                        oldUserIdOfFollowersCollectionNewFollowers = em.merge(oldUserIdOfFollowersCollectionNewFollowers);
                    }
                }
            }
            for (Productlogin productloginCollectionOldProductlogin : productloginCollectionOld) {
                if (!productloginCollectionNew.contains(productloginCollectionOldProductlogin)) {
                    productloginCollectionOldProductlogin.setUserId(null);
                    productloginCollectionOldProductlogin = em.merge(productloginCollectionOldProductlogin);
                }
            }
            for (Productlogin productloginCollectionNewProductlogin : productloginCollectionNew) {
                if (!productloginCollectionOld.contains(productloginCollectionNewProductlogin)) {
                    User oldUserIdOfProductloginCollectionNewProductlogin = productloginCollectionNewProductlogin.getUserId();
                    productloginCollectionNewProductlogin.setUserId(user);
                    productloginCollectionNewProductlogin = em.merge(productloginCollectionNewProductlogin);
                    if (oldUserIdOfProductloginCollectionNewProductlogin != null && !oldUserIdOfProductloginCollectionNewProductlogin.equals(user)) {
                        oldUserIdOfProductloginCollectionNewProductlogin.getProductloginCollection().remove(productloginCollectionNewProductlogin);
                        oldUserIdOfProductloginCollectionNewProductlogin = em.merge(oldUserIdOfProductloginCollectionNewProductlogin);
                    }
                }
            }
            for (Sociallogins socialloginsCollectionOldSociallogins : socialloginsCollectionOld) {
                if (!socialloginsCollectionNew.contains(socialloginsCollectionOldSociallogins)) {
                    socialloginsCollectionOldSociallogins.setUserId(null);
                    socialloginsCollectionOldSociallogins = em.merge(socialloginsCollectionOldSociallogins);
                }
            }
            for (Sociallogins socialloginsCollectionNewSociallogins : socialloginsCollectionNew) {
                if (!socialloginsCollectionOld.contains(socialloginsCollectionNewSociallogins)) {
                    User oldUserIdOfSocialloginsCollectionNewSociallogins = socialloginsCollectionNewSociallogins.getUserId();
                    socialloginsCollectionNewSociallogins.setUserId(user);
                    socialloginsCollectionNewSociallogins = em.merge(socialloginsCollectionNewSociallogins);
                    if (oldUserIdOfSocialloginsCollectionNewSociallogins != null && !oldUserIdOfSocialloginsCollectionNewSociallogins.equals(user)) {
                        oldUserIdOfSocialloginsCollectionNewSociallogins.getSocialloginsCollection().remove(socialloginsCollectionNewSociallogins);
                        oldUserIdOfSocialloginsCollectionNewSociallogins = em.merge(oldUserIdOfSocialloginsCollectionNewSociallogins);
                    }
                }
            }
            for (Thumbs thumbsCollectionOldThumbs : thumbsCollectionOld) {
                if (!thumbsCollectionNew.contains(thumbsCollectionOldThumbs)) {
                    thumbsCollectionOldThumbs.setUserId(null);
                    thumbsCollectionOldThumbs = em.merge(thumbsCollectionOldThumbs);
                }
            }
            for (Thumbs thumbsCollectionNewThumbs : thumbsCollectionNew) {
                if (!thumbsCollectionOld.contains(thumbsCollectionNewThumbs)) {
                    User oldUserIdOfThumbsCollectionNewThumbs = thumbsCollectionNewThumbs.getUserId();
                    thumbsCollectionNewThumbs.setUserId(user);
                    thumbsCollectionNewThumbs = em.merge(thumbsCollectionNewThumbs);
                    if (oldUserIdOfThumbsCollectionNewThumbs != null && !oldUserIdOfThumbsCollectionNewThumbs.equals(user)) {
                        oldUserIdOfThumbsCollectionNewThumbs.getThumbsCollection().remove(thumbsCollectionNewThumbs);
                        oldUserIdOfThumbsCollectionNewThumbs = em.merge(oldUserIdOfThumbsCollectionNewThumbs);
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
                Integer id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
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
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Collection<Followers> followersCollection = user.getFollowersCollection();
            for (Followers followersCollectionFollowers : followersCollection) {
                followersCollectionFollowers.setUserId(null);
                followersCollectionFollowers = em.merge(followersCollectionFollowers);
            }
            Collection<Productlogin> productloginCollection = user.getProductloginCollection();
            for (Productlogin productloginCollectionProductlogin : productloginCollection) {
                productloginCollectionProductlogin.setUserId(null);
                productloginCollectionProductlogin = em.merge(productloginCollectionProductlogin);
            }
            Collection<Sociallogins> socialloginsCollection = user.getSocialloginsCollection();
            for (Sociallogins socialloginsCollectionSociallogins : socialloginsCollection) {
                socialloginsCollectionSociallogins.setUserId(null);
                socialloginsCollectionSociallogins = em.merge(socialloginsCollectionSociallogins);
            }
            Collection<Thumbs> thumbsCollection = user.getThumbsCollection();
            for (Thumbs thumbsCollectionThumbs : thumbsCollection) {
                thumbsCollectionThumbs.setUserId(null);
                thumbsCollectionThumbs = em.merge(thumbsCollectionThumbs);
            }
            em.remove(user);
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

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
