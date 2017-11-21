/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util.ParameterAccess;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.UserDAO;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.NoRemainingRequestsException;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.PageForbiddenException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author marci
 */
public class UserService extends CommandService{

    int currentUser = 0;


    private JSONArray sendGet() throws Exception, PageForbiddenException {
        String urlAppendix = Integer.toString(currentUser);
        if (urlAppendix == null) {
            urlAppendix = "";
        } else {
            urlAppendix = "?since=" + urlAppendix;
        }
        String url = "https://api.github.com/users" + urlAppendix;

        return new JSONArray(getContent(url));

    }

    @Override
    public void increment() {
        ParameterAccess.getInstance().setParameter("last_user", Integer.toString(currentUser));
    }

    @Override
    public void initialize() {
        currentUser = Integer.parseInt(ParameterAccess.getInstance().getParameter("last_user"));
    }

    @Override
    public void getData() throws PageForbiddenException, NoRemainingRequestsException {
        try {
            JSONArray json = sendGet();
            for (int i = 0; i < json.length(); i++) {
                JSONObject userJSON = (JSONObject) json.get(i);

                int id = userJSON.getInt("id");
                String userName = userJSON.getString("login");
                String userType = userJSON.getString("type");
                //String name = userJSON.getString("name");
                UserDAO.getInstance().saveUser(new User(id, userName, userType));
                if (id > currentUser) {
                    currentUser = id;
                }
            }
            System.gc();
        } catch (PageForbiddenException | NoRemainingRequestsException e) {
            throw e;
        } catch (Exception e) {

        }
    }

}
