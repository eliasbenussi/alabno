package jobmanager;

public class Main {

	public static void main(String[] args) {
		if (args.length == 0)
		{
			System.out.println("JobManager requires 1 argument: input configuration");
			System.exit(1);
		}
		
		try {
			JobConfiguration jobConfiguration = new JobConfiguration(args[0]);
			
			// Make the SingleJobConfigs. Each of them is able to
			// generate a JSON and start the corresponding microservice
			jobConfiguration.runAllJobs();
			
		} catch (Exception e) {
			System.out.println("Unexpected error!");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
