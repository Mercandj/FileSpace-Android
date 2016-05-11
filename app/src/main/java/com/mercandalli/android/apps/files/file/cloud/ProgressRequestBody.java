package com.mercandalli.android.apps.files.file.cloud;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercandalli.android.library.base.precondition.Preconditions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {

    private static final int DEFAULT_BUFFER_SIZE = 2_048;
    private static final int TIME_REFRESH_MS = 50;

    private File mFile;

    private long mUploadedSize;
    private long mTotalSize;

    @Nullable
    private UploadCallbacks mListener;

    private final MediaType mMediaType;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ProgressUpdater mProgressUpdater = new ProgressUpdater();

    public interface UploadCallbacks {
        void onUploadProgressUpdate(final long progress, final long length);

        void onUploadError();

        void onUploadFinish();
    }

    public ProgressRequestBody(MediaType mediaType) {
        mMediaType = mediaType;
    }

    public ProgressRequestBody(final MediaType mediaType, final File file, @Nullable final UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
        mMediaType = mediaType;
    }

    @Override
    public MediaType contentType() {
        return mMediaType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mTotalSize = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        mUploadedSize = 0;

        if (mListener != null) {
            mHandler.postDelayed(mProgressUpdater, TIME_REFRESH_MS);
        }
        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                mUploadedSize += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
        if (mListener != null) {
            mHandler.removeCallbacks(mProgressUpdater);
            mListener.onUploadFinish();
        }
    }

    @Override
    public long contentLength() {
        if (mFile != null) {
            return mFile.length();
        }
        return 0;
    }

    private class ProgressUpdater implements Runnable {
        public ProgressUpdater() {
        }

        @Override
        public void run() {
            if (mListener != null) {
                mListener.onUploadProgressUpdate(mUploadedSize, mTotalSize);
            }
            mHandler.postDelayed(mProgressUpdater, TIME_REFRESH_MS);
        }
    }

    /**
     * Returns a new request body that transmits the content of {@code file}.
     */
    public static ProgressRequestBody create(
            @NonNull final MediaType contentType,
            @NonNull final File file,
            @Nullable final UploadCallbacks listener) {
        Preconditions.checkNotNull(contentType);
        Preconditions.checkNotNull(file);
        return new ProgressRequestBody(contentType, file, listener);
    }

    /**
     * Returns a new request body that transmits {@code content}. If {@code contentType} is non-null
     * and lacks a charset, this will use UTF-8.
     */
    public static ProgressRequestBody create(final MediaType contentTypeParam, final String contentStr) {
        Charset charset;
        final MediaType contentType;
        if (contentTypeParam != null) {
            charset = contentTypeParam.charset();
            if (charset == null) {
                charset = Util.UTF_8;
                contentType = MediaType.parse(contentTypeParam + "; charset=utf-8");
            } else {
                contentType = contentTypeParam;
            }
        } else {
            throw new IllegalStateException("contentTypeParam == null");
        }
        final byte[] content = contentStr.getBytes(charset);
        Util.checkOffsetAndCount(content.length, 0, content.length);
        return new ProgressRequestBody(contentType) {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return content.length;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(content, 0, content.length);
            }
        };
    }
}
