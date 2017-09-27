package com.example.junhaozeng.testdesign.Utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by yuandl on 2016-10-13.
 */
public class CommonViewHolder {
    public static <T extends View> T get(View view, int id) {

        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View chidlView = viewHolder.get(id);
        if (chidlView == null) {
            chidlView = view.findViewById(id);
            viewHolder.put(id, chidlView);
        }
        return (T) chidlView;
    }
}
