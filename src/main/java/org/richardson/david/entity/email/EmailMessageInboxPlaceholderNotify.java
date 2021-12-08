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

import lombok.Getter;

@Getter
public class EmailMessageInboxPlaceholderNotify extends EmailMessageInboxPlaceholderBase 
{
	@Getter static private final String MONITOR_TAG_STRING = "Notify";
	@Getter(lazy=true) private final String inboxMonitorString = initEmailMonitorString();

	@Override
	protected String getSubject() {
		return String.valueOf("Automation Monitor Tag Email - Notify");
	}

	private String initEmailMonitorString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(EmailMessageInboxPlaceholderBase.getEMAIL_MONITOR_TAG() + "+");
		stringBuilder.append(EmailMessageInboxPlaceholderNotify.getMONITOR_TAG_STRING());
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
}