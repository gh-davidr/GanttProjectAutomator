package loadTests;

import org.junit.jupiter.api.Assertions;
import org.richardson.david.load.DataLoader;
import org.richardson.david.load.DataSaver;

import controlTests.TestCommandLine;
import utils.UtilsForTests;

public class TestLoadBase 
{
	public DataSaver createDataSaver()
	{
		TestCommandLine testCommandLine = new TestCommandLine();		
		DataLoader dataLoader = new DataLoader(testCommandLine.getProjectFilePath());
		Assertions.assertTrue(dataLoader.getProject() != null);

		DataSaver dataSaver = new DataSaver(dataLoader);
		String backupFileString = dataSaver.getBackupFileName();
		dataSaver.backupFile();
		UtilsForTests.assertFileExists(backupFileString);
		return dataSaver;
	}
}
