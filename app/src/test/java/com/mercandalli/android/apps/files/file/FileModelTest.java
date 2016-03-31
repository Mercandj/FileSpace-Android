package com.mercandalli.android.apps.files.file;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class FileModelTest {

    @Test
    public void createFileModelFromFileNotExist() {
        try {
            new FileModel.FileModelBuilder()
                    .file(new File(""))
                    .build();
            fail("Should throw exception when file not exists");
        } catch (Exception ignored) {

        }
    }

    @Test
    public void createFileModelFromFileNull() {
        try {
            new FileModel.FileModelBuilder()
                    .file(null)
                    .build();
            fail("Should throw exception when file is null");
        } catch (Exception ignored) {

        }
    }

    @Test
    public void createFileModel() {
        final FileModel fileModel = new FileModel.FileModelBuilder()
                .id(1234)
                .isOnline(false)
                .build();
        assertFalse(fileModel.isOnline());
        assertEquals(fileModel.getId(), 1234);
    }

}
