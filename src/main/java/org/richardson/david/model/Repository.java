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
package org.richardson.david.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.richardson.david.config.UserConfig;
import org.richardson.david.entity.email.EmailMessageTaskStart;
import org.richardson.david.entity.gantt.Allocation;
import org.richardson.david.entity.gantt.Project;
import org.richardson.david.entity.gantt.Resource;
import org.richardson.david.entity.gantt.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Value;

@Value
public class Repository 
{
	private static Logger LOGGER = LoggerFactory.getLogger(Repository.class);

	private static Repository m_Instance               = null;

	private final Project                                project;

	private final HashMap<String, ArrayList<Allocation>> allocationTaskIdHashMap = new HashMap<>();
	private final HashMap<String, ArrayList<Allocation>> allocationResourceIdHashMap = new HashMap<>();
	private final HashMap<String, EnrichedResource>      enrichedResourceHashMap = new HashMap<>();
	private final HashMap<String, EnrichedResource>      enrichedResourceEmailHashMap = new HashMap<>();
	private final HashMap<String, EnrichedTask>          enrichedTaskHashMap = new HashMap<>();
	private final ArrayList<EnrichedTask>                orderedEnrichedTaskList = new ArrayList<>();

	private enum DateComparison
	{
		unknown,
		earlierThan,
		laterThan,
		equalTo,		
	}

	private Repository(Project project)
	{
		this.project = project;
		initialize();
	}

	public static Repository getInstance(Project project)
	{
		if (m_Instance == null)
		{
			m_Instance = new Repository(project);
		}
		return m_Instance;
	}

	public static Repository getInstance()
	{
		return m_Instance;
	}

	public ArrayList<EnrichedTask> getTasksStartingEarlierThan(Date date)
	{
		return getEnrichedTasksRelativeToDate(date, DateComparison.earlierThan, true, null);
	}
	public ArrayList<EnrichedTask> getTasksStartingLaterThan(Date date)
	{
		return getEnrichedTasksRelativeToDate(date, DateComparison.laterThan, true, null);
	}
	public ArrayList<EnrichedTask> getTasksStartingBetween(Date fromDate, Date toDate)
	{
		return getEnrichedTasksBetweenDates(fromDate, toDate, true, null);
	}
	public ArrayList<EnrichedTask> getTasksStartingOn(Date date)
	{
		return getEnrichedTasksRelativeToDate(date, DateComparison.equalTo, true, null);
	}
	public ArrayList<EnrichedTask> getEnrichedTasksInProgress()
	{
		ArrayList<EnrichedTask> result = new ArrayList<EnrichedTask>();
		orderedEnrichedTaskList.forEach(t ->
		{
			if (!t.getTask().getComplete().equals("0")) result.add(t);
		});
		return result;
	}

	public ArrayList<EnrichedTask> getTasksStartingEarlierThan(Date date, Boolean onlyNotStarted)
	{
		return getEnrichedTasksRelativeToDate(date, DateComparison.earlierThan, onlyNotStarted, null);
	}
	public ArrayList<EnrichedTask> getTasksStartingLaterThan(Date date, Boolean onlyNotStarted)
	{
		return getEnrichedTasksRelativeToDate(date, DateComparison.laterThan, onlyNotStarted, null);
	}
	public ArrayList<EnrichedTask> getTasksStartingBetween(Date fromDate, Date toDate, Boolean onlyNotStarted)
	{
		return getEnrichedTasksBetweenDates(fromDate, toDate, onlyNotStarted, null);
	}
	public ArrayList<EnrichedTask> getTasksStartingOn(Date date, Boolean onlyNotStarted)
	{
		return getEnrichedTasksRelativeToDate(date, DateComparison.equalTo, onlyNotStarted, null);
	}

	public ArrayList<EnrichedTask> getTasksStartingEarlierThan(Date date, Boolean onlyNotStarted, Resource resource)
	{
		return getEnrichedTasksRelativeToDate(date, DateComparison.earlierThan, onlyNotStarted, resource);
	}
	public ArrayList<EnrichedTask> getTasksStartingLaterThan(Date date, Boolean onlyNotStarted, Resource resource)
	{
		return getEnrichedTasksRelativeToDate(date, DateComparison.laterThan, onlyNotStarted, resource);
	}
	public ArrayList<EnrichedTask> getTasksStartingBetween(Date fromDate, Date toDate, Boolean onlyNotStarted, Resource resource)
	{
		return getEnrichedTasksBetweenDates(fromDate, toDate, onlyNotStarted, resource);
	}
	public ArrayList<EnrichedTask> getTasksStartingOn(Date date, Boolean onlyNotStarted, Resource resource)
	{
		return getEnrichedTasksRelativeToDate(date, DateComparison.equalTo, onlyNotStarted, resource);
	}
	public ArrayList<EnrichedTask> getEnrichedTasksInProgress(Resource resource)
	{
		ArrayList<EnrichedTask> result = new ArrayList<EnrichedTask>();
		orderedEnrichedTaskList.forEach(t ->
		{			
			if ( (resource == null || t.getResourceHashMap().get(resource.getId()) != null)
					&& (!t.getTask().getComplete().equals("0")) 
					&& (!t.getTask().getComplete().equals("100")) ) 
				result.add(t);
		});
		return result;
	}
	public ArrayList<EnrichedTask> getEnrichedTasksCompleted(Resource resource)
	{
		ArrayList<EnrichedTask> result = new ArrayList<EnrichedTask>();
		orderedEnrichedTaskList.forEach(t ->
		{			
			if ( (resource == null || t.getResourceHashMap().get(resource.getId()) != null) 
					&& (t.getTask().getComplete().equals("100")) ) 
				result.add(t);
		});
		return result;
	}
	public EnrichedTask findEnrichedTaskFromEmail(String body)
	{
		EnrichedTask resultTask = null;		
		Pattern pattern = EmailMessageTaskStart.getTASK_ID_PATTERN();

		String[] strings = body.replaceAll("\\r", "").split("\n");
		if (strings != null)
		{
			for (String string : strings)
			{
				Matcher matcher = pattern.matcher(string);
				if (resultTask == null && matcher.matches())
				{
					String taskIdString = matcher.group(1);

					resultTask = enrichedTaskHashMap.get(taskIdString);
				}
			}
		}

		return resultTask;
	}

	public Boolean messageForThisProject(String body)
	{
		// Body needs to reference the project with keyword exactly as it is on a line on its own
		Boolean resultBoolean = false;
		String projectSignature = UserConfig.getInstance().getCommon().getEmailSignature();
		String[] strings = body.replaceAll("\\r", "").split("\n");
		if (strings != null)
		{
			for (String string : strings)
			{
				resultBoolean = string.equals(projectSignature) ? true : resultBoolean;
			}

		}
		return resultBoolean;
	}

	public Boolean messageIsARemind(String subject)
	{
		// Subject needs to include keyword "remind" or "reminder"
		return subject.toLowerCase().contains("remind");		
	}

	public Boolean messageIsAPleaForHelp(String subject)
	{
		// Subject needs to be just the single key word "help"
		return subject != null && subject.toLowerCase().equals("help");		
	}

	public Boolean messageIsAPleaForNotifications(String subject)
	{
		// Subject needs to be just the single key word "help"
		return subject != null && subject.toLowerCase().equals("notify");		
	}

	public Boolean messageIsAPleaForSummarise(String subject)
	{
		// Subject needs to be just the single key word "help"
		return subject != null && subject.toLowerCase().equals("summarise");		
	}

	public Boolean messageReferencesATask(String body)
	{
		Boolean resultBoolean = false;
		Pattern pattern = EmailMessageTaskStart.getTASK_ID_PATTERN();
		String[] strings = body.replaceAll("\\r", "").split("\n");
		if (strings != null)
		{
			for (String string : strings)
			{
				Matcher matcher = pattern.matcher(string);
				resultBoolean = matcher.matches() ? true : resultBoolean;
			}
		}
		return resultBoolean;
	}

	public EnrichedResource findEnrichedResourceFromEmail(String body, String subject, String sender)
	{
		EnrichedResource resultEnrichedResource = null;

		// 1 Subject needs to include keyword "remind" or "reminder"
		// 2 Body needs to reference the project with keyword
		// 3 sender needs to match a resource

		if (messageForThisProject(body) && messageIsARemind(subject))
		{
			resultEnrichedResource = findEnrichedResourceFromEmail(sender);	
		}

		return resultEnrichedResource;
	}

	public EnrichedResource findEnrichedResourceFromEmail(String sender)
	{
		EnrichedResource resultEnrichedResource = null;
		if (sender != null)
		{
			resultEnrichedResource = enrichedResourceEmailHashMap.get(sender.toLowerCase());	
		}

		return resultEnrichedResource;
	}


	private ArrayList<EnrichedTask> getEnrichedTasksRelativeToDate(Date date, DateComparison dateComparison, Boolean onlyNotStarted, Resource resource)
	{
		ArrayList<EnrichedTask> result = new ArrayList<EnrichedTask>();

		AtomicBoolean seekingAtomicBoolean = new AtomicBoolean(true);

		orderedEnrichedTaskList.forEach(t ->
		{
			if (seekingAtomicBoolean.get())
			{
				if ( (dateComparison == DateComparison.earlierThan && t.getStartDate().compareTo(date) < 0)
						|| (dateComparison == DateComparison.laterThan && t.getStartDate().compareTo(date) > 0)
						|| (dateComparison == DateComparison.equalTo && t.getStartDate().compareTo(date) == 0))
				{
					if ( (resource == null || t.getResourceHashMap().get(resource.getId()) != null) && (!onlyNotStarted || t.getTask().getComplete().equals("0")) ) result.add(t);
				}
				else
				{
					seekingAtomicBoolean.set(true);
				}
			}
		});
		return result;
	}

	private ArrayList<EnrichedTask> getEnrichedTasksBetweenDates(Date fromDate, Date toDate, Boolean onlyNotStarted, Resource resource)
	{
		ArrayList<EnrichedTask> result = new ArrayList<EnrichedTask>();
		orderedEnrichedTaskList.forEach(t ->
		{
			if (fromDate.compareTo(t.getStartDate()) <= 0 && toDate.compareTo(t.getStartDate()) >= 0)
			{
				if ( (resource == null || t.getResourceHashMap().get(resource.getId()) != null) && (!onlyNotStarted || t.getTask().getComplete().equals("0")) ) result.add(t);
			}
		});
		return result;
	}

	private void initialize()
	{
		// add all tasks, resources & allocations to our hash maps
		project.getTasks().getTasks().forEach(t -> hashTask(t));
		LOGGER.debug("Repository - Loaded: " + enrichedTaskHashMap.size() + " tasks" );
		orderedEnrichedTaskList.sort((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate()));

		project.getResources().getResources().forEach(r -> hashResource(r));
		LOGGER.debug("Repository - Loaded: " + enrichedResourceHashMap.size() + " resources" );

		project.getAllocations().getAllocations().forEach(a -> { 
			// Allocations keyed by task id
			addAllocation(a, allocationTaskIdHashMap, a.getTaskId());
			// Allocations keyed by resource id
			addAllocation(a, allocationResourceIdHashMap, a.getResourceId());	
		});
		LOGGER.debug("Repository - Loaded: " + project.getAllocations().getAllocations().size() + " allocations" );
	}

	private void hashTask(Task task)
	{
		EnrichedTask enrichedTask = new EnrichedTask(task);
		enrichedTaskHashMap.put(task.getId(), enrichedTask);
		orderedEnrichedTaskList.add(enrichedTask);
		if (task.getTasks() != null) for (Task task2 : task.getTasks()) hashTask(task2);
	}

	private void hashResource(Resource resource)
	{
		EnrichedResource enrichedResource = new EnrichedResource(resource);
		enrichedResourceHashMap.put(resource.getId(), enrichedResource);
		enrichedResourceEmailHashMap.put(resource.getContacts().toLowerCase(), enrichedResource);
	}

	private void addAllocation(Allocation allocation, HashMap<String, ArrayList<Allocation>> hashMap, String id)
	{
		ArrayList<Allocation> list = hashMap.get(id);
		if (list == null) {
			list = new ArrayList<Allocation>();
			hashMap.put(id, list);
		}
		list.add(allocation);
	}

}
