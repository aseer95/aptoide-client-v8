package cm.aptoide.pt.social.commentslist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 28/09/2017.
 */

public class PostCommentsPresenter implements Presenter {

  private final PostCommentsView view;
  private final Comments comments;
  private final String postId;

  public PostCommentsPresenter(@NonNull PostCommentsView commentsView, Comments comments,
      String postId) {
    this.view = commentsView;
    this.comments = comments;
    this.postId = postId;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(created -> comments.getComments(postId))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(commentList -> view.showComments(commentList))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(comments -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
