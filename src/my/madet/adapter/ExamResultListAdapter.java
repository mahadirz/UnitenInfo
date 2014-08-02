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
 * LisAdapter view Exam Result 
 *
 * @author Mahadir Ahmad
 * @version 1.0
 * 
 */

package my.madet.adapter;

import java.util.HashMap;

import my.madet.uniteninfo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExamResultListAdapter extends ArrayAdapter<String>{
	
	private final Context context;
	private HashMap<String, String[]> values;
	private String[] val;

	/**
	 * 
	 * @param context
	 * @param values
	 * the values of String[] in format
	 * 0 - gpa
	 * 1 - cgpa
	 * 2 - image name
	 */
	public ExamResultListAdapter(Context context, HashMap<String, String[]> values, String[] val) {
		super(context,R.layout.list_result,val);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.values = values;
		this.val = val;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
		View rowView = inflater.inflate(R.layout.list_result, parent, false);
		
		ImageView medal_img = (ImageView) rowView.findViewById(R.id.list_image);
		TextView semester = (TextView) rowView.findViewById(R.id.tv_semester);
		TextView gpa = (TextView) rowView.findViewById(R.id.tv_gpa);
		TextView cgpa = (TextView) rowView.findViewById(R.id.tv_cgpa);
		

			String aValue[] = values.get(val[position]);
			semester.setText(val[position]);
			gpa.setText(aValue[0]);
			cgpa.setText(aValue[1]);
			
			if(aValue[2].compareToIgnoreCase("medal_award_gold")==0)
				medal_img.setImageResource(R.drawable.medal_award_gold);
			else if(aValue[2].compareToIgnoreCase("medal_award_silver")==0)
				medal_img.setImageResource(R.drawable.medal_award_silver);
			else
				medal_img.setImageResource(R.drawable.medal_award_bronze);

		
		return rowView;
	}
	

}
