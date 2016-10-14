package jobmanager;

// Holds information about a single microservice
public class MicroServiceInfo {

	private String name;
	private String location;

	public MicroServiceInfo(String name, String location) {
		this.name = name;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "MicroServiceInfo [name=" + name + ", location=" + location + "]";
	}

}
