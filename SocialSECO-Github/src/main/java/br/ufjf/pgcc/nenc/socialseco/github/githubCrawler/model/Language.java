/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model;

/**
 *
 * @author marci
 */
public class Language implements Individualizable{
    private int id;
    private String name;

    public Language(String name) {
        this.name = name;
    }

    public Language(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getURISuffix() {
        return this.getName().replaceAll(" ", "+");
    }
    
    
    
    
}
