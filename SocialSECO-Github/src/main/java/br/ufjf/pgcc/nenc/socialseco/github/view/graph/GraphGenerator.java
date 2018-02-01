/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.view.graph;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.controler.OntologyManager;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author marci
 */
public class GraphGenerator {

    private Map<String, Node> nodeSet;
    private Map<String, Edge> edgeSet;
    private Model model;

    public GraphGenerator(String user) {
        nodeSet = new HashMap<>();
        edgeSet = new HashMap<>();
        OntologyManager oa = new OntologyManager("C:", "temp.owl");
        model = oa.getModel();
        readOntology();
        String script = this.generateScript();
    }

    public void readOntology() {
        getInstitutions();
        getPeople();
        getRepositories();
        getCollaborations();
        System.out.println(generateScript());
    }

    private void getInstitutions() {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "                 PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "                 PREFIX xml: <http://www.w3.org/XML/1998/namespace>\n"
                + "                 PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "                 PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "                 PREFIX onto: <http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#>\n"
                + "                 \n"
                + "                 SELECT DISTINCT ?institution\n"
                + "                 WHERE {?institution rdf:type onto:Institution}";

        Dataset dataset = DatasetFactory.create(model);

        Query consulta = QueryFactory.create(query);

        QueryExecution qexec = QueryExecutionFactory.create(consulta, dataset);
        ResultSet resultado = qexec.execSelect();
        while (resultado.hasNext()) {
            QuerySolution tuple = (QuerySolution) resultado.next();
            Node institution = new Institution(removePrefix(tuple.get("institution").toString()));
            nodeSet.put(institution.toString(), institution);
        }
    }

    private void getPeople() {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "                 PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "                 PREFIX xml: <http://www.w3.org/XML/1998/namespace>\n"
                + "                 PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "                 PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "                 PREFIX onto: <http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#>\n"
                + "                 \n"
                + "                 SELECT DISTINCT ?person\n"
                + "                 WHERE {?person rdf:type onto:Person}";

        Dataset dataset = DatasetFactory.create(model);

        Query consulta = QueryFactory.create(query);

        QueryExecution qexec = QueryExecutionFactory.create(consulta, dataset);
        ResultSet resultado = qexec.execSelect();
        while (resultado.hasNext()) {
            QuerySolution tuple = (QuerySolution) resultado.next();
            Node person = new User(removePrefix(tuple.get("person").toString()));
            nodeSet.put(person.toString(), person);
        }
    }

    private void getRepositories() {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "                 PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "                 PREFIX xml: <http://www.w3.org/XML/1998/namespace>\n"
                + "                 PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "                 PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "                 PREFIX onto: <http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#>\n"
                + "                 \n"
                + "                 SELECT DISTINCT ?repository\n"
                + "                 WHERE {?repository rdf:type onto:Repository}";

        Dataset dataset = DatasetFactory.create(model);

        Query consulta = QueryFactory.create(query);

        QueryExecution qexec = QueryExecutionFactory.create(consulta, dataset);
        ResultSet resultado = qexec.execSelect();
        while (resultado.hasNext()) {
            QuerySolution tuple = (QuerySolution) resultado.next();
            Node repository = new Repository(removePrefix(tuple.get("repository").toString()));
            nodeSet.put(repository.toString(), repository);
        }
    }

    public void getCollaborations() {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "                 PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "                 PREFIX xml: <http://www.w3.org/XML/1998/namespace>\n"
                + "                 PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "                 PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "                 PREFIX onto: <http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#>\n"
                + "                 \n"
                + "                 SELECT ?repository ?user\n"
                + "                 WHERE {?user onto:collaborates ?repository.?repository rdf:type onto:Repository}";

        Dataset dataset = DatasetFactory.create(model);

        Query consulta = QueryFactory.create(query);

        QueryExecution qexec = QueryExecutionFactory.create(consulta, dataset);
        ResultSet resultado = qexec.execSelect();
        while (resultado.hasNext()) {
            QuerySolution tuple = (QuerySolution) resultado.next();
            Node repository = nodeSet.get(removePrefix(tuple.get("repository").toString()));
            Node user = nodeSet.get(removePrefix(tuple.get("user").toString()));
            if (user != null && repository != null) {
                Edge edge = new Edge(user, repository);
                edgeSet.put(edge.toString(), edge);
            }
        }
    }

    private static String removePrefix(String uri) {
        return uri.substring(uri.indexOf("#") + 1, uri.length());
    }

    private String generateScript() {
        String graph = "var s,\n"
                + "    g = {nodes: [],edges: []};\n";
        String node = "g.nodes.push(\n";
        String edge = "\ng.edges.push(\n";

        for (Node n : nodeSet.values()) {
            node += n.generateNodeScript();
        }
        if (node.endsWith(",\n")) {
            node = node.substring(0, node.length() - 2) + ");\n";
        }
        for (Edge e : edgeSet.values()) {
            edge += e.generateNodeScript();
        }
        if (edge.endsWith(",\n")) {
            edge = edge.substring(0, edge.length() - 2) + ");\n";
        }
        
        graph += node + edge;

        return graph;
    }
}
