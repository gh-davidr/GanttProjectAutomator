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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.richardson.david.entity.gantt.Task;
import org.richardson.david.model.EnrichedTask;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class TaskStartEmailResponseParser {

	@Getter private static Pattern NOT_STARTED_PATTERN      = Pattern.compile(".*[\\s]*NOT[\\s]*STARTED[\\s]*.*");
	@Getter private static Pattern QUARTER_PATTERN          = Pattern.compile(".*[\\s]*(QUARTER|1/4|[0]?\\.25)[\\s]*.*");
	@Getter private static Pattern THIRD_PATTERN            = Pattern.compile(".*[\\s]*(THIRD|1/3|[0]?\\.33[3]*)[\\s]*.*");
	@Getter private static Pattern HALF_PATTERN             = Pattern.compile("^.*[\\s]*(HALF|1/2|[0]?\\.5)[\\s]*.*$");
	@Getter private static Pattern TWO_THIRDS_PATTERN       = Pattern.compile(".*[\\s]*(TWO[ ]+THIRD|2/3|[0]?\\.66[6]*)[\\s]*.*");
	@Getter private static Pattern THREE_QUARTERS_PATTERN   = Pattern.compile(".*[\\s]*(THREE[ ]+QUARTER|3/4|[0]?\\.75)[\\s]*.*");
	@Getter private static Pattern COMPLETE_PATTERN         = Pattern.compile(".*[\\s]*(DONE|FINISH|FINISHED|COMPLETE)[\\s]*.*");
	@Getter private static Pattern FRACTION_PATTERN         = Pattern.compile(".*[\\s]*([\\d]+)\\/([\\d]+)[\\s]*.*");
	@Getter private static Pattern PERCENT_PATTERN          = Pattern.compile("\\D*([\\d]+)\\s*(\\%|PER|PERCENT)[\\s]*.*");
	@Getter private static Pattern DECIMAL_PATTERN          = Pattern.compile("\\D*([0]?\\.?[\\d]+)[\\s]*.*");

	@NonNull private String messageBody;
	@NonNull private EnrichedTask enrichedTask;
	@Getter(lazy=true) private Task task = initTask();

	@Getter(lazy=true) Boolean taskUpdated = initTaskUpdated();

	private Boolean initTaskUpdated()
	{
		Boolean resultBoolean = false;

		Long currentCompLong = getTask().getCompleteLong();

		Long newCompLong = getCompletionFromBody(messageBody);

		if (newCompLong >= 0)
		{
			if (!newCompLong.equals(currentCompLong))
			{
				resultBoolean = true;
				getTask().setComplete(newCompLong.toString());
			}
		}

		return resultBoolean;
	}

	private Task initTask()
	{
		return enrichedTask.getTask();
	}

	private Long getCompletionFromBody(String body)
	{
		Long resultLong = -1L;

		String[] strings = body
				.replaceAll("\\r", "")
				.replaceAll("'", "")
				.replaceAll("\"", "")
				.toUpperCase()
				.split("\n");
		if (strings != null)
		{
			for (String string : strings)
			{
				if (resultLong < 0)
				{
					resultLong = getCompletionFromLine(string);
				}
			}
		}

		return resultLong;
	}

	private Long getCompletionFromLine(String line)
	{
		Long resultLong = -1L;

		// Exclude lines for task id, project or reserved pattern
		if (!patternMatch(line, EmailMessageTaskStart.getUPCASE_TASK_ID_PATTERN()) 
				&& !patternMatch(line, EmailMessageTaskStart.getUPCASE_PROJECT_PATTERN())
				&& !patternMatch(line, EmailMessageTaskStart.getUPCASE_RESERVED_PATTERN()))
		{
			// These pattern matchers return true or false 
			// and we set known fixed value accordingly
			if      (patternMatch(line, NOT_STARTED_PATTERN))      resultLong = 0L;
			else if (patternMatch(line, TWO_THIRDS_PATTERN))       resultLong = 66L;
			else if (patternMatch(line, THREE_QUARTERS_PATTERN))   resultLong = 75L;

			else if (patternMatch(line, QUARTER_PATTERN))          resultLong = 25L;
			else if (patternMatch(line, THIRD_PATTERN))            resultLong = 33L;
			else if (patternMatch(line, HALF_PATTERN))             resultLong = 50L;

			// These pattern matchers rely on matches() being called
			// to then gain access to the group()
			// Was previously having problems when calling patternMatch()
			// and then separate handleXXX() which effectively called matches()
			// twice.
			// This was OK in the JUnit tests but it failed for any email responses
			// with fraction, percent or decimal values
			else
			{
				Long testVaLong = -1L;

				resultLong = resultLong < 0 && (testVaLong = handleFractionPattern(line)) > 0 ? testVaLong : resultLong;
				resultLong = resultLong < 0 && (testVaLong = handlePercentPattern(line))  > 0 ? testVaLong : resultLong;
				resultLong = resultLong < 0 && (testVaLong = handleDecimalPattern(line))  > 0 ? testVaLong : resultLong;
			}

			if (resultLong < 0 && patternMatch(line, COMPLETE_PATTERN))         resultLong = 100L;


			// Sanity check ...
			if (resultLong > 100) resultLong = 100L;
		}

		return resultLong;
	}

	private Boolean patternMatch(String string, Pattern pattern)
	{
		Boolean resultBoolean = false;
		Matcher matcher = pattern.matcher(string);
		resultBoolean = matcher.matches();
		return resultBoolean;
	}

	private Long handleFractionPattern(String string)
	{
		Long result = -1L;

		Matcher matcher = FRACTION_PATTERN.matcher(string);
		if (matcher.matches())
		{
			String numerator   = matcher.group(1);
			String denominator = matcher.group(2);
			Integer intNumInteger = Integer.parseInt(numerator);
			Integer intDenomInteger = Integer.parseInt(denominator);
			if (!intDenomInteger.equals(0))
			{
				result = Long.valueOf((intNumInteger * 100) / intDenomInteger);
			}

		}

		return result;
	}

	private Long handlePercentPattern(String string)
	{
		Long result = -1L;
		Matcher matcher = PERCENT_PATTERN.matcher(string);
		if (matcher.matches())
		{
			String val = matcher.group(1);
			result = Long.valueOf(val);
		}
		return result;
	}

	private Long handleDecimalPattern(String string)
	{
		Long result = -1L;
		Matcher matcher = DECIMAL_PATTERN.matcher(string);
		if (matcher.matches())
		{
			String val = matcher.group(1);
			Double dblValDouble = Double.parseDouble(val);

			if (dblValDouble < 1) dblValDouble = dblValDouble * 100;

			result = Long.valueOf(dblValDouble.intValue());
		}
		return result;
	}


}
