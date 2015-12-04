package com.mercandalli.android.apps.files.file;

import com.mercandalli.android.apps.files.file.cloud.FileOnlineApi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import retrofit.mime.TypedFile;

/**
 * Used by {@link FileOnlineApi}.
 */
public class FileUploadTypedFile extends TypedFile {

    private static final int BUFFER_SIZE = 4096;
    private static final int MISSED_CALLBACK = 15;

    /**
     * The upload listener.
     */
    private final FileUploadListener mFileUploadListener;

    /**
     * The {@link FileModel} to upload.
     */
    private final FileModel mFileModel;

    public FileUploadTypedFile(String mimeType, FileModel file, FileUploadListener fileUploadListener) {
        super(mimeType, file.getFile());
        mFileModel = file;
        this.mFileUploadListener = fileUploadListener;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        FileInputStream in = new FileInputStream(super.file());
        long total = 0;
        try {
            int read;
            int currentMissedValue = 0;
            while ((read = in.read(buffer)) != -1) {
                total += read;
                if (currentMissedValue >= MISSED_CALLBACK) {
                    this.mFileUploadListener.onFileUploadProgress(mFileModel, total, super.file().length());
                    currentMissedValue = 0;
                } else {
                    currentMissedValue++;
                }
                out.write(buffer, 0, read);
            }
        } finally {
            in.close();
            mFileUploadListener.onFileUploadFinished(mFileModel);
        }
    }

    interface FileUploadListener {
        void onFileUploadProgress(FileModel fileModel, long progress, long length);

        void onFileUploadFinished(FileModel fileModel);
    }
}
