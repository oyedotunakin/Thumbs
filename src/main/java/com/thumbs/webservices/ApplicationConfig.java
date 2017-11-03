/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thumbs.webservices;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author akin
 */
@javax.ws.rs.ApplicationPath("APIs")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.thumbs.webservices.ActivityRESTFacade.class);
        resources.add(com.thumbs.webservices.CityRESTFacade.class);
        resources.add(com.thumbs.webservices.CommentRESTFacade.class);
        resources.add(com.thumbs.webservices.CountryRESTFacade.class);
        resources.add(com.thumbs.webservices.FollowersRESTFacade.class);
        resources.add(com.thumbs.webservices.GeopointRESTFacade.class);
        resources.add(com.thumbs.webservices.ProductRESTFacade.class);
        resources.add(com.thumbs.webservices.ProductloginRESTFacade.class);
        resources.add(com.thumbs.webservices.ProducttypeRESTFacade.class);
        resources.add(com.thumbs.webservices.SocialloginsRESTFacade.class);
        resources.add(com.thumbs.webservices.ThumbsRESTFacade.class);
        resources.add(com.thumbs.webservices.UserRESTFacade.class);
    }
    
}
