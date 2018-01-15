/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.SocialSECOAPI.ontologyModel;

import java.util.ArrayList;
import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author Marcio Júnior
 */
public class Person extends Thing{
    ArrayList<Skill> interest;
    ArrayList<Skill> known;

    public Person(RDFNode self) {
        super(self);
        interest = new ArrayList<>();
        known = new ArrayList<>();
    }

    public ArrayList<Skill> getInterest() {
        return interest;
    }

    public void setInterest(ArrayList<Skill> interest) {
        this.interest = interest;
    }
    
    public void addInterest(Skill interest){
        this.interest.add(interest);
    }

    public ArrayList<Skill> getKnown() {
        return known;
    }

    public void setKnown(ArrayList<Skill> known) {
        this.known = known;
    }
    
    public void addSkill(Skill skill){
        this.known.add(skill);
    }
}
