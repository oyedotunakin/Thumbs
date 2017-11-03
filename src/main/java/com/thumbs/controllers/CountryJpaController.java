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
import com.thumbs.entities.Country;
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
public class CountryJpaController implements Serializable {

    public CountryJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Country country) throws RollbackFailureException, Exception {
        if (country.getCityCollection() == null) {
            country.setCityCollection(new ArrayList<City>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<City> attachedCityCollection = new ArrayList<City>();
            for (City cityCollectionCityToAttach : country.getCityCollection()) {
                cityCollectionCityToAttach = em.getReference(cityCollectionCityToAttach.getClass(), cityCollectionCityToAttach.getId());
                attachedCityCollection.add(cityCollectionCityToAttach);
            }
            country.setCityCollection(attachedCityCollection);
            em.persist(country);
            for (City cityCollectionCity : country.getCityCollection()) {
                Country oldCountryIdOfCityCollectionCity = cityCollectionCity.getCountryId();
                cityCollectionCity.setCountryId(country);
                cityCollectionCity = em.merge(cityCollectionCity);
                if (oldCountryIdOfCityCollectionCity != null) {
                    oldCountryIdOfCityCollectionCity.getCityCollection().remove(cityCollectionCity);
                    oldCountryIdOfCityCollectionCity = em.merge(oldCountryIdOfCityCollectionCity);
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

    public void edit(Country country) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Country persistentCountry = em.find(Country.class, country.getId());
            Collection<City> cityCollectionOld = persistentCountry.getCityCollection();
            Collection<City> cityCollectionNew = country.getCityCollection();
            Collection<City> attachedCityCollectionNew = new ArrayList<City>();
            for (City cityCollectionNewCityToAttach : cityCollectionNew) {
                cityCollectionNewCityToAttach = em.getReference(cityCollectionNewCityToAttach.getClass(), cityCollectionNewCityToAttach.getId());
                attachedCityCollectionNew.add(cityCollectionNewCityToAttach);
            }
            cityCollectionNew = attachedCityCollectionNew;
            country.setCityCollection(cityCollectionNew);
            country = em.merge(country);
            for (City cityCollectionOldCity : cityCollectionOld) {
                if (!cityCollectionNew.contains(cityCollectionOldCity)) {
                    cityCollectionOldCity.setCountryId(null);
                    cityCollectionOldCity = em.merge(cityCollectionOldCity);
                }
            }
            for (City cityCollectionNewCity : cityCollectionNew) {
                if (!cityCollectionOld.contains(cityCollectionNewCity)) {
                    Country oldCountryIdOfCityCollectionNewCity = cityCollectionNewCity.getCountryId();
                    cityCollectionNewCity.setCountryId(country);
                    cityCollectionNewCity = em.merge(cityCollectionNewCity);
                    if (oldCountryIdOfCityCollectionNewCity != null && !oldCountryIdOfCityCollectionNewCity.equals(country)) {
                        oldCountryIdOfCityCollectionNewCity.getCityCollection().remove(cityCollectionNewCity);
                        oldCountryIdOfCityCollectionNewCity = em.merge(oldCountryIdOfCityCollectionNewCity);
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
                Integer id = country.getId();
                if (findCountry(id) == null) {
                    throw new NonexistentEntityException("The country with id " + id + " no longer exists.");
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
            Country country;
            try {
                country = em.getReference(Country.class, id);
                country.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The country with id " + id + " no longer exists.", enfe);
            }
            Collection<City> cityCollection = country.getCityCollection();
            for (City cityCollectionCity : cityCollection) {
                cityCollectionCity.setCountryId(null);
                cityCollectionCity = em.merge(cityCollectionCity);
            }
            em.remove(country);
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

    public List<Country> findCountryEntities() {
        return findCountryEntities(true, -1, -1);
    }

    public List<Country> findCountryEntities(int maxResults, int firstResult) {
        return findCountryEntities(false, maxResults, firstResult);
    }

    private List<Country> findCountryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Country.class));
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

    public Country findCountry(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Country.class, id);
        } finally {
            em.close();
        }
    }

    public int getCountryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Country> rt = cq.from(Country.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
