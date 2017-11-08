/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.controller.RepositoriesCrawler;
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
public class ParameterAccess {
    
    private static ParameterAccess instance=null;

    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public static ParameterAccess getInstance(){
        if(instance==null){
            instance = new ParameterAccess();
        }
        return instance;
    }
    
    private ParameterAccess() {
    }
    
    
    public String getParameter(String paramName) {
        try {
            open();

            statement = connect.createStatement();

            if(paramName.equals("last_repo"))
            resultSet = statement
                    .executeQuery("SELECT MAX(id) FROM repository");
            else if(paramName.equals("last_user"))
                resultSet = statement
                    .executeQuery("SELECT MAX(id) FROM user");
            else if(paramName.equals("last_repo_cont"))
                resultSet = statement
                    .executeQuery("SELECT MAX(id_repository) FROM repo_colaborator");
            else if(paramName.equals("last_repo_star"))
                resultSet = statement
                    .executeQuery("SELECT MAX(id_repository) FROM repo_follower");
            else if(paramName.equals("last_user_foll"))
                resultSet = statement
                    .executeQuery("SELECT MAX(user_id) FROM user_follower");
            String paramValue = null;

            if (resultSet.next()) {
                paramValue = resultSet.getString(1);
                return paramValue;
            }else{
                return null;
            }
            
        } catch (Exception e) {
            Logger.getLogger(RepositoriesCrawler.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            close();
        }

    }  
    
     public void setParameter(String paramName, String paramValue) {}
    
    /*
    
    public String getParameter(String paramName) {
        try {
            open();

            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT * from parameter WHERE name LIKE '" + paramName + "'");
            String paramValue = null;

            while (resultSet.next()) {
                paramValue = resultSet.getString("value");
            }
            return paramValue;
        } catch (Exception e) {
            Logger.getLogger(RepositoriesCrawler.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            close();
        }

    }

    public void setParameter(String paramName, String paramValue) {
        try {
            String param = getParameter(paramName);
            if (param == null) {
                open();
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("insert into  parameter values (?, ?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setString(1, paramName);
                preparedStatement.setString(2, paramValue);
                preparedStatement.executeUpdate();

            } else {
                open();
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("UPDATE parameter SET value=(?) WHERE name LIKE (?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setString(2, paramName);
                preparedStatement.setString(1, paramValue);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            Logger.getLogger(RepositoriesCrawler.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            close();
        }
    }

*/

    private void open() {
        try{
        Class.forName("com.mysql.jdbc.Driver");

        connect = java.sql.DriverManager.getConnection("jdbc:mysql://localhost/github_crawler", "root", "");
        } catch (Exception e){
            Logger.getLogger(RepositoriesCrawler.class.getName()).log(Level.SEVERE, null, e);
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

        }
    }

}
