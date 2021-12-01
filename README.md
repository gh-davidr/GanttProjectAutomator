# GanttProjectAutomator
GanttProjectAutomator is a utility conceived from years of project management experience to make task status communication just a little bit easier.

## Summary

- Automates visibility of tasks in a GanttProject file by emailing assignees as the task start date arrives (and at configurable intervals if completion remains 0%)
- Automates status updates from assignees by handling email replies on progress
- Generates overall project summary
  - Active tasks (start date \&lt;= today \&lt;= end date) with zero completion
  - In flight tasks (0 \&lt; completion \&lt; 100)
  - Upcoming tasks
  - Completed tasks
- Generates summary reminder for an individual

## Technical Detail

- Developed as a command line application in Java by David Richardson
- Has a fully-fledged DOM model to represent the GanttProject XML file
- Is stateless
  - Uses the GanttProject file to define project state and drive actions
  - Also uses emails to identify triggers to act and also for markers to know what&#39;s been processed before
- Intended for use in a scheduled environment (for example, using &quot;cron&quot; or &quot;taskschd&quot;) to look notify new tasks starting each day and to handle responses and requests.

## Typical Use

| **Schedule** | **Command** | **Description** |
| --- | --- | --- |
| **08:00 daily** | runGPA\_notify.sh | Uses a configured sender email address to notify all assignees of tasks due to start that day.Also sends repeat notifications for tasks with past start date and zero progress |
| **Every 10 minutes, 24x7** | runGPA\_all.sh | Reads the sender email inbox looking for the following:
- Task updates (eg &quot;I&#39;m 50% complete&quot;)
- Requests (eg help, remind, summary)
 |
| **08:00 Sunday** | runGPA\_summarise.sh | Sends a project summary to the configured project manager |
