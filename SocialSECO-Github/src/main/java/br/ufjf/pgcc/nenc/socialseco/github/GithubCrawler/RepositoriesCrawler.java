/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf.pgcc.nenc.socialseco.github.GithubCrawler;

import br.ufjf.pgcc.nenc.socialseco.github.Util.SaveToFile;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;


import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author marci
 */
public class RepositoriesCrawler implements CommandGET {

    private final String USER_AGENT = "Mozilla/5.0";
    
    @Override
    public void executeGETMethod() {

        try {
            JSONArray json = sendGet();
            SaveToFile.save("output.json", json.toString());
        } catch (Exception ex) {
            Logger.getLogger(RepositoriesCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JSONArray sendGet() throws Exception {

		String url = "https://api.github.com/repositories?since=1";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		System.out.println(response.toString());
                return new JSONArray(response.toString());

	}


}
