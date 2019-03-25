package cm.aptoide.pt.home.apps;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

public class ErrorUpgradeAppViewHolder extends AppsViewHolder {

  private TextView appName;
  private ImageView appIcon;
  private ImageView retryButton;
  private PublishSubject<AppClick> retryUpdate;

  public ErrorUpgradeAppViewHolder(View itemView, PublishSubject<AppClick> retryUpdate) {
    super(itemView);

    appName = (TextView) itemView.findViewById(R.id.apps_updates_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.apps_updates_app_icon);
    retryButton = (ImageView) itemView.findViewById(R.id.apps_updates_retry_button);
    retryButton.setImageDrawable(
        ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_refresh_appc));
    this.retryUpdate = retryUpdate;
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((UpdateApp) app).getIcon(), appIcon);
    appName.setText(((UpdateApp) app).getName());
    retryButton.setOnClickListener(
        install -> retryUpdate.onNext(new AppClick(app, AppClick.ClickType.APPC_UPGRADE_RETRY)));
  }
}
