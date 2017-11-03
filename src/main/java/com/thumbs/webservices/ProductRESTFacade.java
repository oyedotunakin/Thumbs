/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.webservices;

import com.thumbs.entities.Comment;
import javax.persistence.EntityManager;
import com.thumbs.entities.Product;
import com.thumbs.entities.Trending;
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
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author akin
 */
@Path("/product")
@com.sun.jersey.spi.resource.Singleton
@com.sun.jersey.api.spring.Autowire
@Stateless
public class ProductRESTFacade {

    @PersistenceContext(unitName = "com_Thumbs_war_1.0-SNAPSHOTPU")
    protected EntityManager entityManager;

    public ProductRESTFacade() {
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Transactional
    public Response create(Product entity) {
        System.out.println(entity.getPlaceId());
        try {
            entityManager.persist(entity);
        } catch (Exception exx) {
            entity = (Product) entityManager.createNamedQuery("Product.findByPlaceId").setParameter("placeId", entity.getPlaceId()).getSingleResult();
        }
        return Response.status(201).entity(entity.getId()).build();
    }

    @PUT
    @Consumes({"application/json"})
    @Transactional
    public void edit(Product entity) {
        entityManager.merge(entity);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void remove(@PathParam("id") Integer id) {
        Product entity = entityManager.getReference(Product.class, id);
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    @Transactional
    public Product find(@PathParam("id") Integer id) {
        return entityManager.find(Product.class, id);
    }

    @GET
    @Path("placeId/{placeId}")
    @Produces({"application/json"})
    @Transactional
    public Response findByPlaceId(@PathParam("placeId") String placeId) {
        Product p = null;
        try {
            p = (Product) entityManager.createNamedQuery("Product.findByPlaceId").setParameter("placeId", placeId).getSingleResult();
        } catch (javax.persistence.NonUniqueResultException nure) {
        }
        return Response.ok().entity(p).build();
    }

    @GET
    @Produces({"application/json"})
    @Transactional
    public List<Product> findAll() {
        return find(true, -1, -1);
    }

    @GET
    @Path("{max}/{first}")
    @Produces({"application/json"})
    @Transactional
    public List<Product> findRange(@PathParam("max") Integer max, @PathParam("first") Integer first) {
        return find(false, max, first);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Product AS o");
            return query.getSingleResult().toString();
        } finally {
//            entityManager.close();
        }
    }

    @GET
    @Path("trending")
    @Produces({"application/json"})
    @Transactional
    public Response findTrending() {
        try {
            Query query = entityManager.createNativeQuery("select Name,p.Id as ProductId,SUM(Vote = True) AS ThumbsUp, SUM(Vote = False) AS ThumbsDown,PlaceId,Address,Phone from product p left join thumbs t on p.Id = t.ProductId group by ProductId order by ThumbsUp desc limit 20", Trending.class);
            List<Trending> resultList = query.getResultList();

            JSONArray jSONArray = new JSONArray();

            for (Trending t : resultList) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("placeId", t.getPlaceId());
                    jSONObject.put("address", t.getAddress());
                    jSONObject.put("name", t.getName());
                    jSONObject.put("productId", t.getProductId());
                    jSONObject.put("thumbsDown", t.getThumbsDown());
                    jSONObject.put("thumbsUp", t.getThumbsUp());
                    jSONArray.put(jSONObject);
                } catch (JSONException ex) {
                    Logger.getLogger(ThumbsRESTFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return Response.ok().entity(jSONArray).build();
        } finally {
        }
    }

    @GET
    @Path("biz-search/{name}")
    @Produces({"application/json"})
    @Transactional
    public List<Trending> findBusinessByName(@PathParam("name") String name) {
        try {
            Query query = entityManager.createNativeQuery("select Name,Id as ProductId,0 AS ThumbsUp, 0 AS ThumbsDown,PlaceId,Address,Phone from product p WHERE Name LIKE '%" + name + "%' limit 20", Trending.class);
            return query.getResultList();
        } finally {
        }
    }

    @GET
    @Path("thumbs-stats/{placeId}")
    @Produces({"application/json"})
    @Transactional
    public Response findThumbsStats(@PathParam("placeId") String placeId) {
        try {
            placeId = placeId.trim();
            Query query = entityManager.createNativeQuery("select Name,p.Id as ProductId,SUM(Vote = True) AS ThumbsUp, SUM(Vote = False) AS ThumbsDown,PlaceId,Address,Phone from product p left join thumbs t on p.Id = t.ProductId where PlaceId = :placeId group by ProductId order by ThumbsUp desc limit 1", Trending.class);
            Trending t = (Trending) query.setParameter("placeId", placeId).getSingleResult();
            return Response.ok().entity(t).build();
        } finally {
        }
    }

    private List<Product> find(boolean all, int maxResults, int firstResult) {
        try {
            Query query = entityManager.createQuery("SELECT object(o) FROM Product AS o");
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return query.getResultList();
        } finally {
        }
    }

}
