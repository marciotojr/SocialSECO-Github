/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.controller.RepositoriesCrawler;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.ParameterAccess;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.RepositoryDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import br.ufjf.pgcc.nenc.socialseco.github.util.SaveToFile;
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
public class RepositoryService implements CommandService, Runnable {

    private final String USER_AGENT = "Mozilla/5.0";

    @Override
    public void crawl() {
        while (true) {
            try {
                JSONArray json = sendGet();
                //JSONArray json = new JSONArray(SaveToFile.load("output.json"));
                int idMax = -1;
                for (int i = 0; i < json.length(); i++) {
                    JSONObject repoJSON = (JSONObject) json.get(i);
                    JSONObject ownerJSON = (JSONObject) repoJSON.get("owner");

                    int id = repoJSON.getInt("id");
                    int ownerId = ownerJSON.getInt("id");
                    String name = repoJSON.getString("full_name");
                    RepositoryDAO.getInstance().saveRepo(new Repository(id, ownerId, name));
                    if (id > idMax) {
                        idMax = id;
                    }
                }
                System.gc();
                ParameterAccess.getInstance().setParameter("last_repo", Integer.toString(idMax));

            } catch (Exception ex) {
                Logger.getLogger(RepositoriesCrawler.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    Thread.sleep(1000*60*15);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(RepositoryService.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    private JSONArray sendGet() throws Exception {

        String urlAppendix = ParameterAccess.getInstance().getParameter("last_repo");
        if (urlAppendix == null) {
            urlAppendix = "";
        } else {
            urlAppendix = "?since=" + urlAppendix;
        }
        String url = "https://api.github.com/repositories" + urlAppendix;

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
        System.out.println("Tentativas restantes : "+con.getHeaderField("X-RateLimit-Remaining"));

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
