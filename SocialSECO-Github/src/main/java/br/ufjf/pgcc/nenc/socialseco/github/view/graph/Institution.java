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
public class Institution extends User{
    
    public Institution(String id) {
        super(id);
    }
    
    @Override
    public String getColor(){
        return "0000ff";
    }
    
    @Override
    public String nodeType() {
        return "Institution";
    }
    
}
