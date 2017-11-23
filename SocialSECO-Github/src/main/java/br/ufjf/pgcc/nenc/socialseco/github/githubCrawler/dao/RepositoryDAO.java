/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Language;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marci
 */
public class RepositoryDAO extends BasicDAO {

    private static RepositoryDAO instance = null;

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
    
    public Repository loadRepo(String name) {
        open();
        Repository repo = null;
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT * from repository WHERE name LIKE '" + name+"'");

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

    public int nextRepo(int currentRepo) {
        open();
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT MIN(id) from repository WHERE id > " + currentRepo);

            if (resultSet.next()) {
                return resultSet.getInt(1);

            } else {
                return nextRepo(-1);
            }
        } catch (Exception e) {
            return nextRepo(-1);
        }
    }

    public boolean addLanguage(Repository repo, Language lang, int loc) {
        return addLanguage(repo.getId(), lang.getId(), loc);
    }

    public boolean addLanguage(int repo, int lang, int loc) {
        open();
        try {
            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("insert into  repo_language values (?, ?,?)");
            // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
            // Parameters start with 1
            preparedStatement.setInt(1, repo);
            preparedStatement.setInt(2, lang);
            preparedStatement.setInt(3, loc);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            close();
            return false;
        }
        close();
        return true;

    }
    
    public List<User> loadCollaborator(int id) {
        open();
        List<User> collaborators = new ArrayList<>();
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
    
    public List<User> loadFollower(int id) {
        open();
        List<User> collaborators = new ArrayList<>();
        User user = null;
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT id_user from repo_follower WHERE id_repository = " + id);

            while (resultSet.next()) {
                collaborators.add(UserDAO.getInstance().loadUser(resultSet.getInt("id_user")));

            }
        } catch (Exception e) {

        }
        close();
        return collaborators;
    }

    public boolean addFollower(User user, Repository repo) {
        if (user != null && repo != null) {
            return addFollower(user.getId(), repo.getId());
        } else {
            return false;
        }
    }

    private boolean addFollower(int userId, int repositoryId) {
            open();
            try {
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("insert into  repo_follower values (?, ?)");
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
    


}
