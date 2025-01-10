package com.example.spotifydecades;

import java.io.IOException;
import java.net.URI;

import java.net.http.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/* class where all user data requests are made
* Methods:
* populateList()
* getUser()
* getSongs()
 */

public class APIRequests {

    private static final int LIMIT = 50;

    //gets the username of the authenticated user
    public static String getUser(String token) {
        String urlString = "https://api.spotify.com/v1/me";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getUserProfile = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("Authorization", "Bearer " + token)
                .build();
        try {
            HttpResponse<String> profileInfo = client.send(getUserProfile, HttpResponse.BodyHandlers.ofString());
            if (profileInfo.statusCode() == 200) {
                JSONParser parser = new JSONParser();
                JSONObject profileJSON = (JSONObject) parser.parse(profileInfo.body());
                return (String) profileJSON.get("display_name");
            }
        }
        catch(IOException | ParseException | InterruptedException e) {
            System.out.println ("Unable to retrieve user profile.");
        }
        return null;
    }

    //gets an object of all the songs in the user's library
    public static JSONObject getSongs(String token, int offset) {
        String urlString = "https://api.spotify.com/v1/me/tracks?limit=" + LIMIT + "&offset=" + offset;
        try {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getSongsList = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("Authorization", "Bearer " + token)
                .build();
            HttpResponse<String> songsList = client.send(getSongsList, HttpResponse.BodyHandlers.ofString());
            if (songsList.statusCode() == 200) {
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(songsList.body());
            }

        } catch (IOException | ParseException | InterruptedException e) {
            throw new RuntimeException("Unable to retrieve songs.");
        }
        return null;
    }
}


