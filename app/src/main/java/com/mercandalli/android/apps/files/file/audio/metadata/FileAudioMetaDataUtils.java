package com.mercandalli.android.apps.files.file.audio.metadata;

import android.support.annotation.Nullable;
import android.util.Log;

import com.mercandalli.android.apps.files.precondition.Preconditions;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

public class FileAudioMetaDataUtils {

    private static final String TAG = "FileAudioMetaDataUtils";

    public static boolean setMetaData(
            final File file,
            @Nullable final String newTitle,
            @Nullable final String newArtist,
            @Nullable final String newAlbum) {

        Preconditions.checkNotNull(file);

        if (newTitle == null && newArtist == null && newAlbum == null) {
            return true;
        }
        if (!file.exists()) {
            return false;
        }
        AudioFile audioFile = null;
        try {
            audioFile = AudioFileIO.read(file);
        } catch (CannotReadException | TagException | IOException | InvalidAudioFrameException |
                ReadOnlyFileException e) {
            Log.e(TAG, "Cannot get AudioFile");
        }
        if (audioFile == null) {
            return false;
        }
        final Tag tag = audioFile.getTag();
        if (tag != null) {
            try {
                if (newTitle != null) {
                    tag.setField(FieldKey.TITLE, newTitle);
                }
                if (newArtist != null) {
                    tag.setField(FieldKey.ARTIST, newArtist);
                }
                if (newAlbum != null) {
                    tag.setField(FieldKey.ALBUM, newAlbum);
                }
                audioFile.setTag(tag);
                audioFile.commit();
                return true;
            } catch (FieldDataInvalidException | CannotWriteException e) {
                Log.e(TAG, "Cannot write MetaData");
            }
        }
        return false;
    }

    /* protected */ static boolean equalsString(@Nullable final String str1, @Nullable final String str2) {
        if (str1 == null) {
            return str2 == null || str2.isEmpty();
        }
        if (str2 == null) {
            return str1.isEmpty();
        }
        return str1.equals(str2);
    }
}
