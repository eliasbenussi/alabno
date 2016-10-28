package jobmanager.tests;

import static alabno.testsuite.TestUtils.*;

import alabno.testsuite.TestModule;
import alabno.testsuite.TestStatistics;
import jobmanager.MicroServiceInfo;

public class MicroServiceInfoTest implements TestModule {

	@Override
	public void run(TestStatistics statistics) {
		constructor_test(statistics);
	}

	private void constructor_test(TestStatistics statistics) {
		MicroServiceInfo the_info = new MicroServiceInfo("haskell", "java -jar proj/../haskellanalyzer/Main");
		
		assertEqualsObjects(the_info.getName(), "haskell");
		
		assertEqualsObjects(the_info.getLocation(), "java -jar proj/../haskellanalyzer/Main");
	}

}
