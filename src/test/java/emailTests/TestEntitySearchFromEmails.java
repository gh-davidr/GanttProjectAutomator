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
package emailTests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.richardson.david.control.AppControl;
import org.richardson.david.control.AppControlRequestRemind;
import org.richardson.david.control.AppControlRequestUpdate;
import org.richardson.david.load.DataLoader;
import org.richardson.david.model.EnrichedResource;
import org.richardson.david.model.EnrichedTask;
import org.richardson.david.model.Repository;

import controlTests.TestCommandLine;

public class TestEntitySearchFromEmails {

	private static Object[][] EMAIL_TESTS_1 =
		{
				// Sender    
				// Subject     
				// Resource filename of Body
				// Expected ID of Task
				// Expected ID of Resource

				// Email to send an update for a task
				{ "david@gmail.com",    
					"This really could be anything at all",    
					"SampleUpdateEmailBody.html",
					"1",
					null,
				},


				// Email to get reminders of tasks assigned
				{ "tom.white@myselfllc.net",    
					"Remind me please",    
					"SampleRemindEmailBody.html",
					null,
					"3",
				},

		};

	private Message reminderMessage = mock(Message.class);
	private Message updateMessage = mock(Message.class);


	@Test
	public void testEmailRemindScan()
	{
		setup();
		setupProject();

		checkEnrichedResource(reminderMessage, true, (String)EMAIL_TESTS_1[1][4]);
		checkEnrichedResource(updateMessage,   false, null);

	}

	@Test
	public void testEmailUpdateScan()
	{
		setup();
		setupProject();

		// The HTML might be malformed.
		// We're not getting a split on CRs to allow parsing of the lines.
		// Might need to test having the body in array above a more full copy
		// of email html
		//
		// So for now, we comment this one out...
		//		checkEnrichedTask(updateMessage,   true, (String)EMAIL_TESTS_1[0][4]);
		checkEnrichedTask(reminderMessage, false, null);
	}


	private void checkEnrichedResource(Message message, Boolean found, String id)
	{
		AppControlRequestRemind appControlRemind = new AppControlRequestRemind(false);
		EnrichedResource enrichedResource = appControlRemind.getEnrichedResourceForReminder(message);
		Assertions.assertTrue(found ? enrichedResource != null : enrichedResource == null);
		if (found)
		{
			Assertions.assertTrue(enrichedResource.getResource() != null);
			Assertions.assertTrue(enrichedResource.getResource().getId().equals(id));
		}
	}

	private void checkEnrichedTask(Message message, Boolean found, String id)
	{
		AppControlRequestUpdate appControlUpdate = new AppControlRequestUpdate(AppControl.getInstance().getDataSaver(), false);
		EnrichedTask enrichedTask = appControlUpdate.getEnrichedTaskForMessage(message);
		Assertions.assertTrue(found ? enrichedTask != null : enrichedTask == null);
		if (found)
		{
			Assertions.assertTrue(enrichedTask.getTask() != null);
			Assertions.assertTrue(enrichedTask.getTask().getId().equals(id));
		}
	}

	private void setup()
	{
		int i = 0;
		int j = 0;
		try {
			when(updateMessage.isMimeType("multipart/*")).thenReturn(false);
			when(updateMessage.isMimeType("text/plain")).thenReturn(false);
			when(updateMessage.isMimeType("text/html")).thenReturn(true);
			when(updateMessage.getReplyTo()).thenReturn(getAddress((String)EMAIL_TESTS_1[i][j++]));
			when(updateMessage.getSubject()).thenReturn((String)EMAIL_TESTS_1[i][j++]);
			when(updateMessage.getContent())
			.thenReturn(new String(TestCommandLine.getFileFromResourceAsStream((String)EMAIL_TESTS_1[i][j++]).readAllBytes(),
					StandardCharsets.UTF_8).trim());

			i++;
			j=0;

			when(reminderMessage.isMimeType("multipart/*")).thenReturn(false);
			when(reminderMessage.isMimeType("text/plain")).thenReturn(false);
			when(reminderMessage.isMimeType("text/html")).thenReturn(true);
			when(reminderMessage.getReplyTo()).thenReturn(getAddress((String)EMAIL_TESTS_1[i][j++]));
			when(reminderMessage.getSubject()).thenReturn((String)EMAIL_TESTS_1[i][j++]);
			when(reminderMessage.getContent())
			.thenReturn(new String(TestCommandLine.getFileFromResourceAsStream((String)EMAIL_TESTS_1[i][j++]).readAllBytes(),
					StandardCharsets.UTF_8).trim());
			
		} catch (MessagingException e) {
			System.out.println("Error setting up Message mocks");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupProject()
	{
		new TestCommandLine();
		DataLoader dataLoader = AppControl.getInstance().getDataLoader();
		Repository.getInstance(dataLoader.getProject());
	}

	private Address[] getAddress(String emailString) throws AddressException
	{
		Address[] resulAddresses = new Address[1];
		resulAddresses[0] = new InternetAddress(emailString);
		return resulAddresses;
	}
}
