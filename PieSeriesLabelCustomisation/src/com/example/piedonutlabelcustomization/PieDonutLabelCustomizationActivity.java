/***
 * 
 * @author karmer
 * 
 * Copyright 2013 Scott Logic
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.example.piedonutlabelcustomization;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.shinobicontrols.charts.ChartFragment;
import com.shinobicontrols.charts.ChartUtils;
import com.shinobicontrols.charts.DataAdapter;
import com.shinobicontrols.charts.DataPoint;
import com.shinobicontrols.charts.Legend;
import com.shinobicontrols.charts.PieDonutSeries;
import com.shinobicontrols.charts.PieDonutSeries.RadialEffect;
import com.shinobicontrols.charts.PieDonutSlice;
import com.shinobicontrols.charts.PieSeries;
import com.shinobicontrols.charts.PieSeriesStyle;
import com.shinobicontrols.charts.Series.SelectionMode;
import com.shinobicontrols.charts.ShinobiChart;
import com.shinobicontrols.charts.ShinobiChart.OnPieDonutSliceLabelDrawListener;
import com.shinobicontrols.charts.ShinobiChart.OnPieDonutSliceUpdateListener;
import com.shinobicontrols.charts.SimpleDataAdapter;

public class PieDonutLabelCustomizationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_series);

        // Only set the chart up the first time the Activity is created
        if (savedInstanceState == null) {

            ChartFragment chartFragment =
                    (ChartFragment) getFragmentManager().findFragmentById(R.id.chart);

            // Get the a reference to the ShinobiChart from the ChartFragment
            ShinobiChart shinobiChart = chartFragment.getShinobiChart();

            // TODO: replace <license_key_here> with you trial license key
            shinobiChart.setLicenseKey("<license_key_here>");

            // Create our DataAdapter and data
            DataAdapter<String, Double> dataAdapter = new SimpleDataAdapter<String, Double>();
            dataAdapter.add(new DataPoint<String, Double>("France", 2.613));
            dataAdapter.add(new DataPoint<String, Double>("Germany", 3.4));
            dataAdapter.add(new DataPoint<String, Double>("Italy", 2.013));
            dataAdapter.add(new DataPoint<String, Double>("Japan", 5.96));
            dataAdapter.add(new DataPoint<String, Double>("UK", 2.435));
            dataAdapter.add(new DataPoint<String, Double>("USA", 15.68));

            // Create a PieSeries with some custom settings and give it the data
            // adapter
            PieSeries series = new PieSeries();
            series.setOuterRadius(0.50f);
            series.setSelectionMode(SelectionMode.POINT_SINGLE);
            series.setSelectedPosition(0.0f);
            series.setDataAdapter(dataAdapter);
            shinobiChart.addSeries(series);

            // Select the dark theme
            shinobiChart.applyTheme(R.style.Theme_Default_Dark, true);

            // Apply styling to the Pie Series
            PieSeriesStyle style = series.getStyle();
            style.setRadialEffect(RadialEffect.BEVELLED_LIGHT);
            style.setCrustShown(false);
            style.setLabelTextSize(16.0f);

            // Apply style to selected slices
            PieSeriesStyle selectedSeriesStyle = series.getSelectedStyle();
            selectedSeriesStyle.setCrustShown(false);
            selectedSeriesStyle.setRadialEffect(RadialEffect.BEVELLED_LIGHT);

            // Add a legend
            shinobiChart.getLegend().setVisibility(View.VISIBLE);
            shinobiChart.getLegend().setPlacement(Legend.Placement.INSIDE_PLOT_AREA);

            // Assign listeners to the chart
            shinobiChart.setOnPieDonutSliceUpdateListener(new
                    ProtrudeAxisLabelListener());
            shinobiChart.setOnPieDonutSliceLabelDrawListener(new
                    DrawLabelWithLinesListener());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.custom_data_adapter, menu);
        return true;
    }

    public static class ProtrudeAxisLabelListener implements OnPieDonutSliceUpdateListener {
        private static final int EXTRUSION = 200;

        @Override
        public void onUpdateSlice(PieDonutSlice slice, PieDonutSeries<?> series) {
            slice.setLabelText(slice.getY() + "\nTrillion (USD)");
            offsetLabelCenter(series, slice);
        }

        private void offsetLabelCenter(PieDonutSeries<?> series, PieDonutSlice slice) {
            // add a little to the center of the slice label to move it outward
            float centerAngle = slice.getCenterAngle() + series.getRotation();
            slice.getLabelCenter().x = (int) (slice.getCenterX() - EXTRUSION
                    * Math.sin(centerAngle));
            slice.getLabelCenter().y = (int) (slice.getCenterY() - EXTRUSION
                    * Math.cos(centerAngle));
        }
    }

    public static class DrawLabelWithLinesListener implements OnPieDonutSliceLabelDrawListener {
        private static RectF bg = new RectF();

        @Override
        public void onDrawLabel(Canvas canvas, PieDonutSlice slice, Rect bgLabelRect,
                PieDonutSeries<?> series) {
            // Apply a styled background to the slice label, and join it to the
            // chart with a 'spoke'
            // Draw line
            slice.getLabelPaint().setColor(Color.WHITE);
            canvas.drawLine(slice.getCenterX(), slice.getCenterY(), slice.getLabelCenter().x,
                    slice.getLabelCenter().y, slice.getLabelPaint());
            // Draw background
            slice.getLabelBackgroundPaint().setColor(Color.GRAY);
            bg.set(bgLabelRect.left, bgLabelRect.top, bgLabelRect.right,
                    bgLabelRect.bottom);
            canvas.drawRoundRect(bg, 10.0f, 10.0f, slice.getLabelBackgroundPaint());
            // And finally draw the text - use ChartUtils as this handles multi
            // line text. Use the label paint to honor already applied styling
            ChartUtils.drawText(canvas, slice.getLabelText(), slice.getLabelCenter().x,
                    slice.getLabelCenter().y, slice.getLabelPaint());
        }
    }
}
