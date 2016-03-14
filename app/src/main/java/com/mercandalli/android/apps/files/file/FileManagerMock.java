package com.mercandalli.android.apps.files.file;

import android.content.Context;

import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;

/**
 * A MockUp to test.
 */
public class FileManagerMock extends FileManagerImpl {

    public FileManagerMock(Context contextApp, FileOnlineApi fileOnlineApi) {
        super(contextApp, fileOnlineApi);
    }
}
