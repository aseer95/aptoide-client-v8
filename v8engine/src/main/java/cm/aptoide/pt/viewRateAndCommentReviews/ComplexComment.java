package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Comment;
import rx.Observable;

public class ComplexComment extends Comment {
  private final Observable<Void> onClickReplyAction;
  private final int level;

  public ComplexComment(CommentNode commentNode, Observable<Void> onClickReplyAction) {
    this.level = commentNode.getLevel();
    Comment comment = commentNode.getComment();
    this.setAdded(comment.getAdded());
    this.setBody(comment.getBody());
    this.setId(comment.getId());
    this.setParentReview(comment.getParentReview());
    this.setUser(comment.getUser());
    this.onClickReplyAction = onClickReplyAction;
  }

  public Observable<Void> observeReplySubmission() {
    return onClickReplyAction;
  }

  public int getLevel() {
    return level;
  }
}
