/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marci
 */
public class UserDAO extends BasicDAO{

    private static UserDAO instance = null;

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    private UserDAO() {

    }

    public User loadUser(int id) {
        open();
        User user = null;
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT * from user WHERE id = " + id);

            if (resultSet.next()) {
                user = new User(resultSet.getInt("id"),  resultSet.getString("username"), UserTypeDAO.getInstance().loadType(resultSet.getInt("user_type")));

            }
        } catch (Exception e) {

        }
        close();
        return user;
    }

    public boolean saveUser(User user) {
        if (loadUser(user.getId()) == null) {
            open();
            try {
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("insert into  user values (?, ?, ?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setInt(1, user.getId());
                preparedStatement.setString(2, user.getUserName());
                preparedStatement.setInt(3, UserTypeDAO.getInstance().saveType(user.getType()));
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                close();
                return false;
            }
            close();
            return true;

        }

        return false;
    }

    public boolean updateUser(User user) {
        if (loadUser(user.getId()) != null) {
            open();
            try {
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("UPDATE user  username=(?) WHERE id=(?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setInt(2, user.getId());
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                close();
                return false;
            }
            close();
            return true;
        }
        return false;
    }
    
    public int nextUser(int id) {
        open();
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT MIN(id) FROM user WHERE id > " + id);

            if (resultSet.next()) {
                return resultSet.getInt(1);

            }
        } catch (Exception e) {

        }
        close();
        return 0;
    }
    
    public List<User> loadFollowers(int id) {
        open();
        List<User> collaborators = new ArrayList<>();
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT follower_id from user_follower WHERE user_id = " + id);

            while (resultSet.next()) {
                collaborators.add(UserDAO.getInstance().loadUser(resultSet.getInt("follower_id")));

            }
        } catch (Exception e) {

        }
        close();
        return collaborators;
    }

    public boolean addFollower(User follower, User user) {
        if (follower != null && user != null) {
            return addFollower(follower.getId(), user.getId());
        } else {
            return false;
        }
    }

    private boolean addFollower(int followerId, int userId) {
            open();
            try {
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("insert into  user_follower values (?, ?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, followerId);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                close();
                return false;
            }
            close();
            return true;
      
    }
    
    public List<Repository> loadOwnedRepositories(int id) {
        open();
        List<Repository> ownedRepos = new ArrayList<>();
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT id from repository WHERE owner_id = " + id);

            while (resultSet.next()) {
                ownedRepos.add(RepositoryDAO.getInstance().loadRepo(resultSet.getInt("id")));

            }
        } catch (Exception e) {

        }
        close();
        return ownedRepos;
    }
    
    public List<Repository> loadCollaborationRepositories(int id) {
        open();
        List<Repository> collaborationsRepos = new ArrayList<>();
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT id_repository from repo_colaborator WHERE id_user = " + id);

            while (resultSet.next()) {
                collaborationsRepos.add(RepositoryDAO.getInstance().loadRepo(resultSet.getInt("id_repository")));

            }
        } catch (Exception e) {

        }
        close();
        return collaborationsRepos;
    }
}