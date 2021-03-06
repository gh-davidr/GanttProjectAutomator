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
package entityTests;

import org.junit.jupiter.api.Test;
import org.richardson.david.entity.gantt.Depend;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import org.junit.jupiter.api.Assertions;

public class TestEntityDepend extends TestEntityBase {

	private static String TEST1_XML_PROPERTY_String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<depend id=\"61\" type=\"2\" difference=\"90\" hardness=\"Strong\"/>";

	@Test
	void test1() {
		Depend element = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Depend.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			element = (Depend)unmarshaller.unmarshal(stringToStream(TEST1_XML_PROPERTY_String));
		}
		catch (Exception e)
		{
			element = null;
			e.printStackTrace();
		}

		Assertions.assertTrue(element != null && element.getId().equals("61"));
	}
}
