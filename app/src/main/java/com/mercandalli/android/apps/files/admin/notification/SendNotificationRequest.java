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
package com.mercandalli.android.apps.files.admin.notification;

import com.google.gson.annotations.SerializedName;
import com.mercandalli.android.library.base.push.PushManager;

public class SendNotificationRequest {

    @SerializedName(PushManager.PUSH_KEY_TYPE)
    private final String mType;

    @SerializedName(PushManager.PUSH_KEY_NOTIFICATION_TITLE)
    private final String mTitle;

    @SerializedName(PushManager.PUSH_KEY_NOTIFICATION_MESSAGE)
    private final String mMessage;

    @SerializedName(PushManager.PUSH_KEY_ACTION_DATA)
    private final String mActionData;

    public SendNotificationRequest(
            final String type,
            final String title,
            final String message,
            final String actionData) {
        mType = type;
        mTitle = title;
        mMessage = message;
        mActionData = actionData;
    }
}
