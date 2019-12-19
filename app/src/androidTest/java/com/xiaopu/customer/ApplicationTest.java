package com.xiaopu.customer;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.xiaopu.customer.utils.EditTextUtils;

import org.junit.Assert;
import org.junit.Test;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    @Test
    public void testPhone(){
        Assert.assertEquals(EditTextUtils.isMobileNO("18978781234"),true);
    }
}
