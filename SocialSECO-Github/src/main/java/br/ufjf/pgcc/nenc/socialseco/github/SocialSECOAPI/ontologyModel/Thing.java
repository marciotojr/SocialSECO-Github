/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.SocialSECOAPI.ontologyModel;

import org.apache.jena.rdf.model.RDFNode;

/**
 *
 * @author marci
 */
public abstract class Thing {
    RDFNode self;
    
    public Thing(RDFNode self) {
        this.self = self;
    }

    public RDFNode getSelf() {
        return self;
    }

    public void setSelf(RDFNode self) {
        this.self = self;
    }
    
    public String toString(){
        return self.toString();
    }
}
