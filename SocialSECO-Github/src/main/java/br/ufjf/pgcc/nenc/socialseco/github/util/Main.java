package br.ufjf.pgcc.nenc.socialseco.github.util;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.controler.OntologyManager;
import br.ufjf.pgcc.nenc.socialseco.github.SocialSECOAPI.GetRecomendedDevelopers;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.githubAPIAccess.ColaboratorsService;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.githubAPIAccess.LanguageService;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.githubAPIAccess.RepositoryFollowerService;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.githubAPIAccess.RepositoryService;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.githubAPIAccess.UserFollowerService;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.githubAPIAccess.UserService;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marci
 */
public class Main {

    public static void main(String[] argsv) {
        
        try {
            UserService us = new UserService();
            RepositoryService rs = new RepositoryService();
            ColaboratorsService cs = new ColaboratorsService();
            RepositoryFollowerService rfs = new RepositoryFollowerService();
            UserFollowerService ufs = new UserFollowerService();
            LanguageService ls = new LanguageService();
            Thread userThread  = new Thread(us, "users");
            Thread repoThread = new Thread(rs, "repos");
            Thread collaboratorsThread = new Thread(cs, "colab");
            Thread stargazersThread = new Thread(rfs, "stargazers");
            Thread followersThread = new Thread(ufs, "follower");
            Thread languagesThread = new Thread(ls, "languages");
            /*OntologyManager om = new OntologyManager("C:", "social-network.owl");
            GetRecomendedDevelopers gi = new GetRecomendedDevelopers();
            System.out.println(gi.getRecomendedDevelopers("railslove","railslove_deploy"));/*
            om.createUser(new User(0, "marcio", null));
            om.createRepository(new Repository(27, 0, "marcio/eseco"));
            om.createRepository(new Repository(28, 0, "marcio/github"));
            om.createRepository(new Repository(273, 81, "engineyard/eycap"));*/
            /*om.loadCompanyCluster(81);
            om.saveOntology();*/
            languagesThread.start();
            collaboratorsThread.start();
            followersThread.start();
            stargazersThread.start();
            userThread.start();
            repoThread.start();/*/
            /*
            ParameterAccess pa = ParameterAccess.getInstance();
            System.out.println(pa.getParameter("exemplo"));
            pa.setParameter("exemplo", "dazzle");
            pa.setParameter("novo", "novo");*/
            /*RepositoryCrawler rc = new RepositoryCrawler();
            rc.crawl();/*//*
            BufferedReader br;
            try {
            br = new BufferedReader(new FileReader("output.json"));
            try {
            
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
            }
            String everything = sb.toString();
            readJson(everything);
            } catch (Exception e) {

            } finally {
            br.close();
            }
            } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }/* */
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /* */
   
}
