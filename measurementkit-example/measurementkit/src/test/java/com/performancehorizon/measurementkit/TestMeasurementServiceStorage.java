package com.performancehorizon.measurementkit;

import android.content.Context;
import android.content.SharedPreferences;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 04/02/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT, sdk = 21)
public class TestMeasurementServiceStorage {

    Context mockContext;
    SharedPreferences mockPreferences;
    SharedPreferences.Editor mockPreferencesEditor;

    @Before
    public void initialise() {

        mockContext = mock(Context.class);
        mockPreferences = mock(SharedPreferences.class);
        mockPreferencesEditor = mock(SharedPreferences.Editor.class);

        when(mockContext.getSharedPreferences(MeasurementServiceStorage.StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE))
                .thenReturn(mockPreferences);

        when(mockPreferences.edit()).thenReturn(mockPreferencesEditor);
    }

    @Test
    public void testGlobalClearPreferences()
    {
        MeasurementServiceStorage.clearPreferences(mockContext);

        verify(mockPreferencesEditor).clear();
        verify(mockPreferencesEditor).apply();
    }

    @Test
    public void testClearPreferences()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        storage.clearPreferences();

        verify(mockPreferencesEditor).clear();
        verify(mockPreferencesEditor).apply();
    }

    @Test
    public void testLoadFromPreferences()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        when(mockPreferences.getString(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_ID, null))
                .thenReturn("trackingid");
        when(mockPreferences.getBoolean(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_INACTIVE, false))
                .thenReturn(false);
        when(mockPreferences.getBoolean(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_HALT, false))
                .thenReturn(false);
        when(mockPreferences.getString(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_CAMREF, null))
                .thenReturn("camref");
        when(mockPreferences.getString(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_REFERRER, null))
                .thenReturn("referrer");

        MeasurementService.MeasurementServiceStatus status= storage.loadFromPreferences();

        Assert.assertEquals("trackingid", storage.getTrackingID());
        Assert.assertEquals(false, storage.getIsTrackingInactive());
        Assert.assertEquals(false, storage.getIsTrackingHalted());
        Assert.assertEquals("camref", storage.getCamRef());
        Assert.assertEquals("referrer", storage.getReferrer());

        Assert.assertEquals(status, MeasurementService.MeasurementServiceStatus.QUERYING);
    }

    @Test
    public void testPutTrackingID()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        storage.putTrackingID("trackingID");

        verify(mockPreferencesEditor).putString(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_ID, "trackingID");
        verify(mockPreferencesEditor).putBoolean(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_INACTIVE, false);
        verify(mockPreferencesEditor).apply();

        Assert.assertEquals(storage.getTrackingID(), "trackingID");
    }

    @Test
    public void testPutTrackingInactive()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        storage.putTrackingInactive();

        verify(mockPreferencesEditor).putBoolean(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_INACTIVE, true);
        verify(mockPreferencesEditor).apply();

        Assert.assertEquals(storage.getIsTrackingInactive(), true);
    }

    @Test
    public void testPutTrackingHalted()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        storage.putHalted(true);

        verify(mockPreferencesEditor).putBoolean(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_HALT, true);
        verify(mockPreferencesEditor).apply();

        Assert.assertEquals(storage.getIsTrackingHalted(), true);
    }

    @Test
    public void testPutCamref()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);
        storage.putCamrefQuery("camref");

        verify(mockPreferencesEditor).putString(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_CAMREF, "camref");
        verify(mockPreferencesEditor).apply();

        Assert.assertEquals(storage.getCamRef(), "camref");
    }

    @Test
    public void testPutReferrer()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);
        storage.putReferrerQuery("referrer");

        verify(mockPreferencesEditor).putString(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_REFERRER, "referrer");
        verify(mockPreferencesEditor).apply();

        Assert.assertEquals(storage.getReferrer(), "referrer");
    }

    @Test
    public void testClearCamref()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);
        storage.clearCamref();

        verify(mockPreferencesEditor).remove(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_CAMREF);
        verify(mockPreferencesEditor).apply();

        Assert.assertNull(storage.getCamRef());
    }

    @Test
    public void testClearReferrer()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);
        storage.clearReferrer();

        verify(mockPreferencesEditor).remove(MeasurementServiceStorage.StorageConstants.TRACKING_PREF_REFERRER);
        verify(mockPreferencesEditor).apply();

        Assert.assertNull(storage.getReferrer());
    }

    @Test
    public void testStatusHalted()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        storage.putHalted(true);

        Assert.assertEquals(storage.status(), MeasurementService.MeasurementServiceStatus.HALTED);
    }

    @Test
    public void testStatusQueryingWithCamref()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        storage.putHalted(false);
        storage.putCamrefQuery("camref");

        Assert.assertEquals(storage.status(), MeasurementService.MeasurementServiceStatus.QUERYING);
    }

    @Test
    public void testStatusQueryingWithReferrer()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        storage.putHalted(false);
        storage.putReferrerQuery("referrer");

        Assert.assertEquals(storage.status(), MeasurementService.MeasurementServiceStatus.QUERYING);
    }

    @Test
    public void testStatusInactive()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        storage.putHalted(false);
        storage.putTrackingInactive();

        Assert.assertEquals(storage.status(), MeasurementService.MeasurementServiceStatus.INACTIVE);
    }

    @Test
    public void testStatusQueryingAsDefault()
    {
        MeasurementServiceStorage storage = new MeasurementServiceStorage(mockContext);

        storage.putHalted(false);

        Assert.assertEquals(storage.status(), MeasurementService.MeasurementServiceStatus.QUERYING);
    }


}
