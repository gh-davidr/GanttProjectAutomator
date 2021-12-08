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

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.richardson.david.entity.gantt.Project;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

public class TestEntityProject extends TestEntityBase {
	
	private static final String HOUSE_BUILD_PROJECT_FILE_STRING = "HouseBuildingSample.gan";

	@Test
	void test1() {
		Project element = loadProject();
		Assertions.assertTrue(element != null);
	}
	
	public String getProjectText()
	{
		InputStream inputStream = getFileFromResourceAsStream(HOUSE_BUILD_PROJECT_FILE_STRING);
		return inputStream.toString();
	}

	protected Project loadProject()
	{
		Project element = null;

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Project.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			element = (Project)unmarshaller.unmarshal(getFileFromResourceAsStream(HOUSE_BUILD_PROJECT_FILE_STRING));
		}
		catch (Exception e)
		{
			element = null;
			e.printStackTrace();
		}
		return element;
	}

}