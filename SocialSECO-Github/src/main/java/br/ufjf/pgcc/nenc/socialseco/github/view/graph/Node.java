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
public abstract class Node {

    private String id;

    public Node(String id) {
        this.id = id;
    }

    public abstract String getInfo();

    public abstract String getColor();

    public abstract String getIcon();

    @Override
    public String toString() {
        return id;
    }

    public abstract String nodeType();

    public String generateNodeScript() {
        return "{\n"
                + "      id: '" + id + "',\n"
                + "      label: '" + id + "',\n"
                + "      x: Math.random()*100,\n"
                + "      y: Math.random()*100,\n"
                + "      color: '#" + getColor() + "',\n"
                + "      size: 1,\n"
                + "      data: {\n"
                + getInfo()
                + "      }\n"
                + "},\n";
    }

    public String getId() {
        return id;
    }

}
