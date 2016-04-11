package com.mercandalli.android.apps.files.file;

import android.content.Context;

import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;

/**
 * A MockUp to test.
 */
@SuppressWarnings("unused")
/* package */ class FileManagerMock extends FileManagerImpl {

    /* package */ FileManagerMock(final Context contextApp, final FileOnlineApi fileOnlineApi) {
        super(contextApp, fileOnlineApi);
    }
}
