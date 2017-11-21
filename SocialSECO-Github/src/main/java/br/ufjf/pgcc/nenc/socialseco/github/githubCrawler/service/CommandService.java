/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service;

import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.NoRemainingRequestsException;
import br.ufjf.pgcc.nenc.socialseco.github.githubCrawler.service.exception.PageForbiddenException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author marci
 */
public abstract class CommandService implements Runnable {
    
    private final String USER_AGENT = "Mozilla/5.0";

    public void run(){
        initialize();
        while(true){
            try{
                getData();
                increment();
            }catch (NoRemainingRequestsException e){
                try {
                    Thread.sleep(1000 * 60 );
                } catch (InterruptedException ex1) {
                    
                }
            } catch (PageForbiddenException e){
                increment();
            }catch (Exception e){
                try {
                    Thread.sleep(1000 * 20);
                } catch (InterruptedException ex1) {
                    
                }
            }
        }
    }
    
    abstract public void increment();
    abstract public void initialize();
    abstract public void getData() throws PageForbiddenException,NoRemainingRequestsException;

    private final String USER_TOKEN = "token a2e6c2553b07188888bdd4ef57b96327c5a609ff";

    protected String getContent(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Authorization", USER_TOKEN);

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        System.out.println("Tentativas restantes : " + con.getHeaderField("X-RateLimit-Remaining"));

        if (con.getHeaderField("X-RateLimit-Remaining").equals("0")) {
            con.disconnect();
            throw new NoRemainingRequestsException();
        }
        
        if(responseCode==403 || responseCode==404){
            con.disconnect();
            throw new PageForbiddenException();
        }
        
        

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        con.disconnect();
        return response.toString();

    }
}
