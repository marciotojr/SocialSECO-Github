/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marci
 */
public class UserFollowerDAO {

    private static UserFollowerDAO instance = null;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public static UserFollowerDAO getInstance() {
        if (instance == null) {
            instance = new UserFollowerDAO();
        }
        return instance;
    }

    private UserFollowerDAO() {

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
            return UserFollowerDAO.this.addFollower(follower.getId(), user.getId());
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

    public boolean updateUser(User user) {
        if (loadFollowers(user.getId()) != null) {
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

    private void open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connect = java.sql.DriverManager.getConnection("jdbc:mysql://localhost/github_crawler", "root", "");
        } catch (Exception e) {
            Logger.getLogger(UserFollowerDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            Logger.getLogger(UserFollowerDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
