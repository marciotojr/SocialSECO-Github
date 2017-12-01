/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.SocialSECOAPI.GetIntitutionsInEcosystem;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.SocialSECOAPI.GetRecomendedDevelopers;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author marci
 */
@Path("getRecommendation")
public class GetRecommendation {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetRecommendation
     */
    public GetRecommendation() {
    }

    /**
     * Retrieves representation of an instance of br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.GetRecommendation
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of GetRecommendation
     * @param content representation for the resource
     */
    @PUT
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getCollaborators/{user}/{repository}")
    public String getRecommendationsForRole(@PathParam("user") String user, @PathParam("repository") String repository) throws MalformedURLException, UnsupportedEncodingException, IOException {        
        GetRecomendedDevelopers gi = new GetRecomendedDevelopers();
        return gi.getRecomendedDevelopers(user,repository);
        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getInstitutions/{user}/")
    public String getInstitutions(@PathParam("user") String user)throws MalformedURLException, UnsupportedEncodingException, IOException {
        GetIntitutionsInEcosystem gi = new GetIntitutionsInEcosystem();
        return gi.getInstitutions(user);
    }
}
