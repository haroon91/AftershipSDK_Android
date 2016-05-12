package com.aftershipsdk100;

import com.aftershipsdk100.sdk.api.ApiDataManager;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class JSONResponseParseTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    ApiDataManager apiDataManager = ApiDataManager.getInstance();

    @Test
    public void parseTrackingTest_Null () {
        JSONObject jsonObject = null;

        




    }
}