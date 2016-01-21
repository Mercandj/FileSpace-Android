package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;

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

public class FileAudioMetaUtils {

    public static boolean setArtist(
            final Context context,
            final File file,
            final String artistName) {

        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(artistName);

        if (!file.exists()) {
            return false;
        }
        AudioFile audioFile = null;
        try {
            audioFile = AudioFileIO.read(file);
        } catch (CannotReadException | TagException | IOException | InvalidAudioFrameException |
                ReadOnlyFileException e) {
            e.printStackTrace();
        }
        if (audioFile == null) {
            return false;
        }
        final Tag tag = audioFile.getTag();
        if (tag != null) {
            try {
                tag.setField(FieldKey.ARTIST, artistName);
                audioFile.setTag(tag);
                audioFile.commit();
                return true;
            } catch (FieldDataInvalidException | CannotWriteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
