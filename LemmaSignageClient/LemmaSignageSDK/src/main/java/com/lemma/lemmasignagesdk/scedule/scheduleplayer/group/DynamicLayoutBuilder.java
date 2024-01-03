package com.lemma.lemmasignagesdk.scedule.scheduleplayer.group;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAdGrp;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdI;

import java.util.ArrayList;

public class DynamicLayoutBuilder {
    private final LinearLayout container;
    ArrayList<ScheduleAd> adQueue = new ArrayList<>();
    private ScheduleAdGrp adGroup;
    private DynamicLayoutBuilderCallback callback;

    public DynamicLayoutBuilder(LinearLayout container) {
        this.container = container;
    }

    public void setCallback(DynamicLayoutBuilderCallback callback) {
        this.callback = callback;
    }

    public void build(ScheduleAdGrp adGroup) {
        this.adGroup = adGroup;
        prepareLayoutForFourAds(adGroup);
    }

    boolean exists(ArrayList<Pair> yArray, int index) {

        for (Pair p : yArray) {
            int i = p.coordinate;
            if (i == index) {
                return true;
            }

        }
        return false;
    }

    private void prepareLayoutForFourAds(ScheduleAdGrp adGroup) {

        ArrayList<Pair> xArray = new ArrayList<>();
        ArrayList<Pair> yArray = new ArrayList<>();

        int maxDiff = 0;
        boolean maxDiffAlongX = true;
        for (ScheduleAd ad : adGroup.getScheduleAds()) {
            adQueue.add(ad);
            AdI.Frame frame = ad.getFrame();
            if (!exists(xArray, frame.startX)) {
                Pair p = new Pair();
                p.frame = frame;
                p.coordinate = frame.startX;
                xArray.add(p);
            }


            if (!exists(yArray, frame.startY)) {
                Pair p = new Pair();
                p.frame = frame;
                p.coordinate = frame.startY;
                yArray.add(p);
            }

            int diff = Math.abs(frame.startX - frame.endX);
            if (maxDiff < diff) {
                maxDiff = diff;
                maxDiffAlongX = true;
            }

            diff = Math.abs(ad.getFrame().startY - frame.endY);
            if (maxDiff < diff) {
                maxDiff = diff;
                maxDiffAlongX = false;
            }
        }

        if (maxDiffAlongX) {
            //cut horizontal
            container.setOrientation(VERTICAL);
            //Unique y n umber of linear layouts
            createLayoutsY(yArray);

        } else {
            //cut vertical
            container.setOrientation(HORIZONTAL);
            //Unique x
            createLayoutsX(xArray);
        }

    }

    private void createLayoutsX(ArrayList<Pair> mapsX) {

        for (Pair pairX : mapsX) {
            AdI.Frame f = pairX.frame;

            int weight = Math.abs(f.startX - f.endX);
            LinearLayout linearLayout1 = new LinearLayout(container.getContext());
            linearLayout1.setOrientation(VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1 - (float) (weight / 100.0);
            addViewsInFrameRangeX(linearLayout1, new AdI.Frame(f.startX, f.endX, 0, 100));
            container.addView(linearLayout1, params);
        }
    }

    public void createLayoutsY(ArrayList<Pair> mapsY) {

        for (Pair pairY : mapsY) {
            AdI.Frame f = pairY.frame;

            int weight = Math.abs(f.startY - f.endY);
            LinearLayout linearLayout1 = new LinearLayout(container.getContext());
            linearLayout1.setOrientation(HORIZONTAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1 - (float) (weight / 100.0);
            addViewsInFrameRangeY(linearLayout1, new AdI.Frame(0, 100, f.startY, f.endY));
            container.addView(linearLayout1, params);
        }
    }

    private void addViewsInFrameRangeY(LinearLayout parentLayout, AdI.Frame f) {

        ArrayList<ScheduleAd> matchedAds = new ArrayList<>();
        for (ScheduleAd ad : adGroup.getScheduleAds()) {
            AdI.Frame frame = ad.getFrame();

            if (frame.startY == f.startY && frame.endY == f.endY) {
                matchedAds.add(ad);
            }
        }

        if (matchedAds.size() == 1) {
            AdI ad = matchedAds.get(0);
            View view = this.callback.viewForObject(ad);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            parentLayout.addView(view, params);

        } else if (matchedAds.size() > 1) {

            for (ScheduleAd ad : matchedAds) {
                AdI.Frame frame = ad.getFrame();

                View view = this.callback.viewForObject(ad);

                int weight = Math.abs(frame.startX - frame.endX);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.weight = 1 - (float) (weight / 100.0);

                parentLayout.addView(view, params);
            }
        }
    }

    private void addViewsInFrameRangeX(LinearLayout parentLayout, AdI.Frame f) {

        ArrayList<ScheduleAd> matchedAds = new ArrayList<>();
        for (ScheduleAd ad : adGroup.getScheduleAds()) {
            AdI.Frame frame = ad.getFrame();

            if (frame.startX == f.startX && frame.endX == f.endX) {
                matchedAds.add(ad);
            }
        }

        if (matchedAds.size() == 1) {
            AdI ad = matchedAds.get(0);
            View view = this.callback.viewForObject(ad);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            parentLayout.addView(view, params);


        } else if (matchedAds.size() > 1) {

            for (ScheduleAd ad : matchedAds) {
                AdI.Frame frame = ad.getFrame();

                View view = this.callback.viewForObject(ad);

                int weight = Math.abs(frame.startY - frame.endY);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.weight = 1 - (float) (weight / 100.0);
                parentLayout.addView(view, params);
            }
        }
    }

    public interface DynamicLayoutBuilderCallback<T> {
        ViewGroup viewForObject(T obj);
    }

    class Pair {

        public Integer coordinate;
        public AdI.Frame frame;
    }

}
