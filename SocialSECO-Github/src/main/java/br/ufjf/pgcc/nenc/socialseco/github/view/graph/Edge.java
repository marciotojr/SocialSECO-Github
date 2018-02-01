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
public class Edge {

    protected Node source, destination;

    public Edge() {
    }

    public Edge(Node source, Node destination) {
        this.source = source;
        this.destination = destination;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        if (source.toString().compareTo(destination.toString()) > 0) {
            return source.toString() + "/" + destination.toString();
        } else {
            return destination.toString() + "/" + source.toString();
        }
    }

    public String generateNodeScript() {
        return "{\n"
                + "      id: 'e" + toString() + "',\n"
                + "      label: 'subClassOf',\n"
                + "      source: '" + source.getId() + "',\n"
                + "      target: '" + destination.getId() + "',\n"
                + "      color: '#0000ff',\n"
                + "      hover_color: '#FC0',\n"
                + "      type: 'arrow',\n"
                + "      size: 1      \n"
                + "    },\n";

    }
}
