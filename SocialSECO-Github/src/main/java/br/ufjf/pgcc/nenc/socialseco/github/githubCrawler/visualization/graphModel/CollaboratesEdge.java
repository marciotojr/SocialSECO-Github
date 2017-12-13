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
public class CollaboratesEdge extends SimpleEdge {

    private String source;
    private String target;

    public CollaboratesEdge(DeveloperNode dev, RepositoryNode repo) {
        super(dev, repo);
    }

   
    /**
     * @return the label
     */
    public String getLabel() {
        return "Collaborates";
    }


    /**
     * @return the type
     */
    public String getType() {
        return "curvedArrow";
    }

    /**
     * @return the size
     */
    public String getSize() {
        return "1";
    }

    @Override
    public String getColor() {
        return "#F00";
    }

    @Override
    public String getHoverColor() {
        return "#FC0";
    }
}
