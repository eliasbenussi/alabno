package alabno.testsuite;

public class TestUtils {

	public static TestStatistics stats = null;
	
	public static void assertTrue(boolean b)
	{
		if (b)
		{
			stats.recordPass();
		} else
		{
			stats.recordFail("Assertion failure");
		}
	}
	
}
