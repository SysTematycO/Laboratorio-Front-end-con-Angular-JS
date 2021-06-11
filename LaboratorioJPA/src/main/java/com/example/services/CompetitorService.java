/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import javax.ws.rs.NotAuthorizedException;
import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.naming.AuthenticationException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Mauricio
 */
@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

    @PersistenceContext(unitName = "Competitors")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();

    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO competitor) {
        JSONObject rta = new JSONObject();
        Competitor competitorTmp = new Competitor();
        competitorTmp.setAddress(competitor.getAddress());
        competitorTmp.setAge(competitor.getAge());
        competitorTmp.setCellphone(competitor.getCellphone());
        competitorTmp.setCity(competitor.getCity());
        competitorTmp.setCountry(competitor.getCountry());
        competitorTmp.setName(competitor.getName());
        competitorTmp.setSurname(competitor.getSurname());
        competitorTmp.setPassword(competitor.getPassword());
        competitorTmp.setTelephone(competitor.getTelephone());
        
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(competitorTmp);
            entityManager.getTransaction().commit();
            entityManager.refresh(competitorTmp);
            rta.put("competitor_id", competitorTmp.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            competitorTmp = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();

    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(CompetitorDTO competitor) {
        JSONObject rta = new JSONObject();
        boolean ver = false;
        int num = 0;
        
            Query q = entityManager.createQuery("SELECT e FROM Competitor e");
            
            List<Competitor> usuario = q.getResultList();
            
            for (int i = 0; i < usuario.size(); i++) {
            if(usuario.get(i).getAddress().equals(competitor.getAddress())&&usuario.get(i).getPassword().equals(competitor.getPassword())){
               ver=true; 
               num = i;
                try {
                    rta.put("competitor_adrress", "Logeado");
                } catch (JSONException ex) {
                    Logger.getLogger(CompetitorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
          }
            if (ver) {
                System.out.println("existe");
                return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
            }else{
            try {
                rta.put("competitor_adrress", "No Logeado");
            } catch (JSONException ex) {
                Logger.getLogger(CompetitorService.class.getName()).log(Level.SEVERE, null, ex);
            }
            //throw new NotAuthorizedException("las credeenciales no coinciden",Response.status(200).header("Access-Control-Allow-Origin", "*").entity(usuario).build());
                System.out.println("no existe");
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
            }
    }
    
    @GET
    @Path("/login/{address}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@PathParam("address") String address, @PathParam("password") String password) {
 
            Query q = entityManager.createQuery("SELECT e FROM Competitor e WHERE e.address = :address AND e.password= :password");
            
            q.setParameter("address", address);
            q.setParameter("password", password);

            List<Competitor> usuario = q.getResultList();
            
            if (usuario.isEmpty()==false) {
                return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(usuario).build();
            }
            //throw new NotAuthorizedException("las credeenciales no coinciden",Response.status(200).header("Access-Control-Allow-Origin", "*").entity(usuario).build());
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(usuario).build();
    }
}
