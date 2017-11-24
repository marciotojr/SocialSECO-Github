/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.githubAPIAccess;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.ParameterAccess;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.RepositoryDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.NoRemainingRequestsException;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.PageForbiddenException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author marci
 */
public class ColaboratorsService extends CommandService {

    private int currentRepo = 0;

    @Override
    public void increment() {
        ParameterAccess.getInstance().setParameter("last_repo_cont", Integer.toString(currentRepo));
        currentRepo = RepositoryDAO.getInstance().nextRepo(currentRepo);
    }

    @Override
    public void initialize() {
        String lastRepo = ParameterAccess.getInstance().getParameter("last_repo_cont");
        if (lastRepo == null) {
            currentRepo = RepositoryDAO.getInstance().nextRepo(new Integer(currentRepo));
        } else {
            currentRepo = new Integer(lastRepo);
        }
    }

    @Override
    public void getData() throws PageForbiddenException, NoRemainingRequestsException {
        Repository repo = RepositoryDAO.getInstance().loadRepo(currentRepo);
        String url = "https://api.github.com/repos/" + repo.getName() + "/contributors";

        JSONArray json;
        try {
            json = new JSONArray(getContent(url));
            for (int i = 0; i < json.length(); i++) {
                JSONObject userJSON = (JSONObject) json.get(i);

                int id = userJSON.getInt("id");
                String userName = userJSON.getString("login");
                String userType = userJSON.getString("type");
                //String name = userJSON.getString("name");
                RepositoryDAO.getInstance().addCollaborator(new User(id, userName, userType), repo);
            }
            System.gc();
        } catch (PageForbiddenException | NoRemainingRequestsException e) {
            throw e;
        } catch (Exception e) {

        }
    }

}
