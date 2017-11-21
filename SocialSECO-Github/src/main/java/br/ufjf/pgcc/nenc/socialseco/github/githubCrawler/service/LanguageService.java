/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.LanguageDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.RepositoryDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.LanguageDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.ParameterAccess;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.NoRemainingRequestsException;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.PageForbiddenException;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author marci
 */
public class LanguageService extends CommandService {

    private int currentRepo = 0;

    private JSONArray sendGet() throws Exception, PageForbiddenException {

        Repository repo = RepositoryDAO.getInstance().loadRepo(currentRepo);
        String url = "https://api.github.com/repos/" + repo.getName() + "/languages";

        JSONObject obj = new JSONObject(getContent(url));
        return new JSONArray().put(0, obj);

    }

    @Override
    public void increment() {
        ParameterAccess.getInstance().setParameter("last_repo_lang", Integer.toString(currentRepo));
        currentRepo = RepositoryDAO.getInstance().nextRepo(currentRepo);
    }

    @Override
    public void initialize() {
        String lastRepo = ParameterAccess.getInstance().getParameter("last_repo_lang");
        if (lastRepo == null) {
            currentRepo = RepositoryDAO.getInstance().nextRepo(currentRepo);
        } else {
            currentRepo = new Integer(lastRepo);
        }
    }

    @Override
    public void getData() throws PageForbiddenException, NoRemainingRequestsException {
        try {
            JSONArray json = sendGet();
            for (int i = 0; i < json.length(); i++) {
                JSONObject repoJson = (JSONObject) json.get(i);
                Collection<String> list = repoJson.keySet();
                for (String key : list) {
                    LanguageDAO.getInstance().saveLanguage(key);
                    int langID = LanguageDAO.getInstance().loadLanguage(key);
                    RepositoryDAO.getInstance().addLanguage(currentRepo, langID, repoJson.getInt(key));
                }
            }
            System.gc();
        } catch (PageForbiddenException | NoRemainingRequestsException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("deu ruim");
        }

    }

}
