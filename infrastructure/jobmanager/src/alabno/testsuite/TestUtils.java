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
	
	public static void assertStringContains(String str, String expected)
	{
		if (str == null)
		{
			stats.recordFail("Actual string is null. Expected for it to contain: " + expected);
		} else if (str.contains(expected))
		{
			stats.recordPass();
		} else
		{
			stats.recordFail("Expected contains: " + expected + ". Actual: " + str);
		}
	}
	
	public static void assertEqualsObjects(Object a, Object b)
	{
		if (a == null && b == null)
		{
			stats.recordPass();
		} else if (a.equals(b))
		{
			stats.recordPass();
		} else
		{
			stats.recordFail("Expected equals, actual:\n" + a + "\n" + b + "\n");
		}
	}

}
