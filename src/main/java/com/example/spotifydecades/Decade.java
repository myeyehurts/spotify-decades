package com.example.spotifydecades;

//Decade object
public class Decade {

    private String year;
    private int tally;
    private String percentage;  //formatted as string so that it can be easily inserted into html/css code
    private String barSize;
    private String color;

    //constructor which takes a year
    public Decade(String newYear) {
        year = newYear;
        tally = 1;
        percentage = "";
        barSize = "";
        color = "";
    }

    //copy constructor
    public Decade(Decade copyDecade) {
        this.year = copyDecade.year;
        this.tally = copyDecade.tally;
        this.percentage = copyDecade.percentage;
        this.barSize = copyDecade.barSize;
        this.color = copyDecade.color;
    }

    public void increaseTally() {
        tally++;
    }

    //getters
    public String getYear() {
        return year;
    }

    public int getTally() {
        return tally;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getBarSize() {
        return barSize;
    }

    public String getColor() {
        return color;
    }

    //setters
    public void setYear(String newYear) {
        year = newYear;
    }

    public void setTally(int newTally) {
        tally = newTally;
    }

    public void setPercentage(String newPercentage) {
        percentage = newPercentage;
    }

    public void setBarSize(String newBarSize) {
        barSize = newBarSize;
    }

    public void setColor(String newColor) {
        color = newColor;
    }
}
