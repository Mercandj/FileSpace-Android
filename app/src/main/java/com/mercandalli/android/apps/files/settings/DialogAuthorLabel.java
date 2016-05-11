/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.settings;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.library.base.java.StringUtils;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class DialogAuthorLabel extends Dialog {

    private Activity mActivity;
    ShimmerTextView myShimmerTextView, version_tv;

    public DialogAuthorLabel(
            final Activity activity) {
        super(activity, android.R.style.Theme_Material_Dialog);
        mActivity = activity;

        this.setContentView(R.layout.dialog_author_label);
        this.setTitle(R.string.app_name);
        this.setCancelable(true);

        version_tv = (ShimmerTextView) this.findViewById(R.id.version_tv);
        countDown(version_tv, 5);

        myShimmerTextView = (ShimmerTextView) this.findViewById(R.id.shimmer_tv);

        getShimmer(myShimmerTextView).start(myShimmerTextView);

        this.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        DialogAuthorLabel.this.show();
    }

    private void countDown(final ShimmerTextView tv, final int count) {
        if (count == 0) {
            tv.setText(""); //Note: the TextView will be visible again here.
            return;
        }
        tv.setText(String.format("%d", count));
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation anim) {
                countDown(tv, count - 1);
                if (count == 1) {
                    try {
                        getShimmer(tv).start(tv);
                        PackageInfo info = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
                        version(tv, "Version: " + info.versionName, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(getClass().getName(), "Exception", e);
                    }

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        tv.startAnimation(animation);
    }

    private void version(final TextView tv, final String text, final int rec) {
        if (rec == text.length()) {
            tv.setText(text); //Note: the TextView will be visible again here.
            return;
        }
        tv.setText(String.format("%s", StringUtils.substring(text, rec)));
        AlphaAnimation animation = new AlphaAnimation(1.0f, 1.0f);
        animation.setDuration(75);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation anim) {
                if (rec < text.length() + 1) {
                    version(tv, text, rec + 1);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        tv.startAnimation(animation);
    }

    public Shimmer getShimmer(final ShimmerTextView tv) {
        Shimmer shimmer = new Shimmer();
        shimmer.setRepeatCount(0)
                .setDuration(1200)
                .setStartDelay(2000)
                .setAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getShimmer(tv).start(tv);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
        return shimmer;
    }
}
