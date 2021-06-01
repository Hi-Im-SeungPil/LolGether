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
}
