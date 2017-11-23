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
public class Repository implements Individualizable{
    private int id;
    private int ownerId;
    private String name;
    
    private User[] followers;
    private User[] collaborators;

    public Repository(int id, int ownerId, String name) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner_id() {
        return ownerId;
    }

    public void setOwner_id(int owner_id) {
        this.ownerId = owner_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getURISuffix(){
        return this.getName().replace("/", "+");
    }
    
}
