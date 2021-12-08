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
package org.richardson.david.control;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.richardson.david.config.UserConfig;
import org.richardson.david.entity.email.EmailMessageAlertHelp;
import org.richardson.david.entity.email.EmailMessageHelp;
import org.richardson.david.entity.email.EmailMessageInboxPlaceholderAll;
import org.richardson.david.entity.email.EmailMessageInboxPlaceholderBase;
import org.richardson.david.entity.email.EmailMessageInboxPlaceholderHelp;
import org.richardson.david.entity.email.ReadEmailSession;
import org.richardson.david.model.Repository;
import org.richardson.david.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AppControlRequestHelp extends AppControlBase {

	private static Logger LOGGER = LoggerFactory.getLogger(AppControlRequestHelp.class);

	@NonNull private Boolean reportOnlyBoolean;

	public void doHelp()
	{		
		doHelp(true);
	}

	public int doHelp(Boolean log)
	{
		return doHelp(log, new EmailMessageInboxPlaceholderHelp());	
	}
	
	public int doHelp(Boolean log, EmailMessageInboxPlaceholderBase inboxMonitorPlaceholderEmail)
	{
		return doHelp(new ReadEmailSession(
				EmailMessageInboxPlaceholderBase.addTag(
						new EmailMessageInboxPlaceholderHelp().getInboxMonitorString(), 
						new EmailMessageInboxPlaceholderAll().getInboxMonitorString())),
				log,
				inboxMonitorPlaceholderEmail);	
	}
	
	private int doHelp(ReadEmailSession accountEmails, Boolean log, EmailMessageInboxPlaceholderBase inboxMonitorPlaceholderEmail)
	{
		int helpsSent = 0;
		int alertsRaised = 0;
		
		String pmEmailString = UserConfig.getInstance().getSummarise().getProjectManager().toLowerCase();
		Boolean assigneesAndPMBoolean = UserConfig.getInstance().getHelp().getAssigneesAndPMOnly();
		
		for (Message message : accountEmails.getFolderEmails().getOrderedMessages())
		{
			try {
				String sender = AppUtils.justTheEmailAddress(InternetAddress.toString(message.getReplyTo()).toLowerCase());
				
				
				// Alert if ...
				//  1 Alerts enabled for non-recognised email senders
				//  2 Sender is not a resource on a task
				//  3 Sender is not the PM

				if (assigneesAndPMBoolean &&
						Repository.getInstance().findEnrichedResourceFromEmail(sender) == null &&
						!pmEmailString.equals(sender) )
				{
					EmailMessageAlertHelp alertEmail = new EmailMessageAlertHelp(message);
					alertEmail.sendAlertEmail();
					alertsRaised++;

					LOGGER.info("  " + getRunMode() + "Resource NOT FOUND to provide help: " + sender);
				}
				else 
				{					
					String subject = message.getSubject();

					if (Repository.getInstance().messageIsAPleaForHelp(subject))
					{
						EmailMessageHelp emailMessageHelp = new EmailMessageHelp(sender);
						emailMessageHelp.sendHelpEmail();
						helpsSent++;
					}
				}

			}
			catch (Exception e) {
				LOGGER.error(getRunMode() + "Error handling email responses\n" + e.toString());
			}
		}

		// Put a placeholder email in the list so we don't reprocess task request emails
		// either as valid updates or invalid with alerts raised.
		if (helpsSent + alertsRaised > 0 && inboxMonitorPlaceholderEmail != null)
		{
			inboxMonitorPlaceholderEmail.sendMonitorPlaceholderEmail();			
		}

		if (log)
		{
			if (helpsSent + alertsRaised > 0)
			{
				LOGGER.info(getRunMode() + AppVersion.getAppNameString() + "-" + AppVersion.getVersionString());
			}

			LOGGER.info(getRunMode() + "Help complete.  " +
					(reportOnlyBoolean ? "  Report Only Mode active, so no help messages sent"
							: (helpsSent == 1 ? "One help message sent." : helpsSent + " help messages sent.")));
		}

		return helpsSent + alertsRaised;
	}

}
