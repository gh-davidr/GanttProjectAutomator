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
package org.richardson.david.entity.email;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.richardson.david.config.UserConfig;
import org.richardson.david.entity.gantt.Resource;
import org.richardson.david.model.EnrichedTask;
import org.richardson.david.model.Repository;
import org.richardson.david.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

@Getter
public class EmailMessageBase {

	private static Logger LOGGER = LoggerFactory.getLogger(EmailMessageBase.class);
	@Getter private static Pattern TASK_ID_PATTERN   = Pattern.compile("\\{Task Id:(\\d+)\\}");
	@Getter private static Pattern UPCASE_TASK_ID_PATTERN   = Pattern.compile("\\{TASK ID:(\\d+)\\}");
	@Getter private static Pattern UPCASE_PROJECT_PATTERN   = Pattern.compile("\\{Project .*\\}".toUpperCase());
	@Getter private static Pattern UPCASE_RESERVED_PATTERN   = Pattern.compile("\\{Reserved Token for Project .*\\}".toUpperCase());
	
	@Getter(lazy=true) private static final String allowedRecipientString = AppUtils.getLowerString(UserConfig.getInstance().getCommon().getAllowedRecipients());
	@Getter(lazy=true) private static final String blockedRecipientString = AppUtils.getLowerString(UserConfig.getInstance().getCommon().getBlockedRecipients());
	
	protected Boolean allowRecipient(String recipientString) 
	{
		Boolean resultBoolean = false;

		if (getAllowedRecipientString() == null && getBlockedRecipientString() == null)
		{
			resultBoolean = true;
		}
		else if (getAllowedRecipientString() != null && getAllowedRecipientString().length() > 0)
		{
			resultBoolean = getAllowedRecipientString().contains(recipientString.toLowerCase());
		}
		else if (getBlockedRecipientString() != null && getBlockedRecipientString().length() > 0)
		{
			resultBoolean = !getBlockedRecipientString().contains(recipientString.toLowerCase());
		}
		
		return resultBoolean;
	}

	protected String getTaskTableHTML(EnrichedTask et)
	{
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append(""
				+ "<table class=MsoTable15Grid4Accent1 border=1 cellspacing=0 cellpadding=0\r\n"
				+ " style='margin-left:30.2pt;border-collapse:collapse;border:none;mso-border-alt:\r\n"
				+ " solid #95B3D7 .5pt;mso-border-themecolor:accent1;mso-border-themetint:153;\r\n"
				+ " mso-yfti-tbllook:1184;mso-padding-alt:0cm 5.4pt 0cm 5.4pt'>\r\n"

				+ getTaskTableHeaderRowHTML()
				+ getTaskRowHTML(et)

				+ "</table>\r\n"
				);

		return sBuilder.toString();
	}


	protected String getTaskTableStartHTML()
	{
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append(""
				+ "<table class=MsoTable15Grid4Accent1 border=1 cellspacing=0 cellpadding=0\r\n"
				+ " style='margin-left:30.2pt;border-collapse:collapse;border:none;mso-border-alt:\r\n"
				+ " solid #95B3D7 .5pt;mso-border-themecolor:accent1;mso-border-themetint:153;\r\n"
				+ " mso-yfti-tbllook:1184;mso-padding-alt:0cm 5.4pt 0cm 5.4pt'>\r\n"
				);

		return sBuilder.toString();
	}

	protected String getTaskTableEndHTML()
	{
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append(""
				+ "</table>\r\n"
				);

		return sBuilder.toString();
	}


	protected String getTaskTableHeaderRowHTML()
	{
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append(""
				+ " <tr style='mso-yfti-irow:-1;mso-yfti-firstrow:yes;mso-yfti-lastfirstrow:yes'>\r\n"

				//				+ "  <td width=125 style='width:93.5pt;border:solid #4F81BD 1.0pt;mso-border-themecolor:\r\n"
			    + "  <td width=125 style='width:40pt;border:solid #4F81BD 1.0pt;mso-border-themecolor:\r\n"
				+ "  accent1;border-right:none;mso-border-top-alt:solid #4F81BD .5pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;mso-border-left-alt:solid #4F81BD .5pt;mso-border-left-themecolor:\r\n"
				+ "  accent1;mso-border-bottom-alt:solid #4F81BD .5pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;background:#4F81BD;mso-background-themecolor:accent1;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:5'><b><span style='color:white;\r\n"
				+ "  mso-themecolor:background1'>Id<o:p></o:p></span></b></p>\r\n"
				+ "  </td>\r\n"

				//				+ "  <td width=125 style='width:93.5pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				// + "  <td width=125 style='width:130pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  <td width=125 style='width:7.0cm;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;border-left:none;border-bottom:solid #4F81BD 1.0pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;border-right:none;mso-border-top-alt:solid #4F81BD .5pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;mso-border-bottom-alt:solid #4F81BD .5pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;background:#4F81BD;mso-background-themecolor:accent1;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal style='mso-yfti-cnfc:1'><b><span style='color:white;\r\n"
				+ "  mso-themecolor:background1'>Task Name<o:p></o:p></span></b></p>\r\n"
				+ "  </td>\r\n"

				+ "  <td width=125 style='width:60pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;border-left:none;border-bottom:solid #4F81BD 1.0pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;border-right:none;mso-border-top-alt:solid #4F81BD .5pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;mso-border-bottom-alt:solid #4F81BD .5pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;background:#4F81BD;mso-background-themecolor:accent1;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:1'><b><span style='color:white;\r\n"
				+ "  mso-themecolor:background1'>Sub-Tasks<o:p></o:p></span></b></p>\r\n"
				+ "  </td>\r\n"

				//				+ "  <td width=125 style='width:93.5pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  <td width=125 style='width:75pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;border-left:none;border-bottom:solid #4F81BD 1.0pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;border-right:none;mso-border-top-alt:solid #4F81BD .5pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;mso-border-bottom-alt:solid #4F81BD .5pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;background:#4F81BD;mso-background-themecolor:accent1;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:1'><b><span style='color:white;\r\n"
				+ "  mso-themecolor:background1'>Progress<o:p></o:p></span></b></p>\r\n"
				+ "  </td>\r\n"

				// + "  <td width=125 style='width:93.5pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  <td width=125 style='width:80.0pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;border-left:none;border-bottom:solid #4F81BD 1.0pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;border-right:none;mso-border-top-alt:solid #4F81BD .5pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;mso-border-bottom-alt:solid #4F81BD .5pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;background:#4F81BD;mso-background-themecolor:accent1;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:1'><b><span style='color:white;\r\n"
				+ "  mso-themecolor:background1'>Start Date<o:p></o:p></span></b></p>\r\n"
				+ "  </td>\r\n"

				// + "  <td width=125 style='width:93.5pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  <td width=125 style='width:80.0pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;border-left:none;border-bottom:solid #4F81BD 1.0pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;border-right:none;mso-border-top-alt:solid #4F81BD .5pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;mso-border-bottom-alt:solid #4F81BD .5pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;background:#4F81BD;mso-background-themecolor:accent1;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:1'><b><span style='color:white;\r\n"
				+ "  mso-themecolor:background1'>End Date<o:p></o:p></span></b></p>\r\n"
				+ "  </td>\r\n"

				+ "  <td width=134 style='width:100.5pt;border-top:solid #4F81BD 1.0pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;border-left:none;border-bottom:solid #4F81BD 1.0pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;border-right:none;mso-border-top-alt:solid #4F81BD .5pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;mso-border-bottom-alt:solid #4F81BD .5pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;background:#4F81BD;mso-background-themecolor:accent1;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal style='mso-yfti-cnfc:1'><b><span style='color:white;\r\n"
				+ "  mso-themecolor:background1'>Resources<o:p></o:p></span></b></p>\r\n"
				+ "  </td>\r\n"

				+ "  <td width=227 style='width:6.0cm;border:solid #4F81BD 1.0pt;mso-border-themecolor:\r\n"
				+ "  accent1;border-left:none;mso-border-top-alt:solid #4F81BD .5pt;mso-border-top-themecolor:\r\n"
				+ "  accent1;mso-border-bottom-alt:solid #4F81BD .5pt;mso-border-bottom-themecolor:\r\n"
				+ "  accent1;mso-border-right-alt:solid #4F81BD .5pt;mso-border-right-themecolor:\r\n"
				+ "  accent1;background:#4F81BD;mso-background-themecolor:accent1;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal style='mso-yfti-cnfc:1'><b><span style='color:white;\r\n"
				+ "  mso-themecolor:background1'>Notes<o:p></o:p></span></b></p>\r\n"
				+ "  </td>\r\n"
				+ " </tr>\r\n"

				);

		return sBuilder.toString();
	}


	protected String getTaskRowHTML(EnrichedTask et)
	{
		StringBuilder sBuilder = new StringBuilder();

		Date now = AppUtils.today();

		StringBuilder resStringBuilder = new StringBuilder();
		resStringBuilder.append("  <p class=MsoNormal style='mso-yfti-cnfc:64'><span style='color:black;\r\n"
				+ "  mso-color-alt:windowtext'>");
		int i = 0;
		for (Resource resource : et.getResources())
		{
			resStringBuilder.append((i++>0?"<br>":"") + resource.getName());
		}
		resStringBuilder.append("</span><o:p></o:p></p>\r\n");

		String complString =  et.getTask().getComplete();

		String taskIdString    = et.getTask().getId();
		String taskNameString  = et.getTask().getName();
		String subTasks = et.getNumDescendants() == 0L ? "-" : et.getNumDescendants().toString();
		String completeString  = complString.equals("0") ? "Not Started" : complString + "% complete";
		String startDateString = now.compareTo(et.getStartDate()) == 0 
				? "Today <br>" + AppUtils.getDateStr(et.getStartDate()) 
						: AppUtils.getDateStr(et.getStartDate()) + "";
		String endDateString   = AppUtils.getDateStr(et.getEndDate());
		String resourceString  = resStringBuilder.toString();
		String notesString     = et.getTask().getNotes();
		notesString = notesString == null ? "" : notesString;

		sBuilder.append(""				
				+ " <tr style='mso-yfti-irow:0;mso-yfti-lastrow:yes'>\r\n"
				//				+ "  <td width=125 style='width:93.5pt;border:solid #95B3D7 1.0pt;mso-border-themecolor:\r\n"
				+ "  <td width=125 style='width:40pt;border:solid #95B3D7 1.0pt;mso-border-themecolor:\r\n"
				+ "  accent1;mso-border-themetint:153;border-top:none;mso-border-top-alt:solid #95B3D7 .5pt;\r\n"
				+ "  mso-border-top-themecolor:accent1;mso-border-top-themetint:153;mso-border-alt:\r\n"
				+ "  solid #95B3D7 .5pt;mso-border-themecolor:accent1;mso-border-themetint:153;\r\n"
				+ "  background:#DBE5F1;mso-background-themecolor:accent1;mso-background-themetint:\r\n"
				+ "  51;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:68'><b><span style='color:black;\r\n"
				+ "  mso-color-alt:windowtext'>" + taskIdString + "</span><o:p></o:p></b></p>\r\n"
				+ "  </td>\r\n"
				//				+ "  <td width=125 style='width:93.5pt;border-top:none;border-left:none;\r\n"
				// + "  <td width=125 style='width:130pt;border-top:none;border-left:none;\r\n"
				+ "  <td width=125 style='width:7.0cm;border-top:none;border-left:none;\r\n"
				+ "  border-bottom:solid #95B3D7 1.0pt;mso-border-bottom-themecolor:accent1;\r\n"
				+ "  mso-border-bottom-themetint:153;border-right:solid #95B3D7 1.0pt;mso-border-right-themecolor:\r\n"
				+ "  accent1;mso-border-right-themetint:153;mso-border-top-alt:solid #95B3D7 .5pt;\r\n"
				+ "  mso-border-top-themecolor:accent1;mso-border-top-themetint:153;mso-border-left-alt:\r\n"
				+ "  solid #95B3D7 .5pt;mso-border-left-themecolor:accent1;mso-border-left-themetint:\r\n"
				+ "  153;mso-border-alt:solid #95B3D7 .5pt;mso-border-themecolor:accent1;\r\n"
				+ "  mso-border-themetint:153;background:#DBE5F1;mso-background-themecolor:accent1;\r\n"
				+ "  mso-background-themetint:51;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal style='mso-yfti-cnfc:64'><span style='color:black;\r\n"
				+ "  mso-color-alt:windowtext'>" + taskNameString + "</span><o:p></o:p></p>\r\n"
				+ "  </td>\r\n"
				//				+ "  <td width=125 style='width:93.5pt;border-top:none;border-left:none;\r\n"
				+ "  <td width=125 style='width:60pt;border-top:none;border-left:none;\r\n"
				+ "  border-bottom:solid #95B3D7 1.0pt;mso-border-bottom-themecolor:accent1;\r\n"
				+ "  mso-border-bottom-themetint:153;border-right:solid #95B3D7 1.0pt;mso-border-right-themecolor:\r\n"
				+ "  accent1;mso-border-right-themetint:153;mso-border-top-alt:solid #95B3D7 .5pt;\r\n"
				+ "  mso-border-top-themecolor:accent1;mso-border-top-themetint:153;mso-border-left-alt:\r\n"
				+ "  solid #95B3D7 .5pt;mso-border-left-themecolor:accent1;mso-border-left-themetint:\r\n"
				+ "  153;mso-border-alt:solid #95B3D7 .5pt;mso-border-themecolor:accent1;\r\n"
				+ "  mso-border-themetint:153;background:#DBE5F1;mso-background-themecolor:accent1;\r\n"
				+ "  mso-background-themetint:51;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:64'><span style='color:black;\r\n"
				+ "  mso-color-alt:windowtext'>" + subTasks + "</span><o:p></o:p></p>\r\n"
				+ "  </td>\r\n"
				//				+ "  <td width=125 style='width:93.5pt;border-top:none;border-left:none;\r\n"
				+ "  <td width=125 style='width:75pt;border-top:none;border-left:none;\r\n"
				+ "  border-bottom:solid #95B3D7 1.0pt;mso-border-bottom-themecolor:accent1;\r\n"
				+ "  mso-border-bottom-themetint:153;border-right:solid #95B3D7 1.0pt;mso-border-right-themecolor:\r\n"
				+ "  accent1;mso-border-right-themetint:153;mso-border-top-alt:solid #95B3D7 .5pt;\r\n"
				+ "  mso-border-top-themecolor:accent1;mso-border-top-themetint:153;mso-border-left-alt:\r\n"
				+ "  solid #95B3D7 .5pt;mso-border-left-themecolor:accent1;mso-border-left-themetint:\r\n"
				+ "  153;mso-border-alt:solid #95B3D7 .5pt;mso-border-themecolor:accent1;\r\n"
				+ "  mso-border-themetint:153;background:#DBE5F1;mso-background-themecolor:accent1;\r\n"
				+ "  mso-background-themetint:51;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:64'><span style='color:black;\r\n"
				+ "  mso-color-alt:windowtext'>" + completeString + "</span><o:p></o:p></p>\r\n"
				+ "  </td>\r\n"
				// + "  <td width=125 style='width:93.5pt;border-top:none;border-left:none;\r\n"
				+ "  <td width=125 style='width:80.0pt;border-top:none;border-left:none;\r\n"
				+ "  border-bottom:solid #95B3D7 1.0pt;mso-border-bottom-themecolor:accent1;\r\n"
				+ "  mso-border-bottom-themetint:153;border-right:solid #95B3D7 1.0pt;mso-border-right-themecolor:\r\n"
				+ "  accent1;mso-border-right-themetint:153;mso-border-top-alt:solid #95B3D7 .5pt;\r\n"
				+ "  mso-border-top-themecolor:accent1;mso-border-top-themetint:153;mso-border-left-alt:\r\n"
				+ "  solid #95B3D7 .5pt;mso-border-left-themecolor:accent1;mso-border-left-themetint:\r\n"
				+ "  153;mso-border-alt:solid #95B3D7 .5pt;mso-border-themecolor:accent1;\r\n"
				+ "  mso-border-themetint:153;background:#DBE5F1;mso-background-themecolor:accent1;\r\n"
				+ "  mso-background-themetint:51;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:64'><span style='color:black;\r\n"
				+ "  mso-color-alt:windowtext'>" + startDateString + "</span><o:p></o:p></p>\r\n"
				+ "  </td>\r\n"
				// + "  <td width=125 style='width:93.5pt;border-top:none;border-left:none;\r\n"
				+ "  <td width=125 style='width:80.0pt;border-top:none;border-left:none;\r\n"
				+ "  border-bottom:solid #95B3D7 1.0pt;mso-border-bottom-themecolor:accent1;\r\n"
				+ "  mso-border-bottom-themetint:153;border-right:solid #95B3D7 1.0pt;mso-border-right-themecolor:\r\n"
				+ "  accent1;mso-border-right-themetint:153;mso-border-top-alt:solid #95B3D7 .5pt;\r\n"
				+ "  mso-border-top-themecolor:accent1;mso-border-top-themetint:153;mso-border-left-alt:\r\n"
				+ "  solid #95B3D7 .5pt;mso-border-left-themecolor:accent1;mso-border-left-themetint:\r\n"
				+ "  153;mso-border-alt:solid #95B3D7 .5pt;mso-border-themecolor:accent1;\r\n"
				+ "  mso-border-themetint:153;background:#DBE5F1;mso-background-themecolor:accent1;\r\n"
				+ "  mso-background-themetint:51;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal align=center style='mso-yfti-cnfc:64'><span style='color:black;\r\n"
				+ "  mso-color-alt:windowtext'>" + endDateString + "</span><o:p></o:p></p>\r\n"
				+ "  </td>\r\n"
				+ "  <td width=134 style='width:100.5pt;border-top:none;border-left:none;\r\n"
				+ "  border-bottom:solid #95B3D7 1.0pt;mso-border-bottom-themecolor:accent1;\r\n"
				+ "  mso-border-bottom-themetint:153;border-right:solid #95B3D7 1.0pt;mso-border-right-themecolor:\r\n"
				+ "  accent1;mso-border-right-themetint:153;mso-border-top-alt:solid #95B3D7 .5pt;\r\n"
				+ "  mso-border-top-themecolor:accent1;mso-border-top-themetint:153;mso-border-left-alt:\r\n"
				+ "  solid #95B3D7 .5pt;mso-border-left-themecolor:accent1;mso-border-left-themetint:\r\n"
				+ "  153;mso-border-alt:solid #95B3D7 .5pt;mso-border-themecolor:accent1;\r\n"
				+ "  mso-border-themetint:153;background:#DBE5F1;mso-background-themecolor:accent1;\r\n"
				+ "  mso-background-themetint:51;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ resourceString

				+ "  <td width=227 style='width:6.0cm;border-top:none;border-left:none;border-bottom:\r\n"
				+ "  solid #95B3D7 1.0pt;mso-border-bottom-themecolor:accent1;mso-border-bottom-themetint:\r\n"
				+ "  153;border-right:solid #95B3D7 1.0pt;mso-border-right-themecolor:accent1;\r\n"
				+ "  mso-border-right-themetint:153;mso-border-top-alt:solid #95B3D7 .5pt;\r\n"
				+ "  mso-border-top-themecolor:accent1;mso-border-top-themetint:153;mso-border-left-alt:\r\n"
				+ "  solid #95B3D7 .5pt;mso-border-left-themecolor:accent1;mso-border-left-themetint:\r\n"
				+ "  153;mso-border-alt:solid #95B3D7 .5pt;mso-border-themecolor:accent1;\r\n"
				+ "  mso-border-themetint:153;background:#DBE5F1;mso-background-themecolor:accent1;\r\n"
				+ "  mso-background-themetint:51;padding:0cm 5.4pt 0cm 5.4pt'>\r\n"
				+ "  <p class=MsoNormal style='mso-yfti-cnfc:64'><span style='color:black;\r\n"
				+ "  mso-color-alt:windowtext'>" + notesString + "</span><o:p></o:p></p>\r\n"
				+ "  </td>\r\n"
				+ " </tr>\r\n"

				);

		return sBuilder.toString();
	}

	protected String getEmailSignature()
	{
		return getEmailSignature(null);
	}

	protected String getEmailSignature(EnrichedTask et)
	{
		StringBuilder sBuilder = new StringBuilder();
		
		String notifierString = UserConfig.getInstance().getCommon().getNotifier();
		String emailSignatureString = UserConfig.getInstance().getCommon().getEmailSignature();


		String tagProjectNameString = Repository.getInstance().getProject().getName();
		String tagTaskIdString = et != null && et.getTask() != null ? buildTaskIdSignature(et.getTask().getId()) : "";

		sBuilder.append(""
				+ "\r\n"
				+ "<p class=MsoNormal><o:p>&nbsp;</o:p></p>\r\n"
				+ "\r\n"
				+ "<p class=MsoNormal>Kind regards<br>" + notifierString + "<o:p></o:p></p>\r\n"
				+ "\r\n"
				+ "<p class=MsoNormal><o:p>&nbsp;</o:p></p>\r\n"
				+ "\r\n"
				+ "<p class=MsoPlainText><i><span style='font-size:8.0pt'>{Project " + tagProjectNameString + "}" + "<br>" + tagTaskIdString + "<br>" + emailSignatureString + "<o:p></o:p></span></i></p>\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "<p class=MsoNormal><o:p>&nbsp;</o:p></p>\r\n"
				+ "\r\n"
				+ "</div>\r\n"
				+ "\r\n"
				+ "</body>\r\n"
				+ "\r\n"
				+ "</html>\r\n"
				+ "");

		return sBuilder.toString();
	}

	public static void sendEmail(String recipient, String subject, String body)
	{
		try 
		{ 
			// MimeMessage object. 
			MimeMessage message = new MimeMessage(SendEmailSession.getInstance().getSession()); 

			// Set From Field: adding senders email to from field. 
			message.setFrom(new InternetAddress(SendEmailSession.getInstance().getFrom())); 

			// Set To Field: adding recipient's email to from field. 
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient)); 

			// Set Subject: subject of the email 
			message.setSubject(subject); 

			// set body of the email and append our signature for identifying emails to parse responses on
			message.setContent(body, "text/html");  

			// Send email. 
			Transport.send(message); 
			LOGGER.info("Mail successfully sent: " 
					+ " To(" + recipient +")"
					+ " Subject(" + subject + ")"); 
		} 
		catch (MessagingException mex)  
		{ 
			LOGGER.error("Error sending message: " 
					+ " To(" + recipient +")"
					+ " Subject(" + subject + ")");
			mex.printStackTrace();
		} 

	}

	private String buildTaskIdSignature(String taskId)
	{
		String resultString = "{Task Id:" + taskId + "}";

		// Assert that it conforms to our Pattern above
		// since this pattern gets used "the other side of the fence"
		// to identify the message being built here
		Pattern pattern = TASK_ID_PATTERN;
		Matcher matcher = pattern.matcher(resultString);
		assert(matcher.matches());

		return resultString;
	}
	
}