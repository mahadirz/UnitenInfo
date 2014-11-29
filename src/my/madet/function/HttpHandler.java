package my.madet.function;

import org.apache.http.client.methods.HttpUriRequest;
import my.madet.function.AsyncHttpTask;

public abstract class HttpHandler {

    public abstract HttpUriRequest getHttpRequestMethod();

    public abstract void onResponse(String result);
    
    public abstract void preRequest();

    public void execute(){
        new AsyncHttpTask(this).execute();
    } 
}