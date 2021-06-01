package org.jeonfeel.withlol2.etc;

public class Item_freeBoard {
    private String id;
    private String writerUid;
    private String tier;
    private String title;
    private String content;
    private String summonerName;
    private int commentCount;
    private int imageExistence;
    private long postWrittenDate;

    public Item_freeBoard(String id, String writerUid, String tier, String title, String content,
                          String summonerName,int imageExistence ,int commentCount, long postWrittenDate) {
        this.id = id;
        this.writerUid = writerUid;
        this.tier = tier;
        this.title = title;
        this.content = content;
        this.summonerName = summonerName;
        this.commentCount = commentCount;
        this.imageExistence = imageExistence;
        this.postWrittenDate = postWrittenDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWriterUid() {
        return writerUid;
    }

    public void setWriterUid(String writerUid) {
        this.writerUid = writerUid;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
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

    public int getImageExistence() {
        return imageExistence;
    }

    public void setImageExistence(int imageExistence) {
        this.imageExistence = imageExistence;
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
}
