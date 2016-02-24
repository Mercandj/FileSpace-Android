package com.mercandalli.android.apps.files.file;

import android.test.suitebuilder.annotation.SmallTest;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class FileModelTest {

    @Test
    public void fileModelCreation_ShouldCreateModel() {
        final FileModel fileModel = new FileModel.FileModelBuilder()
                .id(12345)
                .isOnline(false)
                .build();

        Assert.assertThat(fileModel.isOnline(), Is.is(false));
    }

}
