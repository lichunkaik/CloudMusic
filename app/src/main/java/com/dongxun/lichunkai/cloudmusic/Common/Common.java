package com.dongxun.lichunkai.cloudmusic.Common;

import android.graphics.Bitmap;

import com.dongxun.lichunkai.cloudmusic.Class.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * 公用变量
 */
public class Common {

    //当前歌曲
    public static Song song_playing = new Song();
    //播放状态
    public static Boolean state_playing = false;

    //调整进度变量
    public static int changeProgress = 0;

    //当前歌单
    public static List<Song> songList = new ArrayList<>();

    //播放设置
    public static int Playing_loopType = 0; //循环方式
    public static String[] loopType = {"随机播放","单曲循环","列表循环"};//循环方式集合


}
