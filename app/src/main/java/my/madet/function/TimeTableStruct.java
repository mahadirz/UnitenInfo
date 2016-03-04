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
 * Structure type to store time table
 *
 * @author Mahadir Ahmad
 * @version 1.0
 * 
 */

package my.madet.function;

public class TimeTableStruct {
	
	public static final int INCREMENTS = 30;
	private String day= null;
	private String starttime = null;
	private String endtime= null;
	private String subject = null;
	private String location = null;
	
	public void insertDay(String s){
		day = s;
	}
	public void insertStartTime(String s){
		starttime = s;
	}
	
	public void insertEndTime(String s){
		endtime = s;
	}
	
	public void insertLocation(String s){
		location = s;
	}
	
	public void insertsubject(String s){
		subject = s;
	}
	
	@Override
	public String toString(){
		return day+" : "+starttime+" - "+endtime+"\n"
				+subject+" @ "+location;
	}
	
	public String getDay(){
		return day ;
	}
	public String getStartTime(){
		return starttime ;
	}
	
	public String getEndTime(){
		return endtime;
	}
	
	public String getLocation(){
		return location;
	}
	
	public String getSubject(){
		return subject;
	}
	
	public String timeConverterampm(int time)
	{
		String tmp = Integer.toString(time);
		String min, hr;
		if (tmp.length() >= 4) {
			//ex: 1200
			min = tmp.substring(2);
			hr = tmp.substring(0, 2);
		} else {
			//only 3 ex: 800
			min = tmp.substring(1);
			hr = tmp.substring(0, 1);
		}
		int hour = Integer.parseInt(hr);

		String ytime = null;
		if (hour > 11) {
			//pm
			int xtime = hour % 12;
			if (xtime == 0) {
				xtime = 12;
			}
			ytime = xtime + "."+min+"pm";
		} else {
			ytime = hour + "."+min+"am";
		}
		return ytime;
	}

	/**
	 * Increment the time
	 * @return
	 */
	public static int incrementor(int time) {
		return incrementor(time,1);
	}

	public static int incrementor(int time, int multiplexer)
	{
		String tmp = Integer.toString(time);
		String min, hr;
		if (tmp.length() >= 4) {
			//ex: 1200
			min = tmp.substring(2);
			hr = tmp.substring(0, 2);
		} else {
			//only 3 ex: 800
			min = tmp.substring(1);
			hr = tmp.substring(0, 1);
		}

		int minute = Integer.parseInt(min);
		int hour = Integer.parseInt(hr);
		minute += (INCREMENTS*multiplexer);
		//handle greater than 60 minutes
		int remainder = minute % 60;
		int balance = (int) minute / 60;
		hour += balance;
		hr = Integer.toString(hour);
		min = Integer.toString(remainder);
		if (remainder < 10) {
			//add 0
			min = min + "0";
		}
		tmp = hr + min;
		return Integer.parseInt(tmp);
	}
	
}
