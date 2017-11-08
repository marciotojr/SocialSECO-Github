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
public class UserTypeDAO {

    private static UserTypeDAO instance = null;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public static UserTypeDAO getInstance() {
        if (instance == null) {
            instance = new UserTypeDAO();
        }
        return instance;
    }

    private UserTypeDAO() {

    }

    public String loadType(int id) {
        open();
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT * from user_type WHERE id = " + id);

            if (resultSet.next()) {
                return resultSet.getString("type");

            }
        } catch (Exception e) {

        }
        close();
        return null;
    }
    
    public int loadType(String type) {
        open();
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT * from user_type WHERE type LIKE '" + type+"'");

            if (resultSet.next()) {
                return resultSet.getInt("id");

            }
        } catch (Exception e) {

        }
        close();
        return -1;
    }

    public int saveType(String type) {
        if (loadType(type)==-1) {
            open();
            try {
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("INSERT INTO user_type (id, type) VALUES (NULL, ?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setString(1, type);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                close();
                return -1;
            }
            close();
        }

        return loadType(type);
    }


    private void open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connect = java.sql.DriverManager.getConnection("jdbc:mysql://localhost/github_crawler", "root", "");
        } catch (Exception e) {
            Logger.getLogger(UserTypeDAO.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(UserTypeDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
