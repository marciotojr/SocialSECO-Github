/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Language;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Repository;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author marci
 */
public class LanguageDAO extends BasicDAO {

    private static LanguageDAO instance = null;

    private LanguageDAO() {
    }

    public static LanguageDAO getInstance() {
        if (instance == null) {
            instance = new LanguageDAO();
        }
        return instance;
    }

    public int loadLanguage(String name) {
        
        try {
            open();
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT * from language WHERE name LIKE '" + name + "'");

            if (resultSet.next()) {
                int value = resultSet.getInt("id");;
                close();
                return value;

            }
        } catch (Exception e) {
close();
        }
        close();
        return -1;
    }

    public int saveLanguage(String name) {
        int langCode = loadLanguage(name);
        if (langCode == -1) {
            try {
                open();
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("INSERT INTO language (id, name) VALUES (NULL, ?)");
                // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
                // Parameters start with 1
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                close();
                return -1;
            }
            close();
            return loadLanguage(name);
        } else {
            close();
            return langCode;
        }

    }

    public String loadLanguage(int id) {
        try {
            open();
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT * from language WHERE id =" + id);

            if (resultSet.next()) {
                String returnValue = resultSet.getString(2);
                close();
                return returnValue;

            }
        } catch (Exception e) {
            close();
        }
        close();
        return null;
    }

    public Language[] getRepositoryLanguages(Repository repo) {
        ArrayList<Language> list = new ArrayList<>();
        try {
            open();
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT l.name FROM repo_language rl \n"
                            + "INNER JOIN repository r ON rl.id_repo=r.id\n"
                            + "INNER JOIN language l ON rl.id_lang=l.id\n"
                            + "WHERE r.id=" + repo.getId());

            while (resultSet.next()) {
                list.add(new Language(resultSet.getString(1)));
            }
        } catch (Exception e) {
close();
        }
        close();
        Language[] arr = new Language[list.size()];
        list.toArray(arr);
        return arr;
    }

}
