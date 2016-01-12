package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.mercandalli.android.apps.files.common.listener.ResultCallback;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.FileCache;
import com.mercandalli.android.apps.files.file.FileTypeModel;
import com.mercandalli.android.apps.files.file.FileTypeModelENUM;
import com.mercandalli.android.apps.files.file.FileUtils;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.precondition.Preconditions;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercandalli.android.apps.files.file.FileUtils.getNameFromPath;

/**
 * A {@link FileModel} Manager.
 */
public class FileAudioManagerImpl extends FileAudioManager {

    private static final String LIKE = " LIKE ?";

    private Context mContextApp;
    private FileAudioCache mFileAudioCache;

    public FileAudioManagerImpl(Context contextApp) {
        Preconditions.checkNotNull(contextApp);

        mContextApp = contextApp;
        mFileAudioCache = new FileAudioCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getLocalMusic(final Context context, final int sortMode, final String search, final ResultCallback<List<FileAudioModel>> resultCallback) {
        final List<FileAudioModel> cachedLocalMusics = mFileAudioCache.getLocalMusics();
        if (!cachedLocalMusics.isEmpty()) {
            resultCallback.success(cachedLocalMusics);
            return;
        }

        new AsyncTask<Void, Void, List<FileAudioModel>>() {
            @Override
            protected List<FileAudioModel> doInBackground(Void... params) {
                final List<FileAudioModel> files = new ArrayList<>();

                final String[] PROJECTION = {MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

                for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection += " OR " + MediaStore.Files.FileColumns.DATA + LIKE;
                    searchArray.add("%" + end);
                }
                selection += " )";

                if (search != null && !search.isEmpty()) {
                    searchArray.add("%" + search + "%");
                    selection += " AND " + MediaStore.Files.FileColumns.DISPLAY_NAME + LIKE;
                }

                final Cursor cursor = context.getContentResolver().query(allSongsUri, PROJECTION, selection, searchArray.toArray(new String[searchArray.size()]), null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            final File file = new File(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                            if (file.exists() && !file.isDirectory()) {
                                FileAudioModel.FileMusicModelBuilder fileMusicModelBuilder = new FileAudioModel.FileMusicModelBuilder()
                                        .file(file);
                                try {
                                    MusicMetadataSet musicMetadataSet = new MyID3().read(file);
                                    if (musicMetadataSet != null) {
                                        IMusicMetadata metadata = musicMetadataSet.getSimplified();
                                        fileMusicModelBuilder.album(metadata.getAlbum());
                                        fileMusicModelBuilder.artist(metadata.getArtist());
                                    }
                                } catch (IOException e) {
                                    Log.e(getClass().getName(), "Exception", e);
                                } // read metadata

                                //if (mSortMode == SharedAudioPlayerUtils.SORT_SIZE)
                                //    fileMusicModel.adapterTitleStart = FileUtils.humanReadableByteCount(fileMusicModel.getSize()) + " - ";

                                files.add(fileMusicModelBuilder.build());
                            }

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                if (sortMode == Constants.SORT_ABC) {
                    Collections.sort(files, new Comparator<FileAudioModel>() {
                        @Override
                        public int compare(final FileAudioModel f1, final FileAudioModel f2) {
                            if (f1.getName() == null || f2.getName() == null) {
                                return 0;
                            }
                            return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
                        }
                    });
                } else if (sortMode == Constants.SORT_SIZE) {
                    Collections.sort(files, new Comparator<FileAudioModel>() {
                        @Override
                        public int compare(final FileAudioModel f1, final FileAudioModel f2) {
                            return (new Long(f2.getSize())).compareTo(f1.getSize());
                        }
                    });
                } else {
                    final Map<FileModel, Long> staticLastModifiedTimes = new HashMap<>();
                    for (FileModel f : files) {
                        staticLastModifiedTimes.put(f, f.getLastModified());
                    }
                    Collections.sort(files, new Comparator<FileAudioModel>() {
                        @Override
                        public int compare(final FileAudioModel f1, final FileAudioModel f2) {
                            return staticLastModifiedTimes.get(f2).compareTo(staticLastModifiedTimes.get(f1));
                        }
                    });
                }

                return files;
            }

            @Override
            protected void onPostExecute(List<FileAudioModel> fileModels) {
                resultCallback.success(fileModels);
                mFileAudioCache.setLocalMusics(fileModels);
                super.onPostExecute(fileModels);
            }
        }.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getLocalMusic(final Context context, final FileModel fileModelDirectParent, final int sortMode, final String search, final ResultCallback<List<FileAudioModel>> resultCallback) {
        Preconditions.checkNotNull(fileModelDirectParent);
        Preconditions.checkNotNull(resultCallback);
        if (!fileModelDirectParent.isDirectory()) {
            resultCallback.failure();
            return;
        }
        final List<FileAudioModel> files = new ArrayList<>();
        List<File> fs = Arrays.asList(fileModelDirectParent.getFile().listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return (new FileTypeModel(FileUtils.getExtensionFromPath(name))).equals(FileTypeModelENUM.AUDIO.type);
                    }
                }
        ));
        for (File file : fs) {
            final FileAudioModel.FileMusicModelBuilder fileMusicModelBuilder =
                    new FileAudioModel.FileMusicModelBuilder().file(file);
            if (file.getName().toLowerCase().endsWith(".mp3")) {
                try {
                    IMusicMetadata metadata = (new MyID3().read(file)).getSimplified();
                    fileMusicModelBuilder.album(metadata.getAlbum());
                    fileMusicModelBuilder.artist(metadata.getArtist());
                } catch (Exception e) {
                    Log.e(getClass().getName(), "Exception", e);
                }
            }
            files.add(fileMusicModelBuilder.build());
        }
        resultCallback.success(files);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getLocalMusicFolders(final Context context, final int sortMode, final String search, final ResultCallback<List<FileModel>> resultCallback) {
        final List<FileModel> cachedLocalMusicFolders = mFileAudioCache.getLocalMusicFolders();
        if (!cachedLocalMusicFolders.isEmpty()) {
            resultCallback.success(cachedLocalMusicFolders);
            return;
        }

        new AsyncTask<Void, Void, List<FileModel>>() {
            @Override
            protected List<FileModel> doInBackground(Void... params) {
                // Used to count the number of music inside.
                final Map<String, MutableInt> directories = new HashMap<>();

                final String[] PROJECTION = new String[]{MediaStore.Files.FileColumns.DATA};

                final Uri allSongsUri = MediaStore.Files.getContentUri("external");
                final List<String> searchArray = new ArrayList<>();

                String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;

                for (String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
                    selection += " OR " + MediaStore.Files.FileColumns.DATA + LIKE;
                    searchArray.add("%" + end);
                }
                selection += " )";

                if (search != null && !search.isEmpty()) {
                    searchArray.add("%" + search + "%");
                    selection += " AND " + MediaStore.Files.FileColumns.DISPLAY_NAME + LIKE;
                }

                final Cursor cursor = context.getContentResolver().query(allSongsUri, PROJECTION, selection, searchArray.toArray(new String[searchArray.size()]), null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            final String parentPath = FileUtils.getParentPathFromPath(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                            final MutableInt count = directories.get(parentPath);
                            if (count == null) {
                                directories.put(parentPath, new MutableInt());
                            } else {
                                count.increment();
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                final List<FileModel> result = new ArrayList<>();
                for (String path : directories.keySet()) {
                    result.add(new FileModel.FileModelBuilder()
                            .id(path.hashCode())
                            .url(path)
                            .name(getNameFromPath(path))
                            .isDirectory(true)
                            .countAudio(directories.get(path).value)
                            .isOnline(false)
                            .build());
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<FileModel> fileModels) {
                resultCallback.success(fileModels);
                mFileAudioCache.setLocalMusicFolders(fileModels);
                super.onPostExecute(fileModels);
            }
        }.execute();
    }
}
