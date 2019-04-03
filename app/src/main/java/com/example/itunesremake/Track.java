package com.example.itunesremake;

public class Track {
    private String artistName;
    private String collectionName;
    private String trackName;
    private String artworkUrl60;
    private boolean favorite = false;
    private String documentId;

    public Track(String artistName, String collectionName, String trackName, String artworkUrl60)
    {
        setArtistName(artistName);
        setCollectionName(collectionName);
        setTrackName(trackName);
        setArtworkUrl60(artworkUrl60);
        setFavorite(false);
    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtworkUrl60() {
        return artworkUrl60;
    }

    public void setArtworkUrl60(String artworkUrl60) {
        this.artworkUrl60 = artworkUrl60;
    }

    public Boolean getFavorite() { return favorite; }

    public void setFavorite(Boolean favorite) { this.favorite = favorite; }

    public String getDocumentId() { return documentId; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }
}
