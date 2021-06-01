package org.jeonfeel.withlol2.DTO;

import androidx.annotation.Keep;

public class SaveComment {
    private String _id;
    private String summonerTier;
    private String summonerName;
    private String commentContent;
    private String uid;
    private long commentDate;

    @Keep
    SaveComment(){}



    public SaveComment(String _id, String summonerTier, String summonerName, String commentContent, String uid,
                       long commentDate) {
        this._id = _id;
        this.summonerTier = summonerTier;
        this.summonerName = summonerName;
        this.commentContent = commentContent;
        this.uid = uid;
        this.commentDate = commentDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSummonerTier() {
        return summonerTier;
    }

    public void setSummonerTier(String summonerTier) {
        this.summonerTier = summonerTier;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(long commentDate) {
        this.commentDate = commentDate;
    }
}
