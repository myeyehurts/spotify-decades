package com.example.spotifydecades;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

import java.net.URLEncoder;
import java.net.URI;
import java.net.http.*;

import java.nio.charset.StandardCharsets;

/* class for all authorization procedures
* Methods:
* buildURL()
* getAccessToken()
* refreshToken()
*/

public class UserAuthorization {

    //sensitive data stored in .env
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://localhost:8080/dashboard";
    private static final String RESPONSE_TYPE = "code";
    private static final String SCOPE = "user-library-read";
    private static final String SHOW_DIALOG = "true";

    //creates url to authorization page
    public static String buildURL() {
        return "https://accounts.spotify.com/authorize" +
                "?client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                "&response_type=" + URLEncoder.encode(RESPONSE_TYPE, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8) +
                "&show_dialog=" + URLEncoder.encode(SHOW_DIALOG, StandardCharsets.UTF_8);
    }

    //exchanges authorization code for access token
    public static String getAccessToken(String code) {
        //data for the request
        String postData = "grant_type=authorization_code" +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8);
            HttpClient client = HttpClient.newHttpClient();
            //submit a post request to the token endpoint with the required information
            HttpRequest getToken = HttpRequest.newBuilder()
                    .uri(URI.create("https://accounts.spotify.com/api/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(postData))
                    .build();
            try {
                HttpResponse <String> response = client.send(getToken, HttpResponse.BodyHandlers.ofString());
                //check for a successful response code and return the obtained access token
                if (response.statusCode() == 200) {
                    JSONParser parser = new JSONParser();
                    JSONObject responseJSON = (JSONObject) parser.parse(response.body());
                    return (String) responseJSON.get("access_token");
                }
                else {
                    throw new RuntimeException("Obtaining access token was not successful.");
                }
            }
            catch (IOException | InterruptedException | ParseException e) {
                throw new RuntimeException("Obtaining access token was not successful.");

            }
    }

    //obtains a new token from an expired one
    public static String refreshToken(String token) {
        String postData = "grant_type=refresh_token&refresh_token=" + token;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getNewToken = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(postData))
                .build();
        try {
            HttpResponse <String> response = client.send(getNewToken, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONParser parser = new JSONParser();
                JSONObject responseJSON = (JSONObject) parser.parse(response.body());
                return (String) responseJSON.get("access_token");
            }
            else {
                throw new RuntimeException("Refreshing token was not successful.");
            }
        }
        catch (IOException | InterruptedException | ParseException e) {
            throw new RuntimeException("Refreshing token was not successful.");
        }
    }
}
