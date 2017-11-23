/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.controler;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.LanguageDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.RepositoryDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.UserDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.ontology.OntologyAccess;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Language;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author marci
 */
public class OntologyManager {

    private static Model instance;
    private static HashMap<Integer, User> users = new HashMap<>();
    private static HashMap<Integer, Repository> repos = new HashMap<>();
    private static HashMap<Integer, Language> langs = new HashMap<>();

    private static String baseURI = "http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#";

    private OntModel ontModel;

    public OntologyManager(String path, String fileName) {
        OntologyAccess oa = new OntologyAccess(path, fileName);
        ontModel = oa.getOntModel();
    }

    private void addUser(User user) {
        if (!users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            createUser(user);
        }
    }

    private void addRepository(Repository repo) {
        if (!repos.containsKey(repo.getId())) {
            repos.put(repo.getId(), repo);
            createRepository(repo);
        }
    }

    private void addLanguage(Language lang) {
        if (!langs.containsKey(lang.getId())) {
            langs.put(lang.getId(), lang);
        }
    }

    private void addFollower(User currentUser, User follower) {
        addUser(follower);
        addUser(currentUser);
    }

    private void addStargazer(Repository currentRepo, User follower) {
        addUser(follower);
        addRepository(currentRepo);
    }

    private void addOwnedRepo(User currentUser, Repository repo) {
        addUser(currentUser);
        addRepository(repo);
    }

    private void addCollaborationRepo(User currentUser, Repository repo) {
        addUser(currentUser);
        addRepository(repo);
    }

    private void addCollaborator(Repository currentRepo, User collaborator) {
        addRepository(currentRepo);
        addUser(collaborator);
    }

    public static Model getInstance() {
        if (instance == null) {
            Model baseModel = ModelFactory.createDefaultModel();
            Reasoner resoner = ReasonerRegistry.getOWLReasoner();
            //Model model = ModelFactory.createDefaultModel();
            InputStream input = FileManager.get().open("C:\\\\social-network.owl");
            if (input == null) {
                throw new IllegalArgumentException(
                        "File: " + "social-network.owl" + " not found");
            }

            baseModel.read(input, null);
            instance = ModelFactory.createInfModel(resoner, baseModel);
        }
        return instance;
    }

    public void loadCompanyCluster(int companyID) {
        User company = UserDAO.getInstance().loadUser(companyID);
        Queue<User> userQueue = new LinkedList<>();
        Queue<User> auxUserQueue = new LinkedList<>();
        Queue<Repository> repoQueue = new LinkedList<>();
        userQueue.add(company);
        int iteractions = 0;
        while ((!userQueue.isEmpty() || !repoQueue.isEmpty()) && iteractions < 3) {
            iteractions++;
            for (User currentUser = userQueue.poll(); currentUser != null; currentUser = userQueue.poll()) {
                addUser(currentUser);

                //load followers
                List<User> followers = UserDAO.getInstance().loadFollowers(currentUser.getId());
                for (User follower : followers) {
                    addFollower(currentUser, follower);
                    if (!users.containsKey(follower.getId())) {
                        auxUserQueue.add(follower);
                    }
                }

                List<Repository> ownedRepos = UserDAO.getInstance().loadOwnedRepositories(currentUser.getId());
                for (Repository repo : ownedRepos) {
                    addOwnedRepo(currentUser, repo);
                    if (!repos.containsKey(repo.getId())) {
                        repoQueue.add(repo);
                    }
                }

                List<Repository> collaborationRepos = UserDAO.getInstance().loadCollaborationRepositories(currentUser.getId());
                for (Repository repo : collaborationRepos) {
                    addCollaborationRepo(currentUser, repo);
                    if (!repos.containsKey(repo.getId())) {
                        repoQueue.add(repo);
                    }
                }
            }
            for (User user : auxUserQueue) {
                userQueue.add(user);
            }
            for (Repository currentRepo = repoQueue.poll(); currentRepo != null; currentRepo = repoQueue.poll()) {
//load followers
                addRepository(currentRepo);
                List<User> stargazers = RepositoryDAO.getInstance().loadFollower(currentRepo.getId());
                for (User stargazer : stargazers) {
                    addStargazer(currentRepo, stargazer);
                    if (!users.containsKey(stargazer.getId())) {
                        userQueue.add(stargazer);
                    }
                }
                List<User> collaborators = RepositoryDAO.getInstance().loadCollaborator(currentRepo.getId());
                for (User collaborator : collaborators) {
                    addCollaborator(currentRepo, collaborator);
                    if (!users.containsKey(collaborator.getId())) {
                        userQueue.add(collaborator);
                    }
                }
            }
        }
    }

    public Individual createRepository(Repository repo) {
        Individual repository = ontModel.getIndividual(baseURI + repo.getURISuffix());
        if (repository == null) {
            Resource ontClassRepo = ontModel.getResource(baseURI + "Repository"); //a instância a ser criada pertence à classe Result
            Resource ontClassSoftware = ontModel.getResource(baseURI + "Solution"); //a instância a ser criada pertence à classe Result
            Individual software = ontModel.createIndividual(baseURI + repo.getURISuffix()+"+software", ontClassSoftware);
            repository = ontModel.createIndividual(baseURI + repo.getURISuffix(), ontClassRepo);

            ObjectProperty hosts = ontModel.getObjectProperty(baseURI + "hosts");
            
            repository.addProperty(hosts, software);
            
            for(Language lang: LanguageDAO.getInstance().getRepositoryLanguages(repo)){
                Resource ontClassLang = ontModel.getResource(baseURI + "Skill");
                Individual language = ontModel.createIndividual(baseURI + lang.getURISuffix(),ontClassLang);
                ObjectProperty needsSkill = ontModel.getObjectProperty(baseURI + "needsSkill");
                software.addProperty(needsSkill, language);
            }
            
            User owner = UserDAO.getInstance().loadUser(repo.getOwner_id());
            if(owner!=null && owner.getType().equals("Organization")){
                Individual ontOwner = createUser(owner);
                ontOwner.addOntClass(ontModel.getResource(baseURI + "Institution"));
                ObjectProperty develops = ontModel.getObjectProperty(baseURI + "develops");
                ontOwner.addProperty(develops, software);
               
            }
            
            
        }
        return repository;
    }

    public Individual createUser(User user) {
        Individual ind = ontModel.getIndividual(baseURI + user.getUserName());
        if (ind == null) {
            Resource ontClass = ontModel.getResource(baseURI + "Person"); //a instância a ser criada pertence à classe Result

            return ontModel.createIndividual(baseURI + user.getUserName(), ontClass);
        } else {
            return ind;
        }
        /*ObjectProperty property = ontModel.getObjectProperty("");
        DatatypeProperty rain_mm = ontModel.createDatatypeProperty(baseURI + "hasSimpleResult");//cria-se uma Data Property para a instância (de nome hasSimpleResult)
        Individual ind = ontModel.getIndividual(baseURI + "Rain" + hour);

        ind.addProperty (rain_mm, rainData);//rain_mm se refere à Data Property hasSimpleResult

        /*
        String intensity = data.getRainIntensity(rainFloat);

        DatatypeProperty rainIntensity = ontModel.createDatatypeProperty(baseURI + "hasRainIntensity");

        ind.addProperty (rainIntensity, intensity);
         */
    }

    public void saveOntology() {
        OutputStream out;
        try {
            out = new FileOutputStream("ssn_v1.rdf");
            ontModel.write(out, "RDF/XML-ABBREV");
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OntologyManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OntologyManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
