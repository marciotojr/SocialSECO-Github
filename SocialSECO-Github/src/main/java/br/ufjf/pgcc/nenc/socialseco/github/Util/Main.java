package br.ufjf.pgcc.nenc.socialseco.github.Util;

import br.ufjf.pgcc.nenc.socialseco.github.GithubCrawler.RepositoriesCrawler;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marci
 */
public class Main {

    public static void main(String[] argsv) {
        /*RepositoriesCrawler rc = new RepositoriesCrawler();
        rc.executeGETMethod();*/
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("output.json"));
            try {

                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                readJson(everything);
            } catch (Exception e) {

            } finally {
                br.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static void readJson(String jsonString){
        JSONArray array = new JSONArray(jsonString);
        System.out.println(array.length());
    }
    public static void printJson(String jsonString){
        JSONArray array = new JSONArray(jsonString);
        printJSONArray(array, 0);
    }
    
    private static void printJSONObject(JSONObject jsonObject, int ident){
        String identString="";
        for(int i=0;i<ident;i++)identString+="\t";
        ident+=1;
        for(String key:jsonObject.keySet()){
            System.out.println(identString+jsonObject.getString(key));
            
        }
        
    }
    
    private static void printJSONArray(JSONArray jsonArray, int ident){
        String identString="";
        for(int i=0;i<ident;i++)identString+="\t";
        ident+=1;
        System.out.println(identString+"[");
        for(int i=0; i<jsonArray.length();i++){
            Object obj = jsonArray.get(i);
            if(obj instanceof JSONArray)
                printJSONArray((JSONArray) obj, ident);
            if(obj instanceof JSONObject)
                printJSONObject((JSONObject) obj, ident);
        }
        System.out.println(identString+"[");
    }

}
