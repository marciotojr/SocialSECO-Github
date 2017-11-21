/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.dao;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.model.Language;
import java.util.Collection;

/**
 *
 * @author marci
 */
public class LanguageDAO extends BasicDAO{

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
        open();
        try {
            statement = connect.createStatement();

            resultSet = statement
                    .executeQuery("SELECT * from language WHERE name LIKE '" + name+"'");

            if (resultSet.next()) {
                return resultSet.getInt("id");

            }
        } catch (Exception e) {

        }
        close();
        return -1;
    }

    public int saveLanguage(String name) {
        int langCode = loadLanguage(name);
        if (langCode==-1) {
            open();
            try {
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
        }else{
            return langCode;
        }

        
    }
    
    
}
