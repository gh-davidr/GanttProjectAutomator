# GanttProjectAutomator
GanttProjectAutomator is a utility conceived from years of project management experience to make the communication of task status just a little bit easier.

## Summary

- Automates visibility of tasks in a GanttProject file by emailing assignees as the task start date arrives (and at configurable intervals if completion remains 0%)
- Automates status updates on GanttProject file from assignees by handling email replies on progress
- Generates overall project summary
  - Active tasks (start date <= today <= end date) with zero completion
  - In flight tasks (0 < completion < 100 %)
  - Upcoming tasks
  - Completed tasks
- Generates summary reminder for an individual

## Technical Detail

- Developed as a command line application in Java by David Richardson
- Has a fully-fledged DOM in-memory model to represent the GanttProject XML file
- Requires access to a sending and receiving email account
  - The application will send emails on behalf of this email account to project team members
  - The application will also read emails in this email account looking for responses or requests from project team members
- Is stateless
  - Uses the GanttProject file to define project state and drive actions
  - Also uses emails to identify triggers to act and also for markers to know what&#39;s been processed before
- Intended for use in a scheduled environment (for example, using &quot;cron&quot; or &quot;taskschd&quot;) to look for and notify new tasks starting each day and to handle responses and requests.
- Includes reserved word tokens in sent emails to uniquely identify the Gantt Project and also the specific task.  In this way, the application can be used in a multi-project environment using a single email address
- Build includes a rich suite of tests to verify behaviour

## Typical Use

| **Schedule** | **Command** | **Description** |
| --- | --- | --- |
| **08:00 daily** | runGPA\_notify.sh or runGPA\_notify.bat | Uses a configured sender email address to notify all assignees of tasks due to start that day.  Also sends repeat notifications for tasks with past start date and zero progress |
| **Every 10 minutes, 24x7** | runGPA\_all.sh or runGPA\_all.bat | Reads the sender email inbox looking for task updates (eg &quot;I&#39;m 50% complete&quot;), or requests (eg help, remind, summarise or notify)
| **08:00 Sunday** | runGPA\_summarise.sh or runGPA\_summarise.bat| Sends a project summary to the configured project manager |

## Limitations
- Developed and tested with Gmail accounts only
- Other email accounts should work, but it has not been tested

## Instructions for Use

 - If using Gmail, then set up an App password for the account:
   - See [Sign in using App Passwords](https://support.google.com/mail/answer/185833?hl=en-GB) for instructions
 - Copy the sample yml file to a folder.  You could co-locate it with the project GAN file
 - Modify the yml file and change the 10 lines that reference CHANGEME
    - XML Path to GAN project file
    - Username for email sending account
    - Password for email sending account
    - Notifier title
    - Email signature - add Project Name
    - Backups folder
    - Alerts receiver email address
    - Project Manager (for project summary)
 - Download JAR file to an install folder
 - Download run scripts to the install folder
   - The scripts will need modifying to reference the install folder and also the yml file
 - Decide on scheduling strategy.  Recommended scheduling as described in **[Typical Use](Readme#Typical-Use)** above

## YML File

The YML file has settings for the following features of the program:

- **notify**    - (Finds tasks that start "today" or in the past with no progress and email notifies the assignees)
- **update**    - (Based on the response of an assignee to a notification email, update the progress accoridingly)
- **remind**    - (Generate a summary of tasks (not started, in progress, upcoming) in response to an email request)
- **help**      - (Send a summary of capabilities that GanttProjectAutomator can support)
- **summarise** - (Generate a summary of tasks (not started, in progress, upcoming) for the PM to view)