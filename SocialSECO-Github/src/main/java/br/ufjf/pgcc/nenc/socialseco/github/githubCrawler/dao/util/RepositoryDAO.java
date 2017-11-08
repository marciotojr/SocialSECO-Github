/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
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
public class RepositoryDAO {

    private static RepositoryDAO instance = null;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public static RepositoryDAO getInstance() {
        if (instance == null) {
            instance = new RepositoryDAO();
        }
        return instance;
    }

    private RepositoryDAO() {

    }

    public Repository loadRepo(int id) {
        open();
        Repository repo = null;
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT * from repository WHERE id = " + id);

            if (resultSet.next()) {
                repo = new Repository(resultSet.getInt("id"), resultSet.getInt("owner_id"), resultSet.getString("name"));

            }
        } catch (Exception e) {

        }
        close();
        return repo;
    }

    public boolean saveRepo(Repository repo) {
        if (loadRepo(repo.getId()) == null) {
            open();
            try {
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("insert into  repository values (?, ?,?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setInt(1, repo.getId());
                preparedStatement.setInt(2, repo.getOwner_id());
                preparedStatement.setString(3, repo.getName());
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

    public boolean updateRepo(Repository repo) {
        if (loadRepo(repo.getId()) != null) {
            open();
            try {
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("UPDATE repository SET owner_id=(?) , name=(?) WHERE id=(?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setInt(3, repo.getId());
                preparedStatement.setInt(1, repo.getOwner_id());
                preparedStatement.setString(2, repo.getName());
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
            Logger.getLogger(RepositoryDAO.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(RepositoryDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public int nextRepo(int id) {
        open();
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT MIN(id) FROM repository WHERE id > " + id);

            if (resultSet.next()) {
                return resultSet.getInt(1);

            }
        } catch (Exception e) {

        }
        close();
        return 0;
    }
}
