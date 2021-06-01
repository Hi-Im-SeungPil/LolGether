package org.jeonfeel.withlol2.etc;

public class Item_comment {

    private String commentId;
    private String commentSummonerTier;
    private String commentSummonerName;
    private String commentContent;
    private String Uid;
    private long commentDate;

    public Item_comment(String commentId, String commentSummonerTier, String commentSummonerName,
                        String commentContent, String Uid, long commentDate) {
        this.commentId = commentId;
        this.commentSummonerTier = commentSummonerTier;
        this.commentSummonerName = commentSummonerName;
        this.commentContent = commentContent;
        this.Uid = Uid;
        this.commentDate = commentDate;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentSummonerTier() {
        return commentSummonerTier;
    }

    public void setCommentSummonerTier(String commentSummonerTier) {
        this.commentSummonerTier = commentSummonerTier;
    }

    public String getCommentSummonerName() {
        return commentSummonerName;
    }

    public void setCommentSummonerName(String commentSummonerName) {
        this.commentSummonerName = commentSummonerName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public long getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(long commentDate) {
        this.commentDate = commentDate;
    }
}
