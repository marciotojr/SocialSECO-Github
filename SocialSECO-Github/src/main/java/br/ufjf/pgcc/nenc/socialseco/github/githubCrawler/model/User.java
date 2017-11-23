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
public class User implements Individualizable{
    private int id;
    private String userName;
    private String type;
    
    private User[] followers;

    public User(int id, String userName, String type) {
        this.id = id;
        this.userName = userName;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getURISuffix() {
        return this.getUserName();
    }
    
    
}
