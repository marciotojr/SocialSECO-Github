/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.socialSECOAPI;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.controler.OntologyManager;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.UserDAO;
import br.ufjf.pgcc.nenc.socialseco.github.socialSECOAPI.ontologyModel.Person;
import br.ufjf.pgcc.nenc.socialseco.github.socialSECOAPI.ontologyModel.Role;
import br.ufjf.pgcc.nenc.socialseco.github.socialSECOAPI.ontologyModel.Skill;
import java.util.ArrayList;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author marci
 */
public class GetRecomendedDevelopers {

    public GetRecomendedDevelopers() {
    }

    public String getRecomendedDevelopers(String user, String repo) {
        OntologyManager oa = new OntologyManager("C:", "social-network.owl");
        Model model = oa.loadUserCluster(UserDAO.getInstance().loadUser(user).getId());
        String roleURI = user + "+" + repo + "-developer";
        Role role = getRole(roleURI, model);
        if (role == null) {
            return "[]";
        }
        ArrayList<Person> people = getPeople(model);
        if (people.size() == 0) {
            return "[]";
        }

        JSONArray jRequiredSkills = new JSONArray();
        JSONObject jRoleInfo = new JSONObject();
        for (Skill skill
                : role.getInterest()) {
            JSONObject jSkill = new JSONObject();
            jSkill.put("Skill", skill.getSelf().toString().substring(skill.getSelf().toString().indexOf('#')+1));
            jRequiredSkills.put(jSkill);
        }

        JSONObject jOutput = new JSONObject();
        jRoleInfo.put("RequiredSkill", jRequiredSkills);
        jRoleInfo.put("URI", role.getSelf().toString());
        jOutput.put("Role", jRoleInfo);

        JSONArray jHasAllSkills = new JSONArray();
        JSONArray jHasSomeSkills = new JSONArray();

        //ArrayList<Person> hasSomeInterests = new ArrayList<>();
        for (Person person : people) {
            ArrayList<Skill> extraSkills;
            ArrayList<Skill> lackingSkills;

            lackingSkills = new ArrayList<>();
            for (Skill skill : role.getInterest()) {
                lackingSkills.add(skill);
            }
            extraSkills = new ArrayList<>();

            setSkills(person, model);
            setInterests(person, model);

            for (Skill knwon : person.getKnown()) {

                //Verify skill
                boolean meetsCurrent = false;
                for (Skill required : lackingSkills) {
                    if (required.toString().equals(knwon.toString())) {
                        lackingSkills.remove(required);
                        meetsCurrent = true;
                        break;
                    }
                }
                if (!meetsCurrent) {
                    extraSkills.add(knwon);
                }

                /*
                //Verify possible interest
                boolean foundInterest = false;
                for (Skill skillKnown : person.getKnown()) {
                    if (skillKnown.toString().equals(requiredByRole.toString())) {
                        foundInterest = true;
                        matchInterestedSkills = true;
                        break;
                    }
                }
                if (!foundInterest) {
                    removeSkill(person, requiredByRole);
                }*/
            }

            if (lackingSkills.isEmpty()) {
                JSONArray jExtraSkills = new JSONArray();
                for (Skill skill : extraSkills) {
                    jExtraSkills.put(new JSONObject().put("URI", skill.getSelf().toString()));
                }
                JSONObject jPerson = new JSONObject().put("username", person.getSelf().toString().substring(person.getSelf().toString().indexOf('#')+1));
                jPerson.put("ExtraSkills", jExtraSkills);
                jHasAllSkills.put(jPerson);
            } else if (lackingSkills.size() < role.getInterest().size()) {
                JSONArray jLackingSkills = new JSONArray();
                for (Skill skill : lackingSkills) {
                    jLackingSkills.put(new JSONObject().put("URI", skill.getSelf().toString()));
                }
                JSONArray jExtraSkills = new JSONArray();
                for (Skill skill : extraSkills) {
                    jExtraSkills.put(new JSONObject().put("URI", skill.getSelf().toString()));
                }
                JSONObject jPerson = new JSONObject().put("username", person.getSelf().toString().substring(person.getSelf().toString().indexOf('#')+1));
                jPerson.put("ExtraSkills", jExtraSkills);
                jPerson.put("LackingSkills", jLackingSkills);
                jHasSomeSkills.put(jPerson);
            }
        }
        jOutput.put("HaveAllSkills", jHasAllSkills);
        jOutput.put("HaveSomeSkills", jHasSomeSkills);
        //jOutput.put("HaveSomeInterest", jHasSomeInterests);
        return jOutput.toString();
    }

    private Role getRole(String roleURI, Model model) {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX xml: <http://www.w3.org/XML/1998/namespace>\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX onto: <http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#>\n"
                + "\n"
                + "SELECT DISTINCT ?skill ?role\n"
                + "WHERE{{      ?role onto:needsSkill ?skill.\n"
                + "             ?role rdf:type onto:Role}}";

        Dataset dataset = DatasetFactory.create(model);

        Query consulta = QueryFactory.create(query);

        QueryExecution qexec = QueryExecutionFactory.create(consulta, dataset);
        ResultSet resultado = qexec.execSelect();
        if (!resultado.hasNext()) {
            return null;
        }
        Role role = null;
        while (resultado.hasNext()) {
            QuerySolution tuple = (QuerySolution) resultado.next();
            if (tuple.get("role").toString().contains(roleURI)) {
                System.err.println(tuple.get("role"));
                if (role == null) {
                    role = new Role(tuple.get("role"));
                }
                role.addInterest(new Skill(tuple.get("skill")));
            }
        }
        return role;
    }

    private ArrayList<Person> getPeople(Model model) {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX xml: <http://www.w3.org/XML/1998/namespace>\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX onto: <http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#>\n"
                + "\n"
                + "SELECT DISTINCT ?person\n"
                + "WHERE {{	?person rdf:type onto:Person}}";

        Dataset dataset = DatasetFactory.create(model);

        Query consulta = QueryFactory.create(query);

        QueryExecution qexec = QueryExecutionFactory.create(consulta, dataset);
        ResultSet resultado = qexec.execSelect();
        ArrayList<Person> people = new ArrayList<>();

        while (resultado.hasNext()) {
            QuerySolution tuple = (QuerySolution) resultado.next();
            people.add(new Person(tuple.get("person")));
        }
        return people;
    }

    private void setSkills(Person person, Model model) {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX xml: <http://www.w3.org/XML/1998/namespace>\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX onto: <http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#>\n"
                + "\n"
                + "SELECT DISTINCT ?skill\n"
                + "WHERE {{	<" + person.getSelf().toString() + "> onto:hasSkill ?skill}                 		UNION{?role onto:needsSkill ?skill.\n"
                + "                 			<" + person.getSelf().toString() + "> onto:hasRole ?role}"
                + "UNION {<" + person.getSelf().toString() + "> onto:collaborates ?software.?software onto:needsSkill ?skill}"
                + "UNION {<" + person.getSelf().toString() + "> onto:develops ?software.?software onto:needsSkill ?skill}"
                + "}";

        Dataset dataset = DatasetFactory.create(model);

        Query consulta = QueryFactory.create(query);

        QueryExecution qexec = QueryExecutionFactory.create(consulta, dataset);
        ResultSet resultado = qexec.execSelect();

        while (resultado.hasNext()) {
            QuerySolution tuple = (QuerySolution) resultado.next();
            person.addSkill(new Skill(tuple.get("skill")));
        }
    }

    private void setInterests(Person person, Model model) {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "                 PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "                 PREFIX xml: <http://www.w3.org/XML/1998/namespace>\n"
                + "                 PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "                 PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "                 PREFIX onto: <http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#>\n"
                + "                 \n"
                + "                 SELECT DISTINCT ?skill\n"
                + "                 WHERE {{\n"
                + "                 	<" + person.getSelf().toString() + "> onto:postsA ?post.\n"
                + "                 	?post onto:isPostedOn ?space.\n"
                + "                 	?space onto:isAboutSkill ?skill} \n"
                + "                 	UNION{\n"
                + "                 	<" + person.getSelf().toString() + "> onto:follows ?repo.\n"
                + "                 	?repo onto:requiresSkill ?skill}\n"
                + "                 	UNION{\n"
                + "                 	<" + person.getSelf().toString() + "> onto:follows ?repo.\n"
                + "                 	?repo onto:hosts ?software.\n"
                + "                 	?software onto:needsSkill ?skill}}";

        Dataset dataset = DatasetFactory.create(model);

        Query consulta = QueryFactory.create(query);

        QueryExecution qexec = QueryExecutionFactory.create(consulta, dataset);
        ResultSet resultado = qexec.execSelect();

        while (resultado.hasNext()) {
            QuerySolution tuple = (QuerySolution) resultado.next();
            person.addInterest(new Skill(tuple.get("skill")));
        }
    }

    private void removeInterest(Person person, Skill interest) {
        for (Skill skill : person.getInterest()) {
            if (skill.toString().equals(interest)) {
                person.getInterest().remove(skill);
            }
        }
    }

    private void removeSkill(Person person, Skill interest) {
        for (Skill skill : person.getKnown()) {
            if (skill.toString().equals(interest)) {
                person.getKnown().remove(skill);
            }
        }
    }
}
