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

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.richardson.david.entity.gantt.Task;
import org.richardson.david.model.EnrichedTask;
import lombok.Getter;

@Getter
public class EmailMessageBaseTask extends EmailMessageBase
{
	private EnrichedTask enrichedTask;
	@Getter(lazy=true) private final Task task = initTask();
	
	
	public EmailMessageBaseTask() 
	{
		this.enrichedTask = null;
	}

	public EmailMessageBaseTask(EnrichedTask enrichedTask) 
	{
		this.enrichedTask = enrichedTask;
	}

	private Task initTask()
	{
		return enrichedTask != null ? enrichedTask.getTask() : null;
	}

	protected String getTaskTableHTML()
	{
		return getTaskTableHTML(enrichedTask);
	}
	
	protected String getEmailSignature()
	{
		
		return getEmailSignature(enrichedTask);
	}

	protected String quoteOrigEmailHTML(Message message) throws IOException, MessagingException
	{
		StringBuilder sBuilder = new StringBuilder();
		
		MimeMessage orig = (MimeMessage) message;

		sBuilder.append( ""
				+ "<hr>"			
				+ "On " + orig.getReceivedDate() + ", " + orig.getSender() + " wrote: <br><br>"
				+ "Subject: " + orig.getSubject() + "<br><br>"
				+ EmailInbox.getTextFromMessage(orig, false)
				);
		return sBuilder.toString();
	}

}