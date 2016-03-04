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
 * This class is the core of parsing engine 
 * that parse data from info.uniten.edu.my
 *
 * @author Mahadir Ahmad
 * @version 1.0
 * 
 */

package my.madet.function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import my.madet.ntlm.NTLMSchemeFactory;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class HttpParser {

	// class var
	private String unitenid, unitenpassw;
	
	//var for subject list in result
	ArrayList<HashMap<String, String>> resultSubjectList;

	// constructor
	public HttpParser(String id, String passw) {
		this.unitenid = id;
		this.unitenpassw = passw;
		resultSubjectList = new ArrayList<HashMap<String,String>>();
	}
	
	
	

	public String[] bioData() {
		String url = "http://info.uniten.edu.my/info/Ticketing.ASP?WCI=Biodata";
		String[] outPut = new String[9];

		// fetch html from url
		String html = FetchUrL(url);
		if (html == null)
			return null;

		/**
		 * array 0: id array 1: name array n:
		 * status,program,campus,advisor,phone,email array 8: password
		 */
		//String regex = "Student ID:<\\/TD><TD><B>([\\w\\d]+)[\\s\\S]+Name:<\\/TD><TD>([\\w\\s.,/']+)[\\s\\S]+Student Status:<\\/TD><TD>([\\w]+)[\\s\\S]+Program:<\\/TD><TD>([\\w\\s().,/]+)[\\s\\S]+Campus:<\\/TD><TD>([\\w\\s]+)[\\s\\S]+Advisor:<\\/TD><TD>([\\w\\s,./]+)[\\s\\S]+Phone:<\\/TD><TD>([\\d\\+\\s-]+)<\\/TD><\\/TR>[\\s\\S]+EMAIL\" VALUE=\"([\\S]+)\"";
		//http://www.freeformatter.com/java-dotnet-escape.html#ad-output
		String regex = "Student ID:<\\/TD><TD><B>([\\w\\d]+?)\\s*?<\\/B>[\\s\\S]+?Name:<\\/TD><TD>([\\S\\s]+?)<\\/TD>[\\s\\S]+?Student Status:<\\/TD><TD>([\\S\\s]+?)<\\/TD>[\\s\\S]+?Program:<\\/TD><TD>([\\S\\s]+?)<\\/TD>[\\s\\S]+?Campus:<\\/TD><TD>([\\S\\s]+?)<\\/TD>[\\s\\S]+?Advisor:<\\/TD><TD>([\\S\\s]+?)<\\/TD>[\\s\\S]+?NAME=\"CPHONE\"[\\s\\S]+?VALUE=\"([\\S\\s]+?)\">[\\s\\S]+?NAME=\"EMAIL\" VALUE=\"([\\S\\s]+?)\"";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			for (int i = 0; i < 8; i++) {
				Log.i("Regex" + (i + 1), matcher.group(i + 1));
				outPut[i] = matcher.group(i + 1);
			}
			outPut[8] = unitenpassw;
		}

		return outPut;
	}

	/**
	 * 
	 * @param html
	 * @return true if no class notice
	 */
	public boolean NoClassNoticeFound() {
		String url = "http://info.uniten.edu.my/info/Ticketing.ASP?WCI=ClassNotice";
		// fetch html from url
		String html = FetchUrL(url);
		if (html == null)
			return true;

		return html.matches("No Class Notices found");
	}

	/**
	 * 
	 * @param html
	 * @return amount in string format or null
	 */
	public String LedgerBalance() {
		String url = "http://info.uniten.edu.my/info/Ticketing.ASP?WCI=LedgerBalance";
		String balance = null;
		// fetch html from url
		String html = FetchUrL(url);
		if (html == null)
			return null;

		String regex = "Current Balance[</TD>\\s]+ALIGN=\"RIGHT\">([\\d.,-]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		while(matcher.find())
			balance = matcher.group(1);
			
		return balance;
	}

	/**
	 * 
	 * @param html
	 * @return
	 */
	public boolean StatusNotBlocked() {
		String url = "http://info.uniten.edu.my/info/Ticketing.ASP?WCI=LedgerBalance";
		// fetch html from url
		String html = FetchUrL(url);
		if (html == null)
			return false;
		
		
		Pattern pattern = Pattern.compile("you are not blocked",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(html);
		if(matcher.find()){
			//found the string
			return true;
		}
		else
			return false;
	}

	/**
	 * 
	 * @return
	 * Semester name: GPA, CGPA
	 */
	public HashMap<String, String[]> ExamResult() {
		
		String url = "http://info.uniten.edu.my/info/Ticketing.ASP?WCI=Results";

		// fetch html from url
		String html = FetchUrL(url);
		if (html == null || html == "No_Internet" || html == "Unauthorized")
			return null;

		HashMap<String, String[]> result = new HashMap<String, String[]>();

		// retrieve the semester name and url to result
		String regex = "(?i)<a href=\"([\\S]+)\">(.+?)<\\/a>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);

		while (matcher.find()) {
			String html2 = FetchUrL("http://info.uniten.edu.my/info/"
					+ matcher.group(1));

			if (html2 == null || html2 == "No_Internet")
				continue;

			String regex2 = "Grade Point Average:<\\/TD><TD>([\\d.]+)<\\/TD>[\\s\\S]+Cumulative Grade Point Average:<\\/TD><TD>([\\d.]+)";
			Pattern pattern2 = Pattern.compile(regex2);
			Matcher matcher2 = pattern2.matcher(html2);
			
			//save subject list
			resultSubjectList.addAll(getResultSubjectList(html2));

			// save to hasmap
			if (matcher2.find()) {
				result.put(matcher.group(2), new String[] { matcher2.group(1),
						matcher2.group(2) });
			}
		}

		return result;
	}
	
	
	/**
	 * Must execute ExamResult() first
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getResultSubjectList(){
		return resultSubjectList;
	}
	
	

	/**
	 * 
	 * @return array
	 * 0 - Arts & Cultural
	 * 1- Communication & Enterpreneurship
	 * 2- Leadership & Intelectual
	 * 3- Spiritual & Civilization
	 * 4- Sports & Recreational
	 * 5 - total
	 */
	public String[] GetScorun() {
		String url = "http://info.uniten.edu.my/Scorun/ProgressReport.aspx?mode=student";
		// fetch html from url
		String html = FetchUrL(url);
		if (html == null || html == "No_Internet")
			return null;

		String[] output = new String[6];

		Log.i("GetScorun: ", html);

		// fetch the activity point
		String regex = "(?i)size=\"2\"><b>([\\d.]+)\\/[\\d.]+<\\/b>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		int i = 0;
		while (matcher.find()) {
			output[i] = matcher.group(1);
			Log.i("Regex" + i, matcher.group(1));
			i++;
		}

		// capture the total scorun
		regex = "(?i)Total Student Run :<\\/td>[\\s\\S]+size=\"2\">([\\d.]+)<\\/font><\\/span><\\/td>[\\s]+<\\/tr>[\\s]+<tr>";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(html);
		if (matcher.find()) {
			output[i] = matcher.group(1);
			Log.i("Regex" + i, matcher.group(1));
		}

		return output;
	}
	
	
	public String fetchHtmlStyleCss(){
		String urlString = "http://info.uniten.edu.my/info/styles.css";
		return FetchUrL(urlString);
	}
	
	
	/**
	 * get class notices
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getClassNotices(){
		String urlString = "http://info.uniten.edu.my/info/Ticketing.ASP?WCI=ClassNotice";
		String html = FetchUrL(urlString);
		ArrayList<HashMap<String, String>> noticesArrayList = new ArrayList<HashMap<String,String>>();
		// fetch table
		String regex = "(?i)<TR CLASS=\"LINE(1|2)\" VALIGN=\"TOP\">([\\s\\S]+?)</TR>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			//System.out.println(matcher.group(2));
			//System.out.println("\n========\n");
			html = matcher.group(2); // get the last one

			HashMap<String, String> msgHashMap = new HashMap<String,String>();
						
			//fetch title and body
			regex = "(?i)<td>([\\s\\S]+?)</td>";
			pattern = Pattern.compile(regex);
			Matcher matcher2 = pattern.matcher(matcher.group(2));
			int position=0;
			while(matcher2.find()){
				if(position == 1){
					msgHashMap.put("title1", matcher2.group(1));
				}
				else if(position == 2){
					msgHashMap.put("title2", matcher2.group(1));
				}
				else if(position == 3){
					msgHashMap.put("title3", matcher2.group(1));
				}
				else if(position == 4){
					msgHashMap.put("body", matcher2.group(1));
				}
				position++;
			}
			
			noticesArrayList.add(msgHashMap);
			
		}
		return noticesArrayList;
	}
	
		
    /**
     * Fetching timetable html and change
     * a few appearance
     * @return
     */
	public String fetchHtmlTimeTable(){
    	String url = "http://info.uniten.edu.my/info/Ticketing.ASP?WCI=TimeTable";

		// fetch html from url
		String html = FetchUrL(url);
		if (html == null || html == "No_Internet" || html == "Unauthorized")
			return null;
		// fetch latest timetable
		String regex = "(?i)<td>1.<\\/td><td>[\\w\\d]+<\\/td><td><a[\\n\\s]+href=\"([\\w.?=&\\d]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			url = matcher.group(1);
			Log.i("Timetable latest url", matcher.group(1));
		}
		// fetch html from second url
		html = FetchUrL("http://info.uniten.edu.my/info/" + url);
		
		regex = "(?i)<TABLE CELLSPACING=\"0\"><THEAD>([\\s\\S]+)</table>";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(html);
		if(matcher.find()){
			html = "<HTML><HEAD><LINK REL=\"stylesheet\" MEDIA=\"screen\" TYPE=\"text/css\" HREF=\"styles.css\">"
					+ "<LINK REL=\"stylesheet\" MEDIA=\"print\" TYPE=\"text/css\" HREF=\"print.css\"></HEAD><BODY>"
					+ "<TABLE CELLSPACING=\"0\"><THEAD>"+ matcher.group(1)+"</TD></TABLE><br><br><img src=\"../logo.png\" width=\"50px\""
					+ " height=\"50px\"><H2>Uniten Student Info Mobile App</H2><br><br></BODY></HTML>";
		}
		
		html = html.replaceFirst("0800", "8.00am");
		html = html.replaceFirst("0900", "9.00am");
		html = html.replaceFirst("1000", "10.00am");
		html = html.replaceFirst("1100", "11.00am");
		html = html.replaceFirst("1200", "12.00pm");
		html = html.replaceFirst("1300", "1.00pm");
		html = html.replaceFirst("1400", "2.00pm");
		html = html.replaceFirst("1500", "3.00pm");
		html = html.replaceFirst("1600", "4.00pm");
		html = html.replaceFirst("1700", "5.00pm");
		html = html.replaceFirst("1800", "6.00pm");
		html = html.replaceFirst("1900", "7.00pm");
		html = html.replaceFirst("2000", "8.00pm");
		html = html.replaceFirst("2100", "9.00pm");
		
		
		Log.i("timetable latest html", html);
		return html;
	}

	public List<TimeTableStruct> Timetable() {
		String url = "http://info.uniten.edu.my/info/Ticketing.ASP?WCI=TimeTable";


		// fetch html from url
		String html = FetchUrL(url);
		if (html == null || html == "No_Internet")
			return null;

		// fetch latest timetable
		String regex = "(?i)<td>1.<\\/td><td>[\\w\\d]+<\\/td><td><a[\\n\\s]+href=\"([\\w.?=&\\d]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			url = matcher.group(1);
			Log.i("Timetable latest url", matcher.group(1));
		}

		// fetch html from second url
		html = FetchUrL("http://info.uniten.edu.my/info/" + url);
		//debug
		//html = FetchUrL("http://sandbox.mahadirlab.com/uniteninfo/Ticketing.htm");
		if (html == null || html == "No_Internet")
			return null;
		
		//Log.i("Debug html",html);

		// fetch table
		regex = "(?i)<table.*?>(.*?)<\\/table>";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(html);

		while (matcher.find()) {
			// System.out.println(matcher.group(1));
			html = matcher.group(1); // get the last one
		}
		// fetch tr of the timetable
		regex = "(?i)<\\/THEAD>(.*)";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(html);

		if (matcher.find()) {
			// System.out.println(matcher.group(1));
			html = matcher.group(1);
		}

		// fetch td
		regex = "(?i)<td.*?(?:COLSPAN=\"(\\d+?)\")?>(.*?)<\\/td>";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(html);

		int time = 800;
		String currentday = null;
		List<TimeTableStruct> timeTableStrc2 = new ArrayList<TimeTableStruct>();
		TimeTableStruct d2 = null;

		while (matcher.find()) {
			//loop through all td
			int colspan = 1;
			try{
				colspan = Integer.parseInt(matcher.group(1));
				if(colspan<=0){
					//prevent the multiplexer becomes 0 or negative
					colspan = 1;
				}
			}
			catch(NumberFormatException e){
				//
				colspan = 1;
			}

			String str = matcher.group(2);
			if (str.matches("(?i)monday|tuesday|wednesday|thursday|friday|saturday|sunday")) {
				currentday = str;
				time = 800;

			} else if (str.matches("&nbsp;")) {
				//increment the time
				time = TimeTableStruct.incrementor(time);
			} else if (str.matches("(?i)[\\d\\w]+<br>[\\d\\w-\\s/]+")) {
				//fetch the subject code + location
				d2 = new TimeTableStruct();
				d2.insertDay(currentday);
				d2.insertStartTime(d2.timeConverterampm(time));
				time = TimeTableStruct.incrementor(time,colspan);
				d2.insertEndTime(d2.timeConverterampm(time));

				String[] splitStrings = str.split("<BR>");
				d2.insertsubject(splitStrings[0]);
				d2.insertLocation(splitStrings[1]);
				timeTableStrc2.add(d2);
			}

		}

		return timeTableStrc2;
	}
	
	
	/**
	 * 
	 * @param html
	 * @return arraylist hashmap of
	 * semester,code, descriptions, sections, credit, grade, points
	 * 
	 */
	public ArrayList<HashMap<String, String>> getResultSubjectList(String html){
		ArrayList<HashMap<String, String>> subjectHashMap = new ArrayList<HashMap<String,String>>();
		//fetch table row
		String regex = "(?i)<TR CLASS=\"LINE(1|2)\">([\\s\\S]+?)</tr>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		
		regex = "(?i)Exam Results for (.+?)</font>";		
		pattern = Pattern.compile(regex);
		Matcher matcherSemName = pattern.matcher(html);
		String semNameString="";
		if(matcherSemName.find()){
			semNameString = matcherSemName.group(1);
		}
		
		while (matcher.find()) {
			//System.out.println(matcher.group(2));
			//System.out.println("\n========\n");
			html = matcher.group(2); // get the last one

			HashMap<String, String> msgHashMap = new HashMap<String,String>();
						
			//fetch title and body
			regex = "(?i)<td.*?>([\\s\\S]+?)</td>";
			pattern = Pattern.compile(regex);
			Matcher matcher2 = pattern.matcher(matcher.group(2));
			int position=0;
			while(matcher2.find()){
				//System.out.println(matcher2.group(1));
				if(position == 1){
					msgHashMap.put(DatabaseHandler.RESULT_SUBJECT_SEMESTER_NAME, semNameString);
					msgHashMap.put(DatabaseHandler.RESULT_SUBJECT_CODE, matcher2.group(1));
				}
				else if(position == 2){
					msgHashMap.put(DatabaseHandler.RESULT_SUBJECT_DESCRIPTIONS, matcher2.group(1));
				}
				else if(position == 3){
					msgHashMap.put(DatabaseHandler.RESULT_SUBJECT_SECTION, matcher2.group(1));
				}
				else if(position == 4){
					msgHashMap.put(DatabaseHandler.RESULT_SUBJECT_CREDITS, matcher2.group(1));
				}
				else if(position ==5){
					regex = "(?i)(.+?)<TD ALIGN=\"RIGHT\">(.+)";
					pattern = Pattern.compile(regex);
					Matcher matcher3 = pattern.matcher(matcher2.group(1));
					if(matcher3.find()){
						msgHashMap.put(DatabaseHandler.RESULT_SUBJECT_GRADE, matcher3.group(1));
						msgHashMap.put(DatabaseHandler.RESULT_SUBJECT_POINTS, matcher3.group(2));
					}
					
				}
				position++;
			}
			subjectHashMap.add(msgHashMap);
		}
						
		return subjectHashMap;
	}

	public void SaveToSdCard(String fileName, String str) {
		File file = new File(Environment.getExternalStorageDirectory(),
				fileName);
		FileOutputStream fos;

		// use FileWriter to write file
		/*
		 * FileWriter fw = new FileWriter(file.getAbsoluteFile());
		 * BufferedWriter bw = new BufferedWriter(fw);
		 */
		byte[] data = str.getBytes();
		try {

			if (!file.exists()) {
				file.createNewFile();
			}

			fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// handle exception
			Log.e("SaveToSdCard", "File Not found exception");
		} catch (IOException e) {
			// handle exception
			Log.e("SaveToSdCard", "Exception" + e.toString());
		}
	}

	public String FetchUrL(String url) {
		StringBuffer result = new StringBuffer();
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			// register ntlm auth scheme
			httpclient.getAuthSchemes().register("ntlm",
					new NTLMSchemeFactory());
			httpclient.getCredentialsProvider().setCredentials(
					new AuthScope("info.uniten.edu.my", AuthScope.ANY_PORT),
					new NTCredentials(unitenid, unitenpassw, "", ""));

			HttpGet request = new HttpGet(url);
			HttpResponse httpResponse = httpclient.execute(request);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent()));

			String line = "";
			while ((line = br.readLine()) != null) {
				result.append(line);
			}

			br.close();

		} catch (UnknownHostException e) { // no internet connection catch
			e.printStackTrace();
			Log.e("FetchUrL", "No internet ");
			return "No_Internet";
		} catch (Exception e) {
			Log.e("FetchUrL", "Error in http connection:" + e.toString());
			return null;
		}
		
		String resultString = result.toString();
		String regex = "(?i)<h1>Unauthorized Access</h1>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(resultString);
		if(matcher.matches())
			return "Unauthorized";
		

		Log.i("FetchUrL content: ", result.toString());
		return resultString;

	}
}
