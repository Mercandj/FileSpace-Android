package com.mercandalli.android.apps.files.support;

import android.test.suitebuilder.annotation.SmallTest;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class SupportUtilsTest {

    @Test
    public void equalsNullable() {
        Assert.assertThat(SupportUtils.equalsString("toto", "tata"), Is.is(false));
    }
}
