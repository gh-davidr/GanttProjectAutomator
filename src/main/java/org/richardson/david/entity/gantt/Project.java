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
package org.richardson.david.entity.gantt;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD) 
public class Project {
	
	@XmlAttribute private String name;
	@XmlAttribute private String company;
	@XmlAttribute private String webLink;
	@XmlAttribute(name = "view-date") private String viewDate;
	@XmlAttribute(name = "view-index") private String viewIndex;
	@XmlAttribute(name = "gantt-divider-location") private String ganttDividerLocation;
	@XmlAttribute(name = "resource-divider-location") private String resourceDividerLocation;
	@XmlAttribute private String version;
	@XmlAttribute private String locale;
	
	// Note that this is referenced in the DataSaver as an Element for CDATA output
	@XmlElement(name="description") 	
	private String description;
	
	@XmlElement(name="view") private ArrayList<View> views;
	@XmlElement(name="calendars") private Calendars calendars;
	@XmlElement(name="tasks") private Tasks tasks;
	@XmlElement(name="resources") private Resources resources;
	@XmlElement(name="allocations") private Allocations allocations;
	@XmlElement(name="vacations") private Vacations vacations;
	@XmlElement(name="roles") private ArrayList<Roles> roles;

	private String resource_id;
	private String function;
	private String responsible;
	private String load;
}