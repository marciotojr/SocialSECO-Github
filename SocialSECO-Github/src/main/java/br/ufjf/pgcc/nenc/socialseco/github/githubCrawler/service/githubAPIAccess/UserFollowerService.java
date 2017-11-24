/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.githubAPIAccess;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.githubAPIAccess.CommandService;
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
public class UserFollowerService extends CommandService{

    private int currentUser = 0;


    private JSONArray sendGet() throws Exception {

        User user = UserDAO.getInstance().loadUser(currentUser);
        String url = "https://api.github.com/users/" + user.getUserName() + "/followers";

        return new JSONArray(getContent(url));

    }

    @Override
    public void increment() {
        ParameterAccess.getInstance().setParameter("last_user_star", Integer.toString(currentUser));
        currentUser = UserDAO.getInstance().nextUser(currentUser);
    }

    @Override
    public void initialize() {
        String lastUser = ParameterAccess.getInstance().getParameter("last_user_star");
        if (lastUser == null) {
            currentUser = UserDAO.getInstance().nextUser(currentUser);
        } else {
            currentUser = new Integer(lastUser);
        }
    }

    @Override
    public void getData() throws PageForbiddenException, NoRemainingRequestsException {
        User user = UserDAO.getInstance().loadUser(currentUser);
        try {
            JSONArray json = sendGet();
            for (int i = 0; i < json.length(); i++) {
                JSONObject userJSON = (JSONObject) json.get(i);

                int id = userJSON.getInt("id");
                String userName = userJSON.getString("login");
                String userType = userJSON.getString("type");
                //String name = userJSON.getString("name");
                UserDAO.getInstance().addFollower(new User(id, userName, userType), user);
            }
            System.gc();
        } catch (PageForbiddenException | NoRemainingRequestsException e) {
            throw e;
        } catch (Exception e) {

        }
    }

}
