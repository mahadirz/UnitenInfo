package my.madet.function;

/**
 * Created by Mahadir on 3/4/2016.
 */
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
public class HttpsClient {

    private String contents = "";
    public static String TAG = "HttpsClient";
    private boolean debug = false;

    /**
     * Get the contents after the url was opened
     * @return
     */
    public String getContents()
    {
        return contents;
    }

    /**
     * Set Debug
     * @param val
     */
    public void setDebug(boolean val)
    {
        debug = val;
    }

    public HttpsClient open(String https_url)
    {
        URL url;
        try {

            url = new URL(https_url);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            if (debug) {
                //dumpl all cert info
                //print_https_cert(con);
            }
            retrieveContent(con);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;

    }

    private void print_https_cert(HttpsURLConnection con){

        if(con!=null){

            try {

                Log.d(TAG, "Response Code : " + con.getResponseCode());
                Log.d(TAG, "Cipher Suite : " + con.getCipherSuite());
                Log.d(TAG, "\n");

                Certificate[] certs = con.getServerCertificates();
                for(Certificate cert : certs){
                    Log.d(TAG, "Cert Type : " + cert.getType());
                    Log.d(TAG, "Cert Hash Code : " + cert.hashCode());
                    Log.d(TAG, "Cert Public Key Algorithm : "
                            + cert.getPublicKey().getAlgorithm());
                    Log.d(TAG, "Cert Public Key Format : "
                            + cert.getPublicKey().getFormat());
                    Log.d(TAG, "\n");
                }

            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

        }

    }

    private void retrieveContent(HttpsURLConnection con){
        contents = "";
        if(con!=null){

            try {
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                String input;
                String result = "";
                while ((input = br.readLine()) != null){
                    result += input;
                }
                br.close();
                contents = result;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
