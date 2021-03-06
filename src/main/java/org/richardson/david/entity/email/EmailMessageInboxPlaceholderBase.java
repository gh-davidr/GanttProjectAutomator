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
import org.richardson.david.model.Repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class EmailMessageInboxPlaceholderBase extends EmailMessageBaseTask {

	@Getter private static String  EMAIL_MONITOR_TAG = String.valueOf("{Automation+Email+Monitor+Tag");
	@Getter private static String  EMAIL_PROJECT_TAG = String.valueOf("{Automation+Email+Project+Tag}:");

	abstract protected String getSubject();
	abstract protected String getInboxMonitorString();
	
	public void sendMonitorPlaceholderEmail()
	{
		EmailMessageBaseTask.sendEmail(getRecipient(), 
				getSubject() ,
				getBodyHTML());
	}
	
	
	public static String addTag(String tagString1, String tagString2)
	{
		return new String(tagString1 + "|" + tagString2);
	}
	
	public static Boolean bodyContainsTagList(String body, String tagString)
	{
		Boolean resultBoolean = false;
		String[] tagStrings = tagString.split("\\|");
		for (String string : tagStrings)
		{
			resultBoolean = body.contains(string) ? true : resultBoolean;
		}
		return resultBoolean;
	}
	
	private String getBodyHTML()
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
						+ "<p class=MsoNormal>Automation Monitor Tag Email<o:p></o:p></p>\r\n"
						+ "<p class=MsoNormal>" + getInboxMonitorString() + "<o:p></o:p></p>\r\n"
						+ "<p class=MsoNormal>" + EMAIL_PROJECT_TAG + Repository.getInstance().getProject().getName() + "<o:p></o:p></p>\r\n"
						+ "\r\n"

						+ getEmailSignature()
				);

		return sBuilder.toString();
	}
	
	private String getRecipient() {
		return UserConfig.getInstance().getCommon().getMail().getUsername();
	}


}
