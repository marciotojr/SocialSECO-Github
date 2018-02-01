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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;

/**
 *
 * @author marci
 */
public class OntologyManager {

    private static Model instance;
    private HashMap<Integer, User> users = new HashMap<>();
    private HashMap<Integer, Repository> repos = new HashMap<>();
    private HashMap<Integer, Language> langs = new HashMap<>();
    private Individual platform;
    private ObjectProperty builtForPlatform;

    private static String baseURI = "http://www.semanticweb.org/marciojúnior/ontologies/2017/6/developer_s-social-network#";

    private OntModel ontModel;
        
    public OntologyManager(String path, String fileName) {
        OntologyAccess oa = new OntologyAccess(path, fileName);
        ontModel = oa.getOntModel();

        builtForPlatform = ontModel.getObjectProperty(baseURI + "builtForPlatform");

        platform = ontModel.getIndividual(baseURI + "github");

    }

    private Individual addUser(User user) {
        if (!users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
        return createUser(user);
    }

    private Individual addRepository(Repository repo) {
        if (!repos.containsKey(repo.getId())) {
            repos.put(repo.getId(), repo);
        }
        return createRepository(repo);
    }

    private Individual addLanguage(Language lang) {
        if (!langs.containsKey(lang.getId())) {
            langs.put(lang.getId(), lang);
        }
        return createLanguage(lang);
    }

    private void addFollower(User currentUser, User follower) {
        Individual indFollower = createUser(follower);
        Individual indUser = createUser(currentUser);
        if (indFollower != null && indUser != null) {
            ObjectProperty follows = ontModel.getObjectProperty(baseURI + "follows");
            indFollower.addProperty(follows, indUser);
        }
    }

    private void addStargazer(Repository currentRepo, User follower) {
        Individual indFollower = createUser(follower);
        Individual indRepo = createRepository(currentRepo);
        if (indFollower != null && indRepo != null) {
            ObjectProperty follows = ontModel.getObjectProperty(baseURI + "follows");
            indFollower.addProperty(follows, indRepo);
        }
    }

    private void addOwnedRepo(User currentUser, Repository repo) {
        createUser(currentUser);
        createRepository(repo);

    }

    private void addCollaborationRepo(User currentUser, Repository repo) {
        Individual indUser = createUser(currentUser);
        Individual indRepo = createRepository(repo);
        if (indUser != null && indRepo != null) {
            ObjectProperty colaborates = ontModel.getObjectProperty(baseURI + "collaborates");
            indUser.addProperty(colaborates, indRepo);

            Individual software = ontModel.getIndividual(baseURI + repo.getURISuffix() + "+software");
            indUser.addProperty(colaborates, software);

            software.addProperty(builtForPlatform, platform);
        }

    }

    private void addCollaborator(Repository currentRepo, User collaborator) {
        Individual indRepo = createRepository(currentRepo);
        Individual indUser = createUser(collaborator);

        if (indUser != null && indRepo != null) {
            ObjectProperty colaborates = ontModel.getObjectProperty(baseURI + "collaborates");
            indUser.addProperty(colaborates, indRepo);

            Individual software = ontModel.getIndividual(baseURI + currentRepo.getURISuffix());
            indUser.addProperty(colaborates, software);

            software.addProperty(builtForPlatform, platform);
        }
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

    public OntModel loadUserCluster(int companyID) {
        User company = UserDAO.getInstance().loadUser(companyID);
        Queue<User> userQueue = new LinkedList<>();
        Queue<User> auxUserQueue = new LinkedList<>();
        Queue<Repository> repoQueue = new LinkedList<>();
        userQueue.add(company);
        int iteractions = 0;
        while ((!userQueue.isEmpty() || !repoQueue.isEmpty()) && iteractions < 2) {
            iteractions++;
            for (User currentUser = userQueue.poll(); currentUser != null; currentUser = userQueue.poll()) {
                System.out.println("Usuários: " + userQueue.size() + "/" + users.size() + " Repos: " + repoQueue.size() + "/" + repos.size());
                addUser(currentUser);

                //load followers
                /*
                List<User> followers = UserDAO.getInstance().loadFollowers(currentUser.getId());
                for (User follower : followers) {
                    addFollower(currentUser, follower);
                    if (!users.containsKey(follower.getId())) {
                        users.put(follower.getId(), follower);
                        auxUserQueue.add(follower);
                    }
                }*/
                List<Repository> ownedRepos = UserDAO.getInstance().loadOwnedRepositories(currentUser.getId());
                for (Repository repo : ownedRepos) {
                    addOwnedRepo(currentUser, repo);
                    if (!repos.containsKey(repo.getId())) {
                        repos.put(repo.getId(), repo);
                        repoQueue.add(repo);
                    }
                }

                List<Repository> collaborationRepos = UserDAO.getInstance().loadCollaborationRepositories(currentUser.getId());
                for (Repository repo : collaborationRepos) {
                    addCollaborationRepo(currentUser, repo);
                    if (!repos.containsKey(repo.getId())) {
                        repos.put(repo.getId(), repo);
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
                System.out.println("Usuários: " + userQueue.size() + "/" + users.size() + " Repos: " + repoQueue.size() + "/" + repos.size());
                /*List<User> stargazers = RepositoryDAO.getInstance().loadFollower(currentRepo.getId());
                for (User stargazer : stargazers) {
                    addStargazer(currentRepo, stargazer);
                    if (!users.containsKey(stargazer.getId())) {
                        users.put(stargazer.getId(), stargazer);
                        userQueue.add(stargazer);
                    }
                }*/
                List<User> collaborators = RepositoryDAO.getInstance().loadCollaborator(currentRepo.getId());
                for (User collaborator : collaborators) {
                    addCollaborator(currentRepo, collaborator);
                    if (!users.containsKey(collaborator.getId())) {
                        users.put(collaborator.getId(), collaborator);
                        userQueue.add(collaborator);
                    }
                }
            }
        }
        saveOntology("temp.owl");
        return ontModel;
    }

    private Individual createRepository(Repository repo) {
        if (repo == null) {
            return null;
        }
        Individual repository = ontModel.getIndividual(baseURI + repo.getURISuffix());
        if (repository == null) {
            Resource ontClassRepo = ontModel.getResource(baseURI + "Repository"); //a instância a ser criada pertence à classe Result
            Resource ontClassSoftware = ontModel.getResource(baseURI + "Solution"); //a instância a ser criada pertence à classe Result
            Individual software = ontModel.createIndividual(baseURI + repo.getURISuffix() + "+software", ontClassSoftware);
            repository = ontModel.createIndividual(baseURI + repo.getURISuffix(), ontClassRepo);

            ObjectProperty hosts = ontModel.getObjectProperty(baseURI + "hosts");

            software.addProperty(builtForPlatform, platform);
            repository.addProperty(hosts, software);

            Individual role = null;
            Resource ontClassRole = ontModel.getResource(baseURI + "Role"); //a instância a ser criada pertence à classe Result

            for (Language lang : LanguageDAO.getInstance().getRepositoryLanguages(repo)) {
                if (role == null) {
                    role = ontModel.createIndividual(baseURI + repo.getURISuffix() + "-developer", ontClassRole);
                }
                ObjectProperty needsSkill = ontModel.getObjectProperty(baseURI + "needsSkill");
                software.addProperty(needsSkill, createLanguage(lang));
                role.addProperty(needsSkill, createLanguage(lang));
            }

            User owner = UserDAO.getInstance().loadUser(repo.getOwner_id());
            Individual ontOwner = createUser(owner);
            if (ontOwner != null && owner != null && owner.getType() != null) {
                if (owner.getType().equals("Organization")) {

                    ontOwner.addOntClass(ontModel.getResource(baseURI + "Institution"));

                    ObjectProperty property = ontModel.getObjectProperty(baseURI + "isHostedIn");
                    software.addProperty(property, repository);
                }
                ObjectProperty develops = ontModel.getObjectProperty(baseURI + "develops");
                ontOwner.addProperty(develops, software);
            }
        }
        return repository;
    }

    private Individual createLanguage(Language lang) {
        if (lang == null) {
            return null;
        }
        Individual language = null;
        try {
            language = ontModel.getIndividual(baseURI + lang.getURISuffix());
        } catch (Exception e) {

        }
        if (lang != null) {
            Resource ontClassLang = ontModel.getResource(baseURI + "Skill");
            language = ontModel.createIndividual(baseURI + lang.getURISuffix(), ontClassLang);
        }
        return language;
    }

    private Individual createUser(User user) {
        if (user == null) {
            return null;
        }
        Individual ind = ontModel.getIndividual(baseURI + user.getUserName());
        if (ind == null) {
            Resource ontClass;
            if (user.getType() != null && user.getType().equals("Organization")) {
                ontClass = ontModel.getResource(baseURI + "Institution"); //a instância a ser criada pertence à classe Result
            } else {
                ontClass = ontModel.getResource(baseURI + "Person"); //a instância a ser criada pertence à classe Result
            }
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

    public void saveOntology(String fileName) {
        OutputStream out;
        try {
            Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
            reasoner = reasoner.bindSchema(ontModel);
            OntModelSpec ontModelSpec = OntModelSpec.OWL_DL_MEM_TRANS_INF;
            ontModelSpec.setReasoner(reasoner);
            out = new FileOutputStream(fileName);
            ontModel.write(out, "RDF/XML-ABBREV");
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OntologyManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OntologyManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public OntModel getModel(){
        return ontModel;
    }
}
