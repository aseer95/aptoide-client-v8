package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEventConverter;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEventConverter;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.repository.DownloadRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 7/15/16.
 */
@Deprecated
public class DownloadsFragment extends GridRecyclerFragmentWithDecorator {

  private static final String TAG = DownloadsFragment.class.getName();

  // list of apps in the same state
  private List<Displayable> downloadingDisplayables;
  private List<Displayable> standingByDisplayables;
  private List<Displayable> completedDisplayables;

  private InstallManager installManager;
  private Analytics analytics;
  private InstallEventConverter installConverter;
  private DownloadEventConverter downloadConverter;

  public static DownloadsFragment newInstance() {
    return new DownloadsFragment();
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_fragment_downloads;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    // not calling super on purpose to avoid cleaning displayables
  }

  @Override public void onViewCreated() {
    super.onViewCreated();

    // TODO: 1/2/2017 optimize this listener splitting it in 3 listeners: one for each download state

    DownloadRepository downloadRepo = RepositoryFactory.getDownloadRepository();
    downloadRepo.getAll()
        .observeOn(Schedulers.computation())
        .sample(100, TimeUnit.MILLISECONDS)
        .doOnNext(__ -> {
          downloadingDisplayables.clear();
          standingByDisplayables.clear();
          completedDisplayables.clear();
        })
        .flatMap(data -> Observable.from(data)
            .flatMap(
                downloadProgress -> createDisplayableForDownload(downloadProgress).toObservable())
            .toList())
        // wait for all displayables are created
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(__ -> addListHeaders().andThen(updateUi()).doOnCompleted(() -> {
          Logger.v(TAG, "updated list of download states");
        }).toObservable())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> {
          // does nothing
        }, err -> {
          CrashReport.getInstance().log(err);
        });
  }

  private Completable createDisplayableForDownload(Download download) {
    return Completable.fromCallable(() -> {
      if (isDownloading(download)) {
        downloadingDisplayables.add(new ActiveDownloadDisplayable(download, installManager));
      } else if (isStandingBy(download)) {
        standingByDisplayables.add(
            new CompletedDownloadDisplayable(download, installManager, downloadConverter, analytics,
                installConverter));
      } else {
        // then it is complete
        completedDisplayables.add(
            new CompletedDownloadDisplayable(download, installManager, downloadConverter, analytics,
                installConverter));
      }
      return null;
    });
  }

  private Completable addListHeaders() {
    return Completable.fromCallable(() -> {

      // add each list header displayable

      if (!downloadingDisplayables.isEmpty()) {
        downloadingDisplayables.add(0,
            new ActiveDownloadsHeaderDisplayable(AptoideUtils.StringU.getResString(R.string.active),
                installManager));
      }

      if (!standingByDisplayables.isEmpty()) {
        standingByDisplayables.add(0, new StoreGridHeaderDisplayable(
            new GetStoreWidgets.WSWidget().setTitle(
                AptoideUtils.StringU.getResString(R.string.stand_by))));
      }
      if (!completedDisplayables.isEmpty()) {
        completedDisplayables.add(0, new StoreGridHeaderDisplayable(
            new GetStoreWidgets.WSWidget().setTitle(
                AptoideUtils.StringU.getResString(R.string.completed))));
      }
      return null;
    });
  }

  private Completable updateUi() {
    return Completable.fromCallable(() -> {
      clearDisplayables().
          addDisplayables(downloadingDisplayables, false).
          addDisplayables(standingByDisplayables, false).
          addDisplayables(completedDisplayables, true);
      return null;
    });
  }

  private boolean isDownloading(Download progress) {
    return progress.getOverallDownloadStatus() == Download.PROGRESS;
  }

  private boolean isStandingBy(Download progress) {
    return progress.getOverallDownloadStatus() == Download.ERROR
        || progress.getOverallDownloadStatus() == Download.PENDING
        || progress.getOverallDownloadStatus() == Download.PAUSED
        || progress.getOverallDownloadStatus() == Download.IN_QUEUE;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    // variables initialization

    downloadingDisplayables = new ArrayList<>();
    standingByDisplayables = new ArrayList<>();
    completedDisplayables = new ArrayList<>();

    installManager = new InstallManager(AptoideDownloadManager.getInstance(),
        new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK));

    analytics = Analytics.getInstance();
    installConverter = new InstallEventConverter();
    downloadConverter = new DownloadEventConverter();

    return super.onCreateView(inflater, container, savedInstanceState);
  }
}
