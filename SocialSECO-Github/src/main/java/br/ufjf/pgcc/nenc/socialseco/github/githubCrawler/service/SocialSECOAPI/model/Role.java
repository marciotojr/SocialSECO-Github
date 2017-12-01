/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.SocialSECOAPI.model;

import java.util.ArrayList;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Marcio Júnior
 */
public class Role {
    RDFNode self;
    ArrayList<Skill> required;

    public Role(RDFNode self) {
        this.self = self;
        required = new ArrayList<>();
    }
    
    

    public RDFNode getSelf() {
        return self;
    }

    public void setSelf(RDFNode self) {
        this.self = self;
    }

    public ArrayList<Skill> getInterest() {
        return required;
    }

    public void setInterest(ArrayList<Skill> interest) {
        this.required = interest;
    }
    
    public void addInterest(Skill interest){
        this.required.add(interest);
    }
    
    public String toString(){
        return self.toString();
    }
}
