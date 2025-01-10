package com.example.spotifydecades;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/* class populates the decades list and prepares certain data for the templates
* Methods:
* populateList()
* extractDecade()
* search()
* wipe()
* tally()
* getMaxValue()
* setPercentages()
* setColors()
* generateGradient()
* sort()
*/

public class DecadeCalculation {

    public static ArrayList<Decade> decades = new ArrayList<>();

    //traverses the JSONobject of songs to fill in the list of decades and prepare the data for the view
    public static void populateList(String token) {
        //clear any existing data
        wipe();
        int offset = 0;
        JSONObject curPage = APIRequests.getSongs(token, offset);
        if (curPage != null) {
            //run the decade extraction method while increasing the offset each time to fetch each new page of data
            while (curPage.get("next") != null) {
                extractDecade(curPage);
                //50 songs is the limit per page
                offset += 50;
                curPage = APIRequests.getSongs(token, offset);
            }
            //to avoid skipping last page of data
            extractDecade(curPage);
            //fill in other data after populating the list
            setColors();
            setPercentages();
            //sort once all the data is in place
            sort();
        }
    }

    //creates decade objects from the song data
    private static void extractDecade(JSONObject data) {
        JSONArray tracks = (JSONArray) data.get("items");
        if (tracks != null) {
            int numSongs = tracks.size();
            for (int i = 0; i < numSongs; i++) {
                //release date is nested inside other objects that we must go through first
                JSONObject song = (JSONObject) tracks.get(i);
                JSONObject track = (JSONObject) song.get("track");
                JSONObject album = (JSONObject) track.get("album");
                String date = (String) album.get("release_date");
                //take the first 3 characters of the date to get the decade, adding 0 at the end
                String decade = date.substring(0, 3) + '0';
                //if the decade is not found in the list, create a new one and add it
                int index = search(decade);
                if (index == -1) {
                    Decade temp = new Decade(decade);
                    decades.add(temp);
                } else {
                    //otherwise find that decade and increase its tally
                    decades.get(index).increaseTally();
                }
            }
        }

    }


    private static int search(String d) {
        int size = decades.size();
        for (int i = 0; i < size; i++) {
            if (d.equals(decades.get(i).getYear())) {
                return i;
            }
        }
        return -1;
    }

    //clears list
    public static void wipe() {
        if (!decades.isEmpty()) {
            decades.clear();
        }
    }

    //gets the total number of songs
    public static int tally() {
        int size = decades.size();
        int total = 0;
        //compile the tallies for each decade
        for (int i = 0; i < size; i++) {
            total += decades.get(i).getTally();
        }
        return total;
    }

    //gets the percentage value of the most frequent decade
    private static double getMaxValue() {
        int maxValue = 0;
        int size = decades.size();
        for (int i = 0; i < size; i++) {
            if (decades.get(i).getTally() > maxValue) {
                maxValue = decades.get(i).getTally();
            }
        }
        return ((double) maxValue / tally()) * 100;
    }

    //calculates and sets the percentages and bar graph heights for the decades
    private static void setPercentages() {
        double max = getMaxValue();
        int total = tally();
        int size = decades.size();
        for (int i = 0; i < size; i++) {
            double percent = ((double) decades.get(i).getTally() / total) * 100;
            //bar heights are calculated relative to the maximum value in the set
            double barSize = (percent / max) * 100;
            decades.get(i).setPercentage(String.format("%.2f", percent) + '%');
            decades.get(i).setBarSize(String.format("%.2f", barSize) + '%');
        }
    }

    //assigns a unique colour to each decade
    private static void setColors() {
        //the app can handle up to 20 decades for now
        String[] colors = {"#e097b5", "#d53e4f", "#abdda4", "#ffffbf", "#328881", "#9e0142",
                "#3288bd", "#bda1e0", "#5e4fa2", "#66c2a5", "#a3e2e5", "#fee08b",
                "#2d6689", "#a85175", "#e6f598", "#f46d43", "#fdae61", "#38305a",
                "#d0cacd", "#f4f3f3"};
        int size = decades.size();
        for (int i = 0; i < size; i++) {
            decades.get(i).setColor(colors[i]);
        }
    }

    //creates a string of the values to be used for the pie chart gradient
    public static String generateGradient() {
        StringBuilder gradient = new StringBuilder();
        //value is stored as a double so that calculations can be performed with it
        double value = 0;
        int size = decades.size();
        //traverse the list in reverse order as it is sorted descending
        for (int i = size-1; i >= 0; i--) {
            //get the starting value for the decade as a string
            String prev = String.valueOf(value) + '%';
            String curVal = decades.get(i).getPercentage();
            //update the value by adding on the current decade's percentage
            value += Double.parseDouble(curVal.replaceFirst("%", ""));
            //convert to string
            curVal = String.valueOf(value) + '%';
            //color will span from the starting value to the current value on the circle
            gradient.append('\n').append(decades.get(i).getColor()).append(" ").append(prev).append(", ").append(decades.get(i).getColor()).append(" ").append(curVal);
            //formatting
            if (i != (0)) {
                gradient.append(",");
            }
        }
        //return string value
        return gradient.toString();
}

    private static void sort() {
        int size = decades.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < (size - i - 1); j++) {
                if (decades.get(j).getTally() < decades.get(j + 1).getTally()) {
                    Decade temp = new Decade(decades.get(j));
                    decades.set(j, decades.get(j + 1));
                    decades.set(j+1, temp);
                }
            }
        }
    }
}

