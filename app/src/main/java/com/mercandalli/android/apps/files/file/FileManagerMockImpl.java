package com.mercandalli.android.apps.files.file;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;
import com.mercandalli.android.apps.files.file.local.FileLocalApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercandalli.android.apps.files.file.FileUtils.getNameFromPath;

/**
 * A MockUp to test.
 */
public class FileManagerMockImpl extends FileManagerImpl {

    public FileManagerMockImpl(Context contextApp, FileOnlineApi fileOnlineApi) {
        super(contextApp, fileOnlineApi);
    }
}
