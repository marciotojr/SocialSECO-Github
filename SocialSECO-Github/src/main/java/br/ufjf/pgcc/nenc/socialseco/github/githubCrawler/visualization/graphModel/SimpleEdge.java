/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.visualization.graphModel;

/**
 *
 * @author phillipe
 */
public abstract class SimpleEdge {
    private Node from;
    private Node to;
    private int weight;
    private boolean directed;
    
    protected SimpleEdge(Node from, Node to){
        this.from = from;
        this.to = to;
        weight = 1;
        directed = false;
    }
    
     public String getSource() {
        return from.getName();
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        from.setName(source);
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return to.getName();
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        to.setName(target);
    }
    

    /**
     * @return the from
     */
    public Node getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Node from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public Node getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Node to) {
        this.to = to;
    }

    /**
     * @return the directed
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * @param directed the directed to set
     */
    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    /**
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public abstract String getLabel();
    
    public abstract String getColor();
    
    public abstract String getType();

    public String getSize(){
        return "1";
    }

    /**
     * @return the hoverColor
     */
    public abstract String getHoverColor();
}
