/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.ontology;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author marci
 */


public class OntologyAccess {

    private OntModel ontModel;    
    
    public OntologyAccess(String path, String fileName) {
        ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        Path pathObj = Paths.get(path, fileName);
        InputStream input;
        try {
            input = new FileInputStream(pathObj.toString());
            ontModel.read(input, "RDF/XML");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OntologyAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public OntModel getOntModel() {
        return ontModel;
    }
    
    
}
