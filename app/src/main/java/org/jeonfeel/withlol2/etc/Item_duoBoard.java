package org.jeonfeel.withlol2.etc;

public class Item_duoBoard {
    private String _id;
    private String Uid;
    private String tier;
    private String title;
    private String writtenTier;
    private String content;
    private String summonerName;
    private String selectedMic;
    private String selectedPosition;
    private String boardTitle;
    private int commentCount;
    private long postWrittenDate;

    public Item_duoBoard(String _id, String Uid, String tier, String writtenTier, String title, String content,
                         String summonerName, String selectedMic, String selectedPosition, String boardTitle,
                         int commentCount, long postWrittenDate) {
        this._id = _id;
        this.Uid = Uid;
        this.tier = tier;
        this.writtenTier = writtenTier;
        this.title = title;
        this.selectedMic = selectedMic;
        this.selectedPosition = selectedPosition;
        this.boardTitle = boardTitle;
        this.postWrittenDate = postWrittenDate;
        this.content = content;
        this.summonerName = summonerName;
        this.commentCount = commentCount;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String _id) {
        this.Uid = Uid;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getWrittenTier() {
        return writtenTier;
    }

    public void setWrittenTier(String writtenTier) {
        this.writtenTier = writtenTier;
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

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public long getPostWrittenDate() {
        return postWrittenDate;
    }

    public void setPostWrittenDate(long postWrittenDate) {
        this.postWrittenDate = postWrittenDate;
    }

    public String getSelectedMic() {
        return selectedMic;
    }

    public void setSelectedMic(String selectedMic) {
        this.selectedMic = selectedMic;
    }

    public String getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(String selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }
}
