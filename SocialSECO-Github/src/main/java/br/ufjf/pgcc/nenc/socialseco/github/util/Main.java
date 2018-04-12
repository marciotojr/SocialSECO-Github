package br.ufjf.pgcc.nenc.socialseco.github.util;

import br.ufjf.pgcc.nenc.socialseco.github.socialSECOAPI.GetRecomendedDevelopers;
import br.ufjf.pgcc.nenc.socialseco.github.view.graph.GraphGenerator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marci
 */
public class Main {

    public static void main(String[] argsv) {
        GraphGenerator gg = new GraphGenerator("mojombo");
        /*String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "                 PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "                 PREFIX xml: <http://www.w3.org/XML/1998/namespace>\n"
                + "                 PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "                 PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "                 PREFIX onto: <http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#>\n"
                + "                 \n"
                + "                 SELECT DISTINCT ?repository\n"
                + "                 WHERE {?repository rdf:type onto:repository}";
        System.out.println(query);*/
        /*GetRecomendedDevelopers grd = new GetRecomendedDevelopers();
        grd.getRecomendedDevelopers("mojombo", "git-brz");*/
    }}

