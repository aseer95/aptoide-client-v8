package cm.aptoide.pt.view.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;

/**
 * Created by trinkes on 13/07/2017.
 */

public class SimpleDividerItemDecoration extends ItemDecoration {

  private final int space;

  public SimpleDividerItemDecoration(Context context, int spaceInDips) {
    this.space = getPixelsFromDips(context, spaceInDips);
  }

  private int getPixelsFromDips(Context context, int dipValue) {
    Resources r = context.getResources();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
        r.getDisplayMetrics());
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    outRect.right = space;
    outRect.left = space;
    outRect.bottom = space;
    outRect.top = space;
  }
}
