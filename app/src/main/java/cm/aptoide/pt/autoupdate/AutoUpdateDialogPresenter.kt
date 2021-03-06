package cm.aptoide.pt.autoupdate

import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View
import rx.schedulers.Schedulers

class AutoUpdateDialogPresenter(private val view: AutoUpdateDialogView,
                                private val crashReporter: CrashReport,
                                private val autoUpdateManager: AutoUpdateManager) : Presenter {

  override fun present() {
    handleUpdateClick()
    handleNotNowClick()
  }

  private fun handleUpdateClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { view.updateClicked() }
        .observeOn(Schedulers.io())
        .flatMap { autoUpdateManager.startUpdate(true) }
        .doOnNext { view.dismissDialog() }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

  private fun handleNotNowClick() {
    view.lifecycleEvent
        .filter { lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE }
        .flatMap { view.notNowClicked() }
        .doOnNext {
          view.dismissDialog()
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, { e -> crashReporter.log(e) })
  }

}