/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.view.graph;

/**
 *
 * @author marci
 */
public class User extends Node {
    
    public User(String id) {
        super(id);
    }

    @Override
    public String getInfo() {
        return "        name: '"+this.getId()+"',\n";
    }

    @Override
    public String getIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String nodeType() {
        return "Person";
    }

    @Override
    public String getColor() {
        return "ff0000";
    }
}
