package com.trinhbk.lecturelivestream.model;

/**
 * Created by TrinhBK on 9/21/2018.
 */

public class Video {

    private String videoName;
    private String videoThumb;
    private String videoDate;

    public Video(String videoName) {
        this.videoName = videoName;
    }

    public Video(String videoName, String videoThumb, String videoDate) {
        this.videoName = videoName;
        this.videoThumb = videoThumb;
        this.videoDate = videoDate;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getVideoDate() {
        return videoDate;
    }

    public void setVideoDate(String videoDate) {
        this.videoDate = videoDate;
    }
}
