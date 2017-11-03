/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.webservices;

import javax.persistence.EntityManager;
import com.thumbs.entities.Activity;
import java.net.URI;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author akin
 */
@Path("/activity")
@com.sun.jersey.api.spring.Autowire
@Stateless
public class ActivityRESTFacade {

    @PersistenceContext(unitName = "com_Thumbs_war_1.0-SNAPSHOTPU")
    protected EntityManager entityManager;// = entityManagerFactory.createEntityManager();

    public ActivityRESTFacade() {
    }

    @POST
    @Consumes({ "application/json"})
    @Transactional
    public Response create(Activity entity) {
        entityManager.persist(entity);
        return Response.created(URI.create(entity.getId().toString())).build();
    }

    @PUT
    @Consumes({ "application/json"})
    @Transactional
    public void edit(Activity entity) {
        entityManager.merge(entity);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void remove(@PathParam("id") Integer id) {
        Activity entity = entityManager.getReference(Activity.class, id);
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    @Transactional
    public Activity find(@PathParam("id") Integer id) {
        return entityManager.find(Activity.class, id);
    }

    @GET
    @Produces({"application/json"})
    @Transactional
    public List<Activity> findAll() {
        return find(true, -1, -1);
    }

    @GET
    @Path("{max}/{first}")
    @Produces({"application/json"})
    @Transactional
    public List<Activity> findRange(@PathParam("max") Integer max, @PathParam("first") Integer first) {
        return find(false, max, first);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Activity AS o");
            return query.getSingleResult().toString();
        } finally {
//            entityManager.close();
        }
    }

    private List<Activity> find(boolean all, int maxResults, int firstResult) {
       
        try {
            Query query = entityManager.createQuery("SELECT object(o) FROM Activity AS o");
            if (!all) {
                 System.out.println(query);
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return query.getResultList();
        } catch (Exception exception) {
        } 
        finally {
//            entityManager.close();
        }
        return null;
    }

}
