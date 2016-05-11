package com.mercandalli.android.apps.files.file.audio.metadata;

import android.support.annotation.Nullable;

import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.library.base.precondition.Preconditions;

import java.io.File;

/*
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
*/

public class FileAudioMetaDataUtils {

    public static boolean isMetaDataEditable(FileAudioModel fileAudioModel) {
        return false;//fileAudioModel.getPath().endsWith(".mp3");
    }

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
        // Try to remove 'org.jaudiotagger:jaudiotagger:2.0.1'
        /*
        AudioFile audioFile = null;
        try {
            audioFile = AudioFileIO.read(file);
        } catch (CannotReadException | TagException | IOException | InvalidAudioFrameException |
                ReadOnlyFileException e) {
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
            }
        }
        */
        return false;
    }
}
