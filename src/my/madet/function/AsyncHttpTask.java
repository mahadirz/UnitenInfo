package my.madet.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import my.madet.function.MySSLSocketFactory;

import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import my.madet.function.HttpHandler;
import android.os.AsyncTask;

public class AsyncHttpTask extends AsyncTask<String, Void, String>{

    private HttpHandler httpHandler;
    
    public AsyncHttpTask(HttpHandler httpHandler){
        this.httpHandler = httpHandler;
    }
    
    @Override
	protected void onPreExecute() {
    	httpHandler.preRequest();
    }

    @Override
    protected String doInBackground(String... arg0) {
        InputStream inputStream = null;
        String result = "";
        try {
        	
        	//sni not working so set ALLOW_ALL_HOSTNAME_VERIFIER
        	//this solution taken from 
        	//http://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https
        	KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

        	

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient(ccm, params);
            
            
            // make the http request
            HttpResponse httpResponse = httpclient.execute(httpHandler.getHttpRequestMethod());

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    @Override
    protected void onPostExecute(String result) {
        httpHandler.onResponse(result);
    }

    //--------------------------------------------------------------------------------------------
     private static String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;   
        }
}
