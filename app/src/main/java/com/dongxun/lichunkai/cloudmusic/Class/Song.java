package com.dongxun.lichunkai.cloudmusic.Class;

import android.graphics.Bitmap;

public class Song {
    private String name;//歌曲名
    private String id;//歌曲ID
    private String artist;//艺术家
    private Bitmap cover;//封面图Bitmap
    private String coverURL;//封面图URL

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
