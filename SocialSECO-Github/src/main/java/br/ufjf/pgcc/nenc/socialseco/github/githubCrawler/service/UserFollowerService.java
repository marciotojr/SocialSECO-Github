/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.controller.RepositoriesCrawler;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.ParameterAccess;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.RepositoryDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.RepositoryFollowerDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.UserDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.UserFollowerDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author marci
 */
public class UserFollowerService implements CommandService, Runnable {

    private final String USER_AGENT = "Mozilla/5.0";
    private int currentUser = 0;

    @Override
    public void crawl() {
        String lastUser = ParameterAccess.getInstance().getParameter("last_user_star");
        if (lastUser == null) {
            currentUser = UserDAO.getInstance().nextUser(currentUser);
        } else {
            currentUser = UserDAO.getInstance().nextUser(new Integer(lastUser));
        }
        while (true) {
            User user = UserDAO.getInstance().loadUser(currentUser);
            try {
                JSONArray json = sendGet();
                for (int i = 0; i < json.length(); i++) {
                    JSONObject userJSON = (JSONObject) json.get(i);

                    int id = userJSON.getInt("id");
                    String userName = userJSON.getString("login");
                    String userType = userJSON.getString("type");
                    //String name = userJSON.getString("name");
                    UserFollowerDAO.getInstance().addFollower(new User(id, userName, userType), user);
                }
                System.gc();
                ParameterAccess.getInstance().setParameter("last_user_star", Integer.toString(currentUser));
                currentUser = RepositoryDAO.getInstance().nextRepo(currentUser);

            } catch (Exception ex) {
                Logger.getLogger(RepositoriesCrawler.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    Thread.sleep(1000 * 60 * 15);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(UserFollowerService.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }

        }
    }

    private JSONArray sendGet() throws Exception {

        Repository repo = RepositoryDAO.getInstance().loadRepo(currentUser);
        String url = "https://api.github.com/repos/" + repo.getName() + "/stargazers";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Authorization", "token a2e6c2553b07188888bdd4ef57b96327c5a609ff");

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        System.out.println("Tentativas restantes : " + con.getHeaderField("X-RateLimit-Remaining"));

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        con.disconnect();
        return new JSONArray(response.toString());

    }

    @Override
    public void run() {
        crawl();
    }

}
