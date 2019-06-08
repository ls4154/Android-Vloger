package com.example.androidvloger;

import android.support.annotation.NonNull;

public class NotificationItem implements Comparable<NotificationItem>{
    String id; // 비디오 id, 팔로우의 경우 팔로워 id
    String date;
    String who;
    String title; //댓글 내용 또는 비디오 제목
    int type; //0:댓글, 1:영상, 2:좋아요, 3:팔로우

    NotificationItem(String id, String date, String who, String title, int type) {
        this.id = id;
        this.date = date;
        this.who = who;
        this.title = title;
        this.type = type;
    }
    
    @Override
    public int compareTo(@NonNull NotificationItem o) {
        return -date.compareToIgnoreCase(o.date); // 오름차순인데 반대로 하려면 바꾸기
    }
}
