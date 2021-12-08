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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.FIELD) 
public class Task {

	//	private static Logger LOGGER = LoggerFactory.getLogger(Task.class);
	//	private static final SimpleDateFormat SD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@XmlAttribute private String id;
	@XmlAttribute private String name;
	@XmlAttribute private String meeting;
	@XmlAttribute private String start;
	@XmlAttribute private String duration;
	@XmlAttribute private String complete;
	@XmlAttribute private String expand;
	@XmlAttribute private String thirdDate;
	@XmlAttribute(name = "thirdDate-constraint") private String thirdDateConstraint;

	// Note that this is referenced in the DataSaver as an Element for CDATA output
	@XmlElement  private String notes;

	@XmlElement(name="task") ArrayList<Task>  tasks;
	@XmlElement(name="depend") ArrayList<Depend> depends;

	public Long getCompleteLong()
	{
		Long resultLong = Long.parseLong(getComplete());
		return resultLong;
	}
	public Long getDurationLong()
	{
		Long resultLong = Long.parseLong(getDuration());
		return resultLong;
	}

	/*
	 * // ---------------------------------------- // Everything below ... // Due to
	 * go - moved to EnrichedTask
	 * 
	 * @Getter(lazy=true) private final ArrayList<Resource> resources =
	 * initResources();
	 * 
	 * @Getter(lazy=true) private final Date startDate = initStartDate();
	 * 
	 * @Getter(lazy=true) private final Date endDate = initEndDate();
	 * 
	 * private ArrayList<Resource> initResources() { ArrayList<Resource>
	 * resultArrayList = new ArrayList<Resource>();
	 * 
	 * // Find all the resources for this task from the Repository
	 * ArrayList<Allocation> allocations =
	 * Repository.getInstance().getAllocationTaskIdHashMap().get(id); if
	 * (allocations != null) { allocations.forEach(a -> { String resourceIdString =
	 * a.getResourceId();
	 * resultArrayList.add(Repository.getInstance().getResourceHashMap().get(
	 * resourceIdString)); }); } return resultArrayList; }
	 * 
	 * private Date initStartDate() { Date resultDate = null;
	 * 
	 * try { resultDate = SD_DATE_FORMAT.parse(start); } catch (ParseException e) {
	 * LOGGER.error("ERROR Parsing Date: " + start + " for task: " + id); }
	 * 
	 * return resultDate; } private Date initEndDate() { Date resultDate =
	 * AppUtils.addDaysToDate(getStartDate(), getDurationLong()); return resultDate;
	 * }
	 */
}
