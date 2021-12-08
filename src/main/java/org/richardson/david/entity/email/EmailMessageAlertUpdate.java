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

import javax.mail.Message;

import org.richardson.david.config.UserConfig;
import org.richardson.david.model.EnrichedTask;
import lombok.NonNull;


public class EmailMessageAlertUpdate extends EmailMessageAlertBase
{
	public EmailMessageAlertUpdate(@NonNull Message message, @NonNull EnrichedTask enrichedTask) {
		super("Update", 
				UserConfig.getInstance().getUpdate().getAlertEmail(),
				message, enrichedTask);
	}
}

//@Getter
//public class EmailMessageAlertUpdate extends EmailMessageBaseTask
//{
//	private static Logger LOGGER = LoggerFactory.getLogger(EmailMessageAlertUpdate.class);
//
//	@NonNull private Message message;
//
//	public EmailMessageAlertUpdate(@NonNull Message message, @NonNull EnrichedTask enrichedTask) {
//		super(enrichedTask);
//		this.message = message;
//	}
//
//	public void sendAlertEmail()
//	{
//		String alertReceiverString = UserConfig.getInstance().getUpdate().getAlertEmail();
//		
//		sendEmail(alertReceiverString, 
//				"ALERT : Unauthorized Update request" ,
//				getBodyHTML());
//	}
//
//	private String getBodyHTML()
//	{
//		StringBuilder sBuilder = new StringBuilder();
//
//		sBuilder.append(
//				"<html xmlns:v=\"urn:schemas-microsoft-com:vml\"\r\n"
//						+ "xmlns:o=\"urn:schemas-microsoft-com:office:office\"\r\n"
//						+ "xmlns:w=\"urn:schemas-microsoft-com:office:word\"\r\n"
//						+ "xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\r\n"
//						+ "xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\r\n"
//						+ "xmlns=\"http://www.w3.org/TR/REC-html40\">\r\n"
//						+ "\r\n"
//						+ "<head>\r\n"
//						+ "<meta http-equiv=Content-Type content=\"text/html; charset=unicode\">\r\n"
//						+ "<meta name=ProgId content=Word.Document>\r\n"
//						+ "<meta name=Generator content=\"Microsoft Word 15\">\r\n"
//						+ "<meta name=Originator content=\"Microsoft Word 15\">\r\n"
//						+ "\r\n"
//						+ "</style>\r\n"
//						+ "\r\n"
//						+ "</head>\r\n"
//						+ "\r\n"
//						+ "<body lang=EN-GB link=blue vlink=purple style='tab-interval:36.0pt;word-wrap:\r\n"
//						+ "break-word'>\r\n"
//						+ "\r\n"
//						+ "<div class=WordSection1>\r\n"
//						+ "\r\n"
//						+ "<p class=MsoNormal>Please note that an unauthorised email sender has tried to update the status on this task."
//						+ "<br>"
//						+ "The option is set to enforce updates only from email addresses on the assigned resources."
//						+ ":<o:p></o:p></p>\r\n"
//						+ "\r\n"
//
//						+ getTaskTableHTML()
//						+ getEmailSignature()
//				);
//		
//		try {
//			sBuilder.append(quoteOrigEmailHTML(message));
//		} catch (IOException | MessagingException e) {
//			LOGGER.error("Error quoting original for Update Alert email\n" + e.toString());
//		}
//		
//		return sBuilder.toString();
//	}
//	
//}