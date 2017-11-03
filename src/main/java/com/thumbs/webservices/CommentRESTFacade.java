/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.webservices;

import javax.persistence.EntityManager;
import com.thumbs.entities.Comment;
import com.thumbs.entities.Product;
import com.thumbs.entities.User;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author akin
 */
@Path("/comment")
@com.sun.jersey.spi.resource.Singleton
@com.sun.jersey.api.spring.Autowire
@Stateless
public class CommentRESTFacade {

    @PersistenceContext(unitName = "com_Thumbs_war_1.0-SNAPSHOTPU")
    protected EntityManager entityManager;

    public CommentRESTFacade() {
    }

    @POST
    @Path("{productId}/{userId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Transactional
    public Response create(Comment entity, @PathParam("productId") int productId, @PathParam("userId") int userId) {

        User user = entityManager.find(User.class, userId);
        if (user == null) {
            return Response.serverError().entity(5).build();
        }

        entity.setUserId(user);
        entity.setDate(Calendar.getInstance().getTime());
        entity.setProductId(entityManager.find(Product.class, productId));
        entityManager.persist(entity);
        return Response.ok().entity(1).build();
//        return Response.created(URI.create(entity.getId().toString())).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void edit(Comment entity) {
        entityManager.merge(entity);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void remove(@PathParam("id") Integer id) {
        Comment entity = entityManager.getReference(Comment.class, id);
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Comment find(@PathParam("id") Integer id) {
        return entityManager.find(Comment.class, id);
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public List<Comment> findAll() {
        return find(true, -1, -1);
    }

    @GET
    @Path("{max}/{first}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public List<Comment> findRange(@PathParam("max") Integer max, @PathParam("first") Integer first) {
        return find(false, max, first);
    }

    @GET
    @Path("{productId}/{max}/{first}")
    @Produces({"application/json"})
    @Transactional
    public Response findRangeByProductId(@PathParam("productId") Integer productId, @PathParam("max") Integer max, @PathParam("first") Integer first) {
        JSONArray findByProductId = findByProductId(productId, max, first);
        return Response.ok().entity(findByProductId).build();
    }

    private JSONArray findByProductId(int productId, int maxResults, int firstResult) {
        try {
            Query query = entityManager.createNamedQuery("Comment.findByProductId")
                    .setParameter("productId", entityManager.find(Product.class, productId));
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
            List<Comment> resultList = query.getResultList();
            JSONArray jSONArray = new JSONArray();

            for (Comment t : resultList) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("comment", t.getComment());
                    jSONObject.put("name", t.getUserId().getFirstName());
                    jSONObject.put("date", t.getDate().toString());
                    jSONArray.put(jSONObject);

                } catch (JSONException ex) {
                    Logger.getLogger(ThumbsRESTFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return jSONArray;
        } finally {
        }
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Comment AS o");
            return query.getSingleResult().toString();
        } finally {
//            entityManager.close();
        }
    }

    private List<Comment> find(boolean all, int maxResults, int firstResult) {
        try {
            Query query = entityManager.createQuery("SELECT object(o) FROM Comment AS o");
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return query.getResultList();
        } finally {
//            entityManager.close();
        }
    }

}
