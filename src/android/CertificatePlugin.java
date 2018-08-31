package com.cordova.certificate;

import android.content.res.Resources;
import android.support.annotation.RawRes;
import android.util.Log;

import com.humana.enrollmenthub.R;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CertificatePlugin extends CordovaPlugin {
    private final String LOG_TAG = "Certificate";

    private static int getResId(String resName) {

        return Resources.getSystem().getIdentifier(resName, "raw", "android");
    }


    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        // throws JSONException

        if (action.equals("isCertificate")) {
            isCertificateValid(callbackContext);
            return true;

        }
        if (action.equals("isPinCertificate")) {
            validatePinCertificate(callbackContext);
            return true;
        }
        return true;
    }

    private void validatePinCertificate(CallbackContext callbackContext) {

        String domainName = preferences.getString("pinDomainName", "google.com");
        String testUrl = preferences.getString("pinTestUrl", "https://enrollmenthub.humana.com/eh/#/");
        String pinOne = preferences.getString("pinOne", null);
        String pinTwo = preferences.getString("pinTwo", null);
        String pinThree = preferences.getString("pinThree", null);
        List<String> list = new ArrayList<String>();
        if (pinOne != null) {
            list.add(pinOne);
        }
        if (pinTwo != null) {
            list.add(pinTwo);
        }
        if (pinThree != null) {
            list.add(pinThree);
        }

        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(domainName, list.toArray(new String[list.size()]))
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                .build();
        Request request = new Request.Builder()
                .url(testUrl)
                .build();
        try {
            client.newCall(request).execute();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
        }
    }

    private void isCertificateValid(CallbackContext callbackContext) {
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt

            InputStream caInput = new BufferedInputStream(cordova.getActivity().getResources().openRawResource(getResId(preferences.getString("certificateName", "google_com"))));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            String urlString = preferences.getString("url", "https://google.com");
            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection =
                    (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            InputStream in = urlConnection.getInputStream();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Security First");
            Log.d("Error", "Security  First");
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, false));
        }
    }


}
