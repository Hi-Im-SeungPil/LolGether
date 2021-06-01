package org.jeonfeel.withlol2.DTO;

public class SaveFreeBoardPost {

    private String id;
    private String Uid;
    private String summonerName;
    private String summonerTier;
    private String title;
    private String content;
    private int commentCount;
    private long postDate;
    private int imgExist;

    public SaveFreeBoardPost(){}

    public SaveFreeBoardPost(String id, String uid, String summonerName, String summonerTier,
                             String title, String content, int commentCount, long postDate, int imgExist) {
        this.id = id;
        this.Uid = uid;
        this.summonerName = summonerName;
        this.summonerTier = summonerTier;
        this.title = title;
        this.content = content;
        this.commentCount = commentCount;
        this.postDate = postDate;
        this.imgExist = imgExist;
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

    public int getImgExist() {
        return imgExist;
    }

    public void setImgExist(int imgExist) {
        this.imgExist = imgExist;
    }
}
