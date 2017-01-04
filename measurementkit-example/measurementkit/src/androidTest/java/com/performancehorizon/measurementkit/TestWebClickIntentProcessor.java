package com.performancehorizon.measurementkit;

import android.content.Intent;
import android.net.Uri;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by owainbrown on 04/01/2017.
 */

public class TestWebClickIntentProcessor {

    @Test
    public void testWebIntentWithNoMobileTrackingID() {

        Intent nomobiletrackingidintent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ourwebsite.com/apage"));

        WebClickIntentProccessor processor = new WebClickIntentProccessor(nomobiletrackingidintent);

        Assert.assertNull(processor.getMobileTrackingID());
        Assert.assertNull(processor.getFilteredIntent());
    }

    @Test
    public void testWebIntentWithMobileTrackingID() {
        Intent withmobiletrackingidandscheme = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ourwebsite.com/apage?phn_mtid=bob"));

        WebClickIntentProccessor processor = new WebClickIntentProccessor(withmobiletrackingidandscheme);

        Assert.assertEquals(processor.getMobileTrackingID(), "bob");
        Assert.assertEquals(processor.getFilteredIntent().getData(), Uri.parse("http://www.ourwebsite.com/apage"));;
    }

    @Test
    public void testWebIntentWithMobileTrackingIDAndCustomScheme() {
        Intent withmobiletrackingidandscheme = new Intent(Intent.ACTION_VIEW, Uri.parse("exactview://open?phn_mtid=bob"));

        WebClickIntentProccessor processor = new WebClickIntentProccessor(withmobiletrackingidandscheme);

        Assert.assertEquals(processor.getMobileTrackingID(), "bob");
        Assert.assertEquals(processor.getFilteredIntent().getData(), Uri.parse("exactview://open"));
    }

    @Test
    public void testWillPreserveOtherQueryArgs() {
        Intent withmobiletrackingidandscheme = new Intent(Intent.ACTION_VIEW, Uri.parse("exactview://open?phn_mtid=bob&someotherparam=steve"));

        WebClickIntentProccessor processor = new WebClickIntentProccessor(withmobiletrackingidandscheme);

        Assert.assertEquals(processor.getMobileTrackingID(), "bob");
        Assert.assertEquals(processor.getFilteredIntent().getData(), Uri.parse("exactview://open?someotherparam=steve"));
    }

    @Test
    public void testWrongIntentAction() {
        Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("content://contacts/people/1"));

        WebClickIntentProccessor processor = new WebClickIntentProccessor(dial);

        Assert.assertNull(processor.getMobileTrackingID());
        Assert.assertNull(processor.getFilteredIntent());
    }

    @Test
    public void testNoUriAction() {
        Intent nouri = new Intent(Intent.ACTION_VIEW);

        WebClickIntentProccessor processor = new WebClickIntentProccessor(nouri);

        Assert.assertNull(processor.getMobileTrackingID());
        Assert.assertNull(processor.getFilteredIntent());
    }
}
