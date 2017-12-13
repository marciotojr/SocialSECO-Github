/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.visualization.graphModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author phillipe
 */
public class CompositionGraph implements Cloneable{
    private List<Node> servicesNodes;
    private List<CollaboratesEdge> dependsOfList;

    @Override
    public CompositionGraph clone() throws CloneNotSupportedException {
        return (CompositionGraph) super.clone();
    }
    
    /**
     * it calculates the shortest distance for all nodes
     * @param graph
     * @param sourceNode 
     * @return  
     */
    public Map<Node, Integer> calculateShortestDistanceGraph(CompositionGraph graph,Node sourceNode){
        
        Map<Node, Integer> distanceToSource = new HashMap<>();
        List<Node> unVisitedNodes = new ArrayList<>();
        
        //unvisitedNodes initially contains all nodes
        for(Node node:graph.servicesNodes){
            node.setDistanceFromSource(Integer.MAX_VALUE);
            unVisitedNodes.add(node);
        }

        //initially all vertices have infinite distance (constructor)
        
        //setting source distance as 0
        sourceNode.setDistanceFromSource(0);
        
        distanceToSource.put(sourceNode,0);
        
        //unVisitedNodes = copy.getServicesNodes();
        unVisitedNodes.remove(sourceNode);

        List<Node> neighborsSource = sourceNode.getNeighbors();
        
        for(Node neighb:neighborsSource){
            neighb.setDistanceFromSource(1);
            distanceToSource.put(neighb,1);
        }
        
        //while	the arrayList is not empty
        while(!unVisitedNodes.isEmpty()){
            Node u = minDistance(unVisitedNodes);
            unVisitedNodes.remove(u);
            
            for(Node v:u.getNeighbors()){
                if(v.getDistanceFromSource() > u.getDistanceFromSource()){
                    v.setDistanceFromSource(u.getDistanceFromSource()+1);
                    distanceToSource.put(v, u.getDistanceFromSource()+1);
                }
            }
        }        
        
        return distanceToSource;
    }
    
    public Integer betweness (CompositionGraph graph,Node source){
        Integer numShortestPaths = 0;
        Integer numShortestPathsSource = 1;
        List<Node> nodesList1 = new ArrayList<>();
        List<Node> nodesList2 = new ArrayList<>();
        
        //fill both lists
        for(Node node:graph.servicesNodes){
            if(!source.getName().equals(node.getName())){
                nodesList1.add(node);
                nodesList2.add(node);
            }
        }
        
        //get shortest path
        
        for(Node n1:nodesList1){
            for(Node n2:nodesList2){
                if(!n1.getName().equals(n2.getName())){
                    numShortestPaths += 0;
                    numShortestPathsSource += 1;                    
                }            
            }
        }
        
        return 0;
    }
    
    /**
     * select the element of the list with the min distance to the source node
     * @param nodes
     * @return 
     */
    public Node minDistance(List<Node> nodes){
        if(nodes != null){
            Node menor;
                menor = nodes.get(0);
            for(Node n:nodes){
                if(n.getDistanceFromSource() < menor.getDistanceFromSource()){
                    menor = n;
                }
            }
            return menor;
        }
        System.out.println("nodes list is null in minDistance method - CompositionGraph Class");
        return null;        
    }
    
    /**
     * @return the servicesNodes
     */
    public List<Node> getServicesNodes() {
        return servicesNodes;
    }

    /**
     * @param servicesNodes the servicesNodes to set
     */
    public void setServicesNodes(List<Node> servicesNodes) {
        this.servicesNodes = servicesNodes;
    }

    /**
     * @return the dependsOfList
     */
    public List<CollaboratesEdge> getDependsOfList() {
        return dependsOfList;
    }

    /**
     * @param dependsOfList the dependsOfList to set
     */
    public void setDependsOfList(List<CollaboratesEdge> dependsOfList) {
        this.dependsOfList = dependsOfList;
    }
}
