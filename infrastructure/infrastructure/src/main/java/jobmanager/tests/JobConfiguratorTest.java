package jobmanager.tests;
import static alabno.testsuite.TestUtils.*;

import java.net.URL;
import alabno.testsuite.TestModule;
import alabno.testsuite.TestStatistics;
import jobmanager.JobConfiguration;

public class JobConfiguratorTest implements TestModule {

	@Override
	public void run(TestStatistics statistics) {
		read_configuration_test(statistics);
	}

	public void read_configuration_test(TestStatistics stats)
	{
		// get sample input from classpath
		URL sample_input_url = this.getClass().getClassLoader().getResource("sample_input.json");
		
		// get the url of the input file
		String sample_input_path = "";
		
		if (sample_input_url != null)
		{
			sample_input_path = sample_input_url.getPath();
		}
		
		// run read configuration
		JobConfiguration the_configuration = null;
		try {
			the_configuration = new JobConfiguration(sample_input_path);
		} catch (Exception e)
		{
			e.printStackTrace();
			stats.recordFail(e.getMessage());
		}
		
		stats.recordPass();
		
		// check information
		assertStringContains(the_configuration.getAdditional_config(),
				"scripts/auto-marker/pintos.conf");
		assertStringContains(the_configuration.getInput_directory(),
				"4b8ea98f6a8f97b98c97a8a6");
		assertStringContains(the_configuration.getOutput_directory(),
				"/tmp/");
		assertStringContains(the_configuration.getType(),
				"haskell");
		assertTrue(the_configuration.getServices() != null);
		assertEqualsObjects(the_configuration.getServices().size(), 2);
	}

}