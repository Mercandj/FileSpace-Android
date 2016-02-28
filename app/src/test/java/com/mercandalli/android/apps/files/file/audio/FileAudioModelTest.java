package com.mercandalli.android.apps.files.file.audio;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static junit.framework.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class FileAudioModelTest {

    @Test
    public void createFileAudioModelFromFileNotExist() {
        try {
            new FileAudioModel.FileAudioModelBuilder()
                    .file(new File(""))
                    .build();
            fail("Should throw exception when file not exists");
        } catch (Exception ignored) {

        }
    }

    @Test
    public void createFileAudioModelFromFileNull() {
        try {
            new FileAudioModel.FileAudioModelBuilder()
                    .file(null)
                    .build();
            fail("Should throw exception when file is null");
        } catch (Exception ignored) {

        }
    }

    @Test
    public void createFileAudioModel() {
        final FileAudioModel.FileAudioModelBuilder fileAudioModelBuilder = new FileAudioModel.FileAudioModelBuilder();
        fileAudioModelBuilder
                .id(1234)
                .isOnline(false);
        final FileAudioModel fileAudioModel = fileAudioModelBuilder.build();


        Assert.assertFalse(fileAudioModel.isOnline());
        Assert.assertTrue(fileAudioModel.getId() == 1234);
    }

}
