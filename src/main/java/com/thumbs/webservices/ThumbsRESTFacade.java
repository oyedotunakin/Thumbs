/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.webservices;

import com.thumbs.entities.Product;
import javax.persistence.EntityManager;
import com.thumbs.entities.Thumbs;
import com.thumbs.entities.User;
import java.util.Calendar;
import java.util.HashMap;
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
@Path("/eThumbs")
@com.sun.jersey.spi.resource.Singleton
@com.sun.jersey.api.spring.Autowire
@Stateless
public class ThumbsRESTFacade {

    @PersistenceContext(unitName = "com_Thumbs_war_1.0-SNAPSHOTPU")
    protected EntityManager entityManager;

    public ThumbsRESTFacade() {
    }

    @POST
    @Path("{productId}/{userId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Transactional
    public Response create(Thumbs entity, @PathParam("productId") int productId, @PathParam("userId") int userId) {
        User user = entityManager.find(User.class, userId);
        Product product = entityManager.find(Product.class, productId);
        //Check if that user has voted before
        System.out.println(productId + "/" + userId);
        Thumbs eThumbs = null;
        try {
            eThumbs = (Thumbs) entityManager.createNamedQuery("Thumbs.findByProductIdUserId").setParameter("productId", product).setParameter("userId", user).getSingleResult();
        } catch (Exception e) {

        }

        if (eThumbs != null) {
            if (!(eThumbs.getVote().equals(entity.getVote()))) {
                System.out.println(entity.getVote());
                eThumbs.setVote(entity.getVote());
            }
            entityManager.merge(eThumbs);
            entityManager.flush();

        } else {
            entity.setUserId(user);
            entity.setProductId(product);
            entity.setDateEntered(Calendar.getInstance().getTime());
            entityManager.persist(entity);

        }
        //fetch thumbs stats for that place
//        Trending t = (Trending) entityManager.createNativeQuery("select Name,p.Id as ProductId,SUM(Vote = True) AS ThumbsUp, SUM(Vote = False) AS ThumbsDown,PlaceId,Address,Phone from product p left join thumbs t on p.Id = t.ProductId where p.Id = :productId group by ProductId order by ThumbsUp desc limit 1", Trending.class).setParameter("productId", productId).getSingleResult();
        return Response.ok().entity(1).build();

    }

    @PUT
    @Consumes({"application/json"})
    @Transactional
    public void edit(Thumbs entity) {
        entityManager.merge(entity);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void remove(@PathParam("id") Integer id) {
        Thumbs entity = entityManager.getReference(Thumbs.class, id);
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    @Transactional
    public Thumbs find(@PathParam("id") Integer id) {
        return entityManager.find(Thumbs.class, id);
    }

    @GET
    @Path("vote-stats/{placeId}")
    @Produces({"application/json"})
    @Transactional
    public Response findVoteStats(@PathParam("placeId") String placeId) {
        Product p = (Product) entityManager.createNamedQuery("Product.findByPlaceId").setParameter("placeId", placeId).getSingleResult();
        long a = (long) entityManager.createNamedQuery("Thumbs.findUpVoteByProductId").setParameter("productId", p).getSingleResult();
        long b = (long) entityManager.createNamedQuery("Thumbs.findDownVoteByProductId").setParameter("productId", p).getSingleResult();
        HashMap h = new HashMap();
        h.put("upvotes", a);
        h.put("downvotes", b);

        return Response.ok().entity(h).build();
    }

    @GET
    @Produces({"application/json"})
    @Transactional
    public List<Thumbs> findAll() {
        return find(true, -1, -1);
    }

    @GET
    @Path("{max}/{first}")
    @Produces({"application/json"})
    @Transactional
    public List<Thumbs> findRange(@PathParam("max") Integer max, @PathParam("first") Integer first) {
        return find(false, max, first);
    }

    @GET
    @Path("thumb-list/{productId}/{max}/{first}")
    @Produces({"application/json"})
    @Transactional
    public Response findRangeByProductId(@PathParam("productId") Integer productId, @PathParam("max") Integer max, @PathParam("first") Integer first) {
        JSONArray findByProductId = findByProductId(productId, max, first);
        return Response.ok().entity(findByProductId.toString()).build();
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Thumbs AS o");
            return query.getSingleResult().toString();
        } finally {
//            entityManager.close();
        }
    }

    private List<Thumbs> find(boolean all, int maxResults, int firstResult) {
        try {
            Query query = entityManager.createQuery("SELECT object(o) FROM Thumbs AS o");
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return query.getResultList();
        } finally {
//            entityManager.close();
        }
    }

    private JSONArray findByProductId(int productId, int maxResults, int firstResult) {
        try {
            Query query = entityManager.createNamedQuery("Thumbs.findByProductId")
                    .setParameter("productId", entityManager.find(Product.class, productId));
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
            List<Thumbs> resultList = query.getResultList();
            JSONArray jSONArray = new JSONArray();

            for (Thumbs t : resultList) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("comment", t.getComment());
                    jSONObject.put("name", t.getUserId().getFirstName());
                    jSONObject.put("date", t.getDateEntered().toString());
                    jSONArray.put(jSONObject);
                    
                } catch (JSONException ex) {
                    Logger.getLogger(ThumbsRESTFacade.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            
            return jSONArray;
        } finally {
        }
    }

}
