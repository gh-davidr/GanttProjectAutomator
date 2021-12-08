#!/bin/bash

rootdir=CHANGEME-to-install-dir
dir="${rootdir}"
log=CHANGEME-to-log-dir/GanttProjectAutomator.log
jar="${rootdir}/GanttProjectAutomator.jar"
proj_yml=CHANGEME-to-path-to-project-yml-file
option=Request-Update

java -Xmx1024m -Xms128m -jar $jar -m $option -f ${proj_yml} 2>&1 | tee -a $log