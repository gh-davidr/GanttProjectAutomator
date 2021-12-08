@ECHO OFF

SET rootdir=CHANGEME-to-install-dir
SET dir=%rootdir%
SET log=CHANGEME-to-log-dir\GanttProjectAutomator.log
SET jar=%rootdir%\GanttProjectAutomator.jar
SET proj_yml=CHANGEME-to-path-to-project-yml-file
SET option=Notify

powershell "java -Xmx1024m -Xms128m -jar %jar% -m %option% -f %proj_yml% | tee -Append %log%"