package com.videokenplayer.database;

/**
 * Created by ranjith on 31/5/17.
 */

public class VideoModel {
    int _id;
    String _video;
    String _time;
    String _speachtext;

    public String get_speachtext() {
        return _speachtext;
    }

    public VideoModel() {

    }

    public void set_speachtext(String _speachtext) {
        this._speachtext = _speachtext;
    }

    public VideoModel(int _id, String _video, String _speachtext,String _time) {
        this._id = _id;
        this._video = _video;
        this._time = _time;
        this._speachtext = _speachtext;
    }

    public VideoModel(String _video, String _speachtext,String _time) {
        this._video = _video;
        this._time = _time;
        this._speachtext = _speachtext;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_video() {
        return _video;
    }

    public void set_video(String _video) {
        this._video = _video;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }
}
