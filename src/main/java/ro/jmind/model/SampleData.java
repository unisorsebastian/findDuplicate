package ro.jmind.model;

public class SampleData {
	//private byte [] data;
	private long timeTook;
	private SampleDataMethod sampleDataMethod;
	private String hash;
	
	public SampleData(long timeTook, SampleDataMethod sampleDataMethod,String hash) {
		//this.data = data;
		this.timeTook = timeTook;
		this.hash = hash;
		this.sampleDataMethod=sampleDataMethod; 
	}
	
	//	public byte[] getData() {
//		return data;
//	}
	public long getTimeTook() {
		return timeTook;
	}
	public SampleDataMethod getSampleDataMethod() {
		return sampleDataMethod;
	}
	public String getHash() {
		return hash;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleData other = (SampleData) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "SampleData[timeTook=" + timeTook + ", "+sampleDataMethod+"]";
	}
	

}
