package com.performancehorizon.mobiletracking;


import android.support.v4.util.ArrayMap;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import junit.framework.TestCase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by owainbrown on 17/03/15.
 * Liberally C&P'ed from AFToolsTest.
 */
public class Test_RequestTools extends InstrumentationTestCase {

    @Before
    public void setUp() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }

    public void testMapShouldConvertToList()
    {
        //set up parameter data.
        ArrayMap<String, Object> parameters = new ArrayMap<>();
        ArrayList<Map<String, Object>> publishers = new ArrayList<Map<String, Object>>();

        ArrayMap<String, Object> publisher1 = new ArrayMap<>();
        ArrayMap<String, Object> publisher2 = new ArrayMap<>();

        publisher1.put("pub_id", "12");
        publisher1.put("pub_name", "Dennis");

        publisher2.put("pub_id", "13");
        publisher2.put("pub_name", "Steven");

        publishers.add(publisher1);
        publishers.add(publisher2);

        parameters.put("publishers", publishers);

        //set up the "correct" output.
        List<NameValuePair> correctparameters = new ArrayList<NameValuePair>();

        correctparameters.add(new BasicNameValuePair("publishers[0][pub_id]", "12"));
        correctparameters.add(new BasicNameValuePair("publishers[0][pub_name]", "Dennis"));
        correctparameters.add(new BasicNameValuePair("publishers[1][pub_id]", "13"));
        correctparameters.add(new BasicNameValuePair("publishers[1][pub_name]", "Steven"));

        List<NameValuePair> generatedparameters = RequestTools.convertParameterMapToList(parameters);

        assertEquals(generatedparameters, correctparameters);
    }

    @SmallTest
    public void testNullHandling()
    {
        HashMap<String, Object> parameters = new HashMap<>();

        parameters.put("ace", "ace");
        parameters.put("not ace", null);

        List<NameValuePair> correctparameters = new ArrayList<>();
        correctparameters.add(new BasicNameValuePair("ace", "ace"));

        List<NameValuePair> generatedparameters = RequestTools.convertParameterMapToList(parameters);
        assertEquals(generatedparameters, correctparameters);
    }
}
