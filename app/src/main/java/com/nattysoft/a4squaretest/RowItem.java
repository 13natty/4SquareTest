package com.nattysoft.a4squaretest;

/**
 * Created by F3838284 on 12/14/2016.
 */

public class RowItem {
    private String imageURL;
    private String title;

    public RowItem(String imageURL, String title) {
        this.imageURL = imageURL;
        this.title = title;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n";
    }
}
