package alabno.testsuite;

public class TestStatistics {

	private int passed = 0;
	private int total = 0;
	
	public void recordPass()
	{
		passed += 1;
		total += 1;
	}
	
	public void recordFail(String message)
	{
		total += 1;
		System.out.println();
		new RuntimeException("Test failed: " + message).printStackTrace();
	}
	
	public String getFinalMessage()
	{
		StringBuilder msg = new StringBuilder();
		if (passed < total)
		{
			msg.append("TESTS FAILED\n");
		}
		msg.append("Passed " + passed + "/" + total + " tests");
		return msg.toString();
	}
	
	public int getReturnCode()
	{
		if (passed < total)
		{
			return 1;
		} else
		{
			return 0;
		}
	}
	
}
