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
import org.richardson.david.config.UserConfig;
import org.richardson.david.control.AppControl;
import org.richardson.david.model.EnrichedTask;
import org.richardson.david.model.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

// Sends email notifications for a particular task

@Getter
public class EmailMessageTaskStart extends EmailMessageBaseTask
{
	private static Logger LOGGER = LoggerFactory.getLogger(AppControl.class);

	private Double fractionForNotificationDouble = null;

	public EmailMessageTaskStart(EnrichedTask enrichedTask) {
		super(enrichedTask);
	}

	public EmailMessageTaskStart(EnrichedTask enrichedTask, Double fractionForNotificationDouble) {
		super(enrichedTask);
		this.fractionForNotificationDouble = fractionForNotificationDouble;
	}

	public void sendInitialEmails()
	{
		sendEmails(false);
	}

	public void sendReminderEmails()
	{
		sendEmails(true);
	}

	private void sendEmails(Boolean reminder)
	{
		Long completeLong = getEnrichedTask().getCompleteLong();
		if (completeLong == 0L) // Only case we email
		{
			if (getEnrichedTask().getResources() != null) getEnrichedTask().getResources().forEach(r ->
			{
				if (allowRecipient(r.getContacts().toLowerCase()))
					sendEmail(r.getContacts(), 
							
							"Task '" + getTask().getName() + (reminder ? "' start date has passed" : "' is due to start today"),
							
							// Way too complex a subject
//							(reminder 
//									? "REPEAT NOTIFICATION " +
//									(fractionForNotificationDouble != null && fractionForNotificationDouble > 0 // Include a message only if based on a fraction amount 
//									? "(Now " + fractionForNotificationDouble * 100 + " percent through from start to end) " : "")
//									: "") + 
//							"Project '" + Repository.getInstance().getProject().getName() + "'"
//							+ " Task '" + getTask().getName() + (reminder ? "' start date has passed" : "' is due to start today") ,
							
							getBodyHTML(r.getName(), reminder));
			});

			if (getEnrichedTask().getResources() != null && getEnrichedTask().getResources().size() == 0)
			{
				LOGGER.info("NOTE: Task '{}' should be notified but has no associated resource", getEnrichedTask().getTask().getName());
				
				// Let's email the PM with this information ...
				
				String pm = UserConfig.getInstance().getNotify().getAlertEmail();
				
				sendEmail(pm, 
						"TASK WITH NO ASSIGNEES : " +
						(reminder 
								? "REPEAT NOTIFICATION " : "") + 
						"Project '" + Repository.getInstance().getProject().getName() + "'"
						+ " Task '" + getTask().getName() + (reminder ? "' start date has passed" : "' is due to start today") ,
						getBodyHTML(pm, reminder));
			
			}
		}
	}

	private String getBodyHTML(String r, Boolean reminder)
	{
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append(
				"<html xmlns:v=\"urn:schemas-microsoft-com:vml\"\r\n"
						+ "xmlns:o=\"urn:schemas-microsoft-com:office:office\"\r\n"
						+ "xmlns:w=\"urn:schemas-microsoft-com:office:word\"\r\n"
						+ "xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\r\n"
						+ "xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\r\n"
						+ "xmlns=\"http://www.w3.org/TR/REC-html40\">\r\n"
						+ "\r\n"
						+ "<head>\r\n"
						+ "<meta http-equiv=Content-Type content=\"text/html; charset=unicode\">\r\n"
						+ "<meta name=ProgId content=Word.Document>\r\n"
						+ "<meta name=Generator content=\"Microsoft Word 15\">\r\n"
						+ "<meta name=Originator content=\"Microsoft Word 15\">\r\n"
						+ "\r\n"
						+ "</style>\r\n"
						+ "\r\n"
						+ "</head>\r\n"
						+ "\r\n"
						+ "<body lang=EN-GB link=blue vlink=purple style='tab-interval:36.0pt;word-wrap:\r\n"
						+ "break-word'>\r\n"
						+ "\r\n"
						+ "<div class=WordSection1>\r\n"
						+ "\r\n"
						+ "<p class=MsoNormal>Dear " + r + ",<o:p></o:p></p>\r\n"
						+ "\r\n"
						+ "<p class=MsoNormal><o:p>&nbsp;</o:p></p>\r\n"
						+ "\r\n"

						+ (
								reminder == true 
								? "<p class=MsoNormal>The start date for the task below has passed and the plan records no progress as yet.\r\n"

								+ (fractionForNotificationDouble != null ? "  (This task is now " + fractionForNotificationDouble * 100 + " percent through from start to end)" : "")

								+ "The system is configured to send reminders at various points between the start and end dates if no progress is recorded.\r\n"
								+ "These points are typically start, end and various fractions along the way.  The configuration for this project is: (" 
								+   UserConfig.getInstance().getNotify().getNotifications() + ")<o:p></o:p></p>\r\n"
								: "<p class=MsoNormal>The task below is due to start and run as indicated in the table below.<o:p></o:p></p>\r\n"
								)

						+ "\r\n"
						+ "<p class=MsoNormal><o:p>&nbsp;</o:p></p>\r\n"
						+ "\r\n"

						+ getTaskTableHTML()

						+ "<p class=MsoNormal>"
						+ "Please note that as a task assignee, you can have progress updates automatically applied to the Project Plan by simply replying to this email.<br>\r\n"
						+ "Just state the progress in the body of the reply.  You can use natural text as in the examples below:<br>\r\n"
						+ "<ul>\r\n"						
						+ "<li>I'm a Quarter finished</li>\r\n"                       
						+ "<li>It's about 1/4 completed</li>\r\n"                         
						+ "<li>.25 done</li>\r\n"                          
						+ "<li>0.25 finished</li>\r\n"                       
						+ "<li>It is quarter complete</li>\r\n"
						+ "<li>It is 1/4 done</li>\r\n"               
						+ "<li>Just about 0.25  finished</li>\r\n"     
						+ "<li>Just about 0.250 finished</li>\r\n"   
						+ "<li>I've had issues and really I've not started</li>\r\n"  
						+ "<li>Finally, it is finished</li>\r\n"  
						+ "</ul>\r\n"
						+ "<br>\r\n"
						+ "Once your response has been processed (a process polls every few minutes), an acknowledgement email will be sent back confirming the change to the task.<br>\r\n"
						+ "<o:p></o:p></p>\r\n"
						+ "\r\n"

						+ getEmailSignature()
				);

		return sBuilder.toString();
	}

}
