package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by owainbrown on 13/01/16.
 */
public class TestMeasurementServiceDefaultCallback {

    /*private class Callback extends MeasurementServiceDefaultCallback{

        @Override
        public void MeasurementServiceDidRegister(MeasurementService service, String mobileTrackingID) {
        }

        @Override
        public void MeasurementServiceDidRegisterDidRetrieveDeepLink(MeasurementService service, String deeplink) {
        }
    }

    @Test //worlds simplest test of the worlds simplest class.  Default class doesn't open deep links.
    public void testCallbackDeeplinkOpen()
    {
        Callback callback = new Callback();
        Assert.assertFalse(callback.MeasurementServiceWillOpenDeepLink(mock(MeasurementService.class), "adeeplink"));
    }*/
}
