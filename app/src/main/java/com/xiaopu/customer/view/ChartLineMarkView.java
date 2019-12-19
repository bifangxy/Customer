package com.xiaopu.customer.view;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.xiaopu.customer.R;

/**
 * Created by Administrator on 2016/10/24 0024.
 */
public class ChartLineMarkView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     */
    private TextView tvMarker;

    public ChartLineMarkView(Context context) {
        super(context, R.layout.layout_marker_view);
        tvMarker = (TextView) findViewById(R.id.tvMarkText);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        if (entry instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) entry;
            tvMarker.setText(ce.getBodyRange() + "," + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            tvMarker.setText("" + entry.getVal() + "," + highlight.getDataSetIndex());
        }
    }

    @Override
    public int getXOffset(float v) {
        return 0;
    }

    @Override
    public int getYOffset(float v) {
        return 0;
    }

}
