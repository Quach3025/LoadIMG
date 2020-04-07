package hqph.idri.loadimg.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter111 extends RecyclerView.ItemDecoration {
    int halSpace ;

    public Adapter111(int halSpace) {
        this.halSpace = halSpace/2;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getPaddingLeft()!=halSpace){
            parent.setPadding(halSpace,halSpace,halSpace,halSpace);
            parent.setClipToPadding(false);

        }
        outRect.top= halSpace;
        outRect.right= halSpace;
        outRect.left= halSpace;
        outRect.bottom= halSpace;

    }
}
