package jobmanager.tests;

import java.util.Arrays;
import java.util.List;

import alabno.testsuite.TestModule;
import alabno.testsuite.TestStatistics;
import alabno.testsuite.TestUtils;

public class Test {

	private static List<TestModule> modules = Arrays.asList(new JobConfiguratorTest());
	
	public static void main(String[] args)
	{
		TestStatistics stats = new TestStatistics();
		TestUtils.stats = stats;
		
		for (TestModule module : modules)
		{
			module.run(stats);
		}
		
		System.out.println(stats.getFinalMessage());
		System.out.println("Tests for JOBMANAGER completed");
		System.exit(stats.getReturnCode());
	}
	
}
