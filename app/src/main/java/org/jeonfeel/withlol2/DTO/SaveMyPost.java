package org.jeonfeel.withlol2.DTO;

public class SaveMyPost {
    private String postId;
    private String postBoardChild;
    private String postSelectedPosition;

    SaveMyPost(){}

    public SaveMyPost(String postId, String postBoardChild, String postSelectedPosition) {
        this.postId = postId;
        this.postBoardChild = postBoardChild;
        this.postSelectedPosition = postSelectedPosition;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostBoardChild() {
        return postBoardChild;
    }

    public void setPostBoardChild(String postBoardChild) {
        this.postBoardChild = postBoardChild;
    }

    public String getPostSelectedPosition() {
        return postSelectedPosition;
    }

    public void setPostSelectedPosition(String postSelectedPosition) {
        this.postSelectedPosition = postSelectedPosition;
    }
}
