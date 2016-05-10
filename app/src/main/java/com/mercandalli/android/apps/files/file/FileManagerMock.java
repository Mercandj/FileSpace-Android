package com.mercandalli.android.apps.files.file;

import android.content.Context;

import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;
import com.mercandalli.android.apps.files.file.cloud.FileUploadOnlineApi;

/**
 * A MockUp to test.
 */
@SuppressWarnings("unused")
/* package */ class FileManagerMock extends FileManagerImpl {

    /* package */ FileManagerMock(
            final Context contextApp,
            final FileOnlineApi fileOnlineApi,
            final FileUploadOnlineApi fileUploadOnlineApi) {
        super(contextApp, fileOnlineApi, fileUploadOnlineApi);
    }
}
