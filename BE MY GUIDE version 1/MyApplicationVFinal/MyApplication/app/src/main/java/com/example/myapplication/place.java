package com.example.myapplication;

public class place {
    private long id;
    private int number;
    private double lng;
    private double lat;
    private String place;
    private int zoom;
    private int statut;
    private double notes;

    public place(){

    }

    public place(double lng, double lat, String place, int zoom) {
        this.lng = lng;
        this.lat = lat;
        this.place = place;
        this.zoom = zoom;
    }
    public place(double lng, double lat, String place, int zoom,double notes) {
        this.lng = lng;
        this.lat = lat;
        this.place = place;
        this.zoom = zoom;
        this.notes=notes;

    }
    public place(double lng, double lat, String place, int zoom,int statut) {
        this.lng = lng;
        this.lat = lat;
        this.place = place;
        this.zoom = zoom;
        this.statut=statut;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public place(double lng, double lat, String place, int zoom, int statut, double notes) {
        this.lng = lng;
        this.lat = lat;
        this.place = place;
        this.zoom = zoom;
        this.statut=statut;
        this.notes=notes;
    }

    @Override
    public String toString() {
        return "place{" +
                "id=" + id +
                ", lng=" + lng +
                ", lat=" + lat +
                ", place='" + place + '\'' +
                ", zoom=" + zoom +
                ", statut=" + statut +
                ", notes=" + notes +
                '}';
    }

    public void setNotes(double notes) {
        this.notes = notes;
    }

    public double getNotes() {
        return notes;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setStatut(int statut) {
        this.statut= statut;
    }

    public int getStatut() {
        return statut;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public long getId() {
        return id;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getPlace() {
        return place;
    }

    public int getZoom() {
        return zoom;
    }
}
