package org.jeonfeel.withlol2.DTO;

import androidx.annotation.Keep;

public class SaveDuoBoardPost {

    private String id;
    private String Uid;
    private String postTier;
    private String summonerName;
    private String summonerTier;
    private String title;
    private String content;
    private String selectedPosition;
    private String selectedMic;
    private String boardTitle;
    private int commentCount;
    private long postDate;

    @Keep
    SaveDuoBoardPost(){}

    public SaveDuoBoardPost(String id, String uid, String postTier, String summonerName,
                            String summonerTier, String title, String content, String selectedPosition
            , String selectedMic, String boardTitle, int commentCount, long postDate) {
        this.id = id;
        this.Uid = uid;
        this.postTier = postTier;
        this.summonerName = summonerName;
        this.summonerTier = summonerTier;
        this.title = title;
        this.content = content;
        this.selectedPosition = selectedPosition;
        this.selectedMic = selectedMic;
        this.boardTitle = boardTitle;
        this.commentCount = commentCount;
        this.postDate = postDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getPostTier() {
        return postTier;
    }

    public void setPostTier(String postTier) {
        this.postTier = postTier;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public String getSummonerTier() {
        return summonerTier;
    }

    public void setSummonerTier(String summonerTier) {
        this.summonerTier = summonerTier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(String selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public String getSelectedMic() {
        return selectedMic;
    }

    public void setSelectedMic(String selectedMic) {
        this.selectedMic = selectedMic;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public long getPostDate() {
        return postDate;
    }

    public void setPostDate(long postDate) {
        this.postDate = postDate;
    }
}
