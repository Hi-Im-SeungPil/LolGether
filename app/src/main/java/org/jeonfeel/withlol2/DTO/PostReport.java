package org.jeonfeel.withlol2.DTO;

public class PostReport {

    public String Uid;
    public String title;
    public String content;
    public String postId;

    public PostReport(String uid, String title, String content, String postId) {
        Uid = uid;
        this.title = title;
        this.content = content;
        this.postId = postId;
    }
}
