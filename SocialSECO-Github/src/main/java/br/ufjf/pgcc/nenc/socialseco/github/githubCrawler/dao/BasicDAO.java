/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao;

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
public abstract class BasicDAO {
    
    protected Connection connect = null;
    protected Statement statement = null;
    protected PreparedStatement preparedStatement = null;
    protected ResultSet resultSet = null;
    
    protected void open() {
        try {
            Class.forName("com.mysql.jdbc.Driver"); 

            connect = java.sql.DriverManager.getConnection("jdbc:mysql://localhost/github_crawler", "root", "");
        } catch (Exception e) {
            Logger.getLogger(UserTypeDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    // You need to close the resultSet
    protected void close() {
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
