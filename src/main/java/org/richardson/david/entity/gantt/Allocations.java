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

import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement(name = "allocations")
@XmlAccessorType(XmlAccessType.FIELD)  
public class Allocations {
    @XmlElement(name="allocation") private ArrayList<Allocation> allocations;    
}
