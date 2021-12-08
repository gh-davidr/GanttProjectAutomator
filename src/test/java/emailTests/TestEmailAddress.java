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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.richardson.david.utils.AppUtils;

public class TestEmailAddress {
	
	
	private static Boolean SHOOSHBoolean = true;

	private static Object[][] EMAIL_ADDRESS_TESTS = {
			{	"david@richardson.org",                                 "david@richardson.org",            null},
			{	"David Richardson <david@richardson.org>",              "david@richardson.org",            "David Richardson"},
			{	"David Richardson Jnr <david_jnr@richardson.org>",      "david_jnr@richardson.org",        "David Richardson Jnr"},
			{	"David Richardson ii  <david2@richardson.org>",         "david2@richardson.org",           "David Richardson ii"},
			{	"David Richardson Minus <david-r@richardson.org>",      "david-r@richardson.org",          "David Richardson Minus"},
			{	"David Richardson Minus <da-vi__d2-r@richardson.org>",  "da-vi__d2-r@richardson.org",      "David Richardson Minus"},
			{	"David Richardson <david.richardson@richardson.org>",   "david.richardson@richardson.org", "David Richardson"},
	};	

	
	@Test
	public void checkSomeEmailAddresses()
	{
		doObjArrayTest(EMAIL_ADDRESS_TESTS);
	}

	private void doObjArrayTest(Object[][] arr)
	{
		for (Object[] textValsObjects : arr)
		{
			Assertions.assertTrue(textValsObjects.length == 3);

			int i = 0;
			String  fullEmail = (String)textValsObjects[i++];
			String  justEmail = (String)textValsObjects[i++];
			String  justName = (String)textValsObjects[i++];

			if (!SHOOSHBoolean)
				System.out.println("Processing fulEmail: " + fullEmail + " to hopefully arrive just at email: " + justEmail + " and name: " + justName);

			String email = AppUtils.justTheEmailAddress(fullEmail);
			String assertString = "Full Email-" + fullEmail + " Just Email-" + justEmail + " Just Name: " + (justName == null ? "null" : justName) 
					+ " Processed to get JustEmail-" + email;
			
			Assertions.assertEquals(email, justEmail, assertString);
			
			if (justName != null)
			{
				String name = AppUtils.justTheName(fullEmail);
				String assertString2 = "Full Email-" + fullEmail + " Just Email-" + justEmail + " Just Name: " + (justName == null ? "null" : justName) 
						+ " Processed to get JustEmail-" + email
						+ " and Processed further to get JustName-" + name;
				
				Assertions.assertEquals(name, justName, assertString2);
			}
		}
	}
	
}
