package org.jeonfeel.withlol2.DTO;

public class SaveCommentReport {
    private String commentId;
    private String WriterUid;
    private String commentContent;

    public SaveCommentReport(){}

    public SaveCommentReport(String commentId, String writerUid, String commentContent) {
        this.commentId = commentId;
        this.WriterUid = writerUid;
        this.commentContent = commentContent;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getWriterUid() {
        return WriterUid;
    }

    public void setWriterUid(String writerUid) {
        WriterUid = writerUid;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
