/*
 * The MIT License (MIT)
 * Copyright (c) 2014 Mahadir Ahmad
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * 
 */

/**
 * File cache Class
 * Handle the caching of time table
 *
 * @author Mahadir Ahmad
 * @version 1.0
 * 
 */
package my.madet.function;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import my.madet.uniteninfo.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FileCache {
    
    private File cacheDir;
    private File cacheProfileDir;
    private final static String rootDir = "UnitenInfo";
    
    private final static String htmlFileName = "timetable.html";
    private final static String htmlStyleCss = "styles.css";
    private final static String screenShotFile = "screenshot.png";
    
    private String studentId;
    
    public FileCache(Context context,String id){
    	
    	studentId = id;
    	
        //Find the dir at SDCARD to save cached images
    	
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
        	//if SDCARD is mounted (SDCARD is present on device and mounted)
        	cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),rootDir);
        	cacheProfileDir=new File(android.os.Environment.getExternalStorageDirectory(),rootDir+"/"+id);
        	
        }
        else
        {
        	// if checking on simulator the create cache dir in your application context
            cacheDir=context.getCacheDir();
        }
        
        if(!cacheDir.exists()){
        	// create cache dir in your application context
            cacheDir.mkdirs();
        }
        if(!cacheProfileDir.exists()){
        	// create cache dir in your application context
        	cacheProfileDir.mkdirs();
        }
        
        File file = new File(android.os.Environment.getExternalStorageDirectory(), rootDir+"/logo.png");
        if(!file.exists()){
        	try {
				saveUnitenLogoToSdCard(context,id);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    private void saveUnitenLogoToSdCard(Context c,String id) throws IOException{
    	FileOutputStream outStream;
    	Bitmap bm = BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_launcher);
    	File file = new File(android.os.Environment.getExternalStorageDirectory(), rootDir+"/logo.png");
		outStream = new FileOutputStream(file);
		bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
	    outStream.flush();
	    outStream.close();        
    }
    
    public String screenShotPath(){
    	String path = android.os.Environment.getExternalStorageDirectory()+"/"+rootDir+"/"+studentId+"/"+screenShotFile;
    	return path;
    }
    
    public String htmlTimeTablePath(){
    	String path = "file://"+android.os.Environment.getExternalStorageDirectory()+"/"+rootDir+"/"+studentId+"/"+htmlFileName;
    	return path;
    }
    
    public boolean isHtmlTimeTableExist(){
    	try {
    		File myFile = new File(cacheProfileDir,htmlFileName);
    		
    		if(myFile.exists())
    			return true;
    		else
    			return false;
    	} 
    	catch (Exception e) {
    		Log.e("saveHtmlTimeTable","saveHtmlTimeTable error "+e.toString());
    		return false;
    	}
    	
    }
    
    public void saveHtmlTimeTable(String data){
    	// write on SD card file data in the text box
    	try {
    		File myFile = new File(cacheProfileDir,htmlFileName);
    		
    		if(myFile.exists())
    			myFile.delete();
    		
    		myFile.createNewFile();
    		FileOutputStream fOut = new FileOutputStream(myFile);
    		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
    		myOutWriter.append(data);
    		myOutWriter.close();
    		fOut.close();
    	} 
    	catch (Exception e) {
    		Log.e("saveHtmlTimeTable","saveHtmlTimeTable error "+e.toString());
    	}
    }
    
    public void saveHtmlStyleCss(String data){
    	// write on SD card file data in the text box
    	try {
    		File myFile = new File(cacheProfileDir,htmlStyleCss);
    		
    		if(myFile.exists())
    			myFile.delete();
    		
    		myFile.createNewFile();
    		FileOutputStream fOut = new FileOutputStream(myFile);
    		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
    		myOutWriter.append(data);
    		myOutWriter.close();
    		fOut.close();
    	} 
    	catch (Exception e) {
    		Log.e("saveHtmlStyleCss","saveHtmlStyleCss error "+e.toString());
    	}
    }
       
    
    
    public File getFile(String url){
        //Identify images by hashcode or encode by URLEncoder.encode.
        String filename=String.valueOf(url.hashCode());
        
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
    	// list all files inside cache directory
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        //delete all cache directory files
        for(File f:files)
            f.delete();
    }

}
