/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.ParameterAccess;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.RepositoryDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.NoRemainingRequestsException;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.PageForbiddenException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author marci
 */
public class RepositoryService extends CommandService{

    int currentRepo = 0;

    private JSONArray sendGet() throws Exception {

        String urlAppendix = Integer.toString(currentRepo);
        if (urlAppendix == null) {
            urlAppendix = "";
        } else {
            urlAppendix = "?since=" + urlAppendix;
        }
        String url = "https://api.github.com/repositories" + urlAppendix;

        return new JSONArray(getContent(url));

    }

    @Override
    public void increment() {
        ParameterAccess.getInstance().setParameter("last_repo", Integer.toString(currentRepo));
    }

    @Override
    public void initialize() {
        currentRepo = Integer.parseInt(ParameterAccess.getInstance().getParameter("last_repo"));
    }

    @Override
    public void getData() throws PageForbiddenException, NoRemainingRequestsException {
        try {
            JSONArray json = sendGet();
            for (int i = 0; i < json.length(); i++) {

                try {
                    JSONObject repoJSON = (JSONObject) json.get(i);
                    JSONObject ownerJSON = (JSONObject) repoJSON.get("owner");
                    int id = repoJSON.getInt("id");
                    int ownerId = ownerJSON.getInt("id");
                    String name = repoJSON.getString("full_name");
                    RepositoryDAO.getInstance().saveRepo(new Repository(id, ownerId, name));
                    if (id > currentRepo) {
                        currentRepo = id;
                    }
                } catch (ClassCastException e) {
                }
            }
            System.gc();
        } catch (PageForbiddenException | NoRemainingRequestsException e) {
            throw e;
        } catch (Exception e) {
        }
    }

}
