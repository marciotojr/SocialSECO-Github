/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marci
 */
public class UserDAO {

    private static UserDAO instance = null;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

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

    private void open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connect = java.sql.DriverManager.getConnection("jdbc:mysql://localhost/github_crawler", "root", "");
        } catch (Exception e) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, e);
        }
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
}
