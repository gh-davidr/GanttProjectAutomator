/*
Copyright 2021 David Richardson, a regular GanttProject User

This file is part of GanttProjectAutomator, a utility conceived from
years of project management experience to make task status communication
just a little bit easier.

It works specifically with files generated by GanttProject, an 
open source project management tool.

GanttProjectAutomator is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

GanttProjectAutomator is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

See <http://www.gnu.org/licenses/>.
*/
package org.richardson.david.config;

import java.util.ArrayList;
import java.util.Date;

import org.richardson.david.utils.AppUtils;

import lombok.Data;

@Data
public class UserConfigNotify {

	private Long    minsBack;
	private Long    retries;
	private Integer precision;
	private String  notifications;
	private Boolean includeParentTasks;
	private Boolean assigneesAndPMOnly;
	private String  alertEmail;

	public Boolean doNotification(Date start, Date end)
	{
		return doNotification(start, end, new Date());
	}

	public Boolean doNotification(Date start, Date end, Date now)
	{
		
		Double fractionForNotificationDouble = getFractionForNotification(start, end, now);
		
		Boolean resultBoolean = fractionForNotificationDouble == null ? false : true;
		
		return resultBoolean;
	}

	public Double getFractionForNotification(Date start, Date end, Date now)
	{
		Double result = null;
		long startEndDaysDiff = AppUtils.timeDiffInDays(start, end);
		long startNowDaysDiff = AppUtils.timeDiffInDays(start, now);

		Double fracIncrementDouble = AppUtils.round(AppUtils.fractionToDouble("1/" + startEndDaysDiff), getPrecision());
		Double currentFractionDouble = AppUtils.round(AppUtils.fractionToDouble("" + startNowDaysDiff + "/" + startEndDaysDiff), getPrecision());
		ArrayList<Double> notificationFractionList = getNotificationFractions();

		for (Double d : notificationFractionList)
		{
			// Special case ... always will notify every active day after start as long as progress is zero
			if (startNowDaysDiff >= 0 && d < 0.0)
			{
				result = d;
			}
			else
			{
				Double diffDouble = AppUtils.round(currentFractionDouble - d, getPrecision());
				int retVal = fracIncrementDouble.compareTo(diffDouble);
				if (currentFractionDouble >= d && retVal > 0)
				{
					result = d;
				}
			}
		}

		return result;
	}

	
	public ArrayList<Double> getNotificationFractions()
	{
		ArrayList<Double> resultArrayList = new  ArrayList<Double>();

		String[] fractionStrings = getNotifications().split(" ");

		for (String s : fractionStrings)
		{
			Double fracDouble = geNotificationFraction(s.toUpperCase());
			if (fracDouble != null) resultArrayList.add(fracDouble);
		}

		return resultArrayList;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sBuilder = new StringBuilder();
				
		sBuilder.append(""
				
				+ "<ul>"
				+ "<li>" + "<b>Mins Back           :</b> " + getMinsBack() + "</li>\r\n" 
				+ "<li>" + "<b>Retries             :</b> " + getRetries() + "</li>\r\n" 
				+ "<li>" + "<b>Precision           :</b> " + getPrecision() + "</li>\r\n" 
				+ "<li>" + "<b>Notifications       :</b> " + getNotifications() + "</li>\r\n" 
				+ "<li>" + "<b>Inc Parent Tasks    :</b> " + getIncludeParentTasks() + "</li>\r\n" 
				+ "<li>" + "<b>Assignees & PM Only :</b> " + getAssigneesAndPMOnly() + "</li>\r\n" 
				+ "<li>" + "<b>Alert Email         :</b> " + getAlertEmail() + "</li>\r\n" 
				+ "</ul>"
				);
		
		return sBuilder.toString();
	}

	private Double geNotificationFraction(String fractionString)
	{
		Double resultDouble = null;

		if (fractionString != null)
		{
			// Special case ... this allows sending of ALL tasks with start date in the past.
			// Use with care :-)
			if (fractionString.contains("ALWAYS"))
			{
				resultDouble = Double.valueOf(-10);
			}

			else if (fractionString.contains("/"))
			{
				resultDouble = AppUtils.fractionToDouble(fractionString);
			}
			else if (fractionString.equals("QUARTER"))
			{
				resultDouble = Double.valueOf(0.25);
			}
			else if (fractionString.equals("THIRD"))
			{
				resultDouble = Double.valueOf(1 / 3);
			}
			else if (fractionString.equals("HALF"))
			{
				resultDouble = Double.valueOf(0.5);
			}
			else if (fractionString.equals("START"))
			{
				resultDouble = Double.valueOf(0);
			}
			else if (fractionString.equals("END"))
			{
				resultDouble = Double.valueOf(1);
			}
			else if (fractionString.contains("EXPIR"))
			{
				resultDouble = Double.valueOf(1);
			}
			else if (fractionString.contains("FINI"))
			{
				resultDouble = Double.valueOf(1);
			}


		}

		return resultDouble;
	}
}