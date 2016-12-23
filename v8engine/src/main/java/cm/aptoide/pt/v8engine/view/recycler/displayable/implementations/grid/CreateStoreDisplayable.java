package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 02/12/2016.
 */

public class CreateStoreDisplayable extends Displayable {
  @Override public int getViewLayout() {
    return R.layout.create_store_displayable_layout;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
