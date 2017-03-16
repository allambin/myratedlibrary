package com.inextends.myratedlibrary;

import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

class StarsHandler {
    private int mCount;
    private ArrayList<ImageView> mStarsImageViews = new ArrayList<>();

    public int getStarsCount() {
        return mCount;
    }

    public void setStarsImageViews(View view) {
        mStarsImageViews.add((ImageView) view.findViewById(R.id.image_star_1));
        mStarsImageViews.add((ImageView) view.findViewById(R.id.image_star_2));
        mStarsImageViews.add((ImageView) view.findViewById(R.id.image_star_3));
        mStarsImageViews.add((ImageView) view.findViewById(R.id.image_star_4));
        mStarsImageViews.add((ImageView) view.findViewById(R.id.image_star_5));
    }

    private void switchStarsStatus() {
        for (int i = 0; i < mCount; i++) {
            mStarsImageViews.get(i).setImageResource(android.R.drawable.btn_star_big_on);
        }
        for (int i = mCount; i < mStarsImageViews.size(); i++) {
            mStarsImageViews.get(i).setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    public void handleStarsClick() {
        for (int i = 0; i < mStarsImageViews.size(); i++) {
            mStarsImageViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.image_star_1:
                            mCount = 1;
                            break;
                        case R.id.image_star_2:
                            mCount = 2;
                            break;
                        case R.id.image_star_3:
                            mCount = 3;
                            break;
                        case R.id.image_star_4:
                            mCount = 4;
                            break;
                        case R.id.image_star_5:
                            mCount = 5;
                            break;
                    }
                    switchStarsStatus();
                }
            });
        }
    }

    public void displayStarsStatus(int rating) {
        for (int i = 0; i < mStarsImageViews.size(); i++) {
            if (mStarsImageViews.get(i) == null) {
                continue;
            }
            if (i < rating) {
                mStarsImageViews.get(i).setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                mStarsImageViews.get(i).setImageResource(android.R.drawable.btn_star_big_off);
            }
        }
    }
}
