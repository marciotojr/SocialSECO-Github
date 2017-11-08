/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao.util;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
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
public class ColaboratorDAO {

    private static ColaboratorDAO instance = null;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public static ColaboratorDAO getInstance() {
        if (instance == null) {
            instance = new ColaboratorDAO();
        }
        return instance;
    }

    private ColaboratorDAO() {

    }

    public List<User> loadCollaborator(int id) {
        open();
        List<User> collaborators = new ArrayList<>();
        User user = null;
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT id_user from repo_colaborator WHERE id_repository = " + id);

            while (resultSet.next()) {
                collaborators.add(UserDAO.getInstance().loadUser(resultSet.getInt("id_user")));

            }
        } catch (Exception e) {

        }
        close();
        return collaborators;
    }

    public boolean addCollaborator(User user, Repository repo) {
        if (user != null && repo != null) {
            return addCollaborator(user.getId(), repo.getId());
        } else {
            return false;
        }
    }

    private boolean addCollaborator(int userId, int repositoryId) {
            open();
            try {
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("insert into  repo_colaborator values (?, ?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setInt(1, repositoryId);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                close();
                return false;
            }
            close();
            return true;
      
    }

    private void open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connect = java.sql.DriverManager.getConnection("jdbc:mysql://localhost/github_crawler", "root", "");
        } catch (Exception e) {
            Logger.getLogger(ColaboratorDAO.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(ColaboratorDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
