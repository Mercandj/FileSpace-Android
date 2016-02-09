package com.mercandalli.android.apps.files.support;

import android.content.Context;

import com.mercandalli.android.apps.files.file.cloud.response.MyResponse;

import java.util.List;

public class SupportCommentsResponse extends MyResponse<SupportCommentResponse> {

    @Override
    public List<SupportCommentResponse> getResult(final Context context) {
        return super.getResult(context);
    }

    public String getToast() {
        return mToast;
    }
}
