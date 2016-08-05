package org.edx.mobile.test;

import android.content.Intent;
import android.net.Uri;

import org.edx.mobile.util.BrowserUtil;
import org.edx.mobile.util.Sha1Util;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class UtilTests extends BaseTestCase {

    @Test
    public void testBrowserOpenUrl() throws Exception {
        String url = "https://courses.edx.org/register";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        RuntimeEnvironment.application.startActivity(intent);

        print("finished open URL in browser");
    }

    @Test
    public void testHostAndUrls() throws Exception {
        String host = "edx.org";

        assertTrue(BrowserUtil.isUrlOfHost("http://www.edx.org", host));
        assertTrue(BrowserUtil.isUrlOfHost("https://courses.edx.org", host));
        assertTrue(BrowserUtil.isUrlOfHost("https://edx.org/", host));
        assertFalse(BrowserUtil.isUrlOfHost("https://fake-domain.com/edx.org/", host));
        assertFalse(BrowserUtil.isUrlOfHost("https://fake-domain.com/xyz/", host));
    }

    @Test
    public void testSha1Encryption() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        assertEquals("94ca247fff5ad413788a1c8d8c80394a246dba1c", Sha1Util.SHA1("khalid"));
        assertEquals("d52f2b07afef758721dd630fcbc15f83fa2e42aa", Sha1Util.SHA1("some_vague_string"));
    }
}
