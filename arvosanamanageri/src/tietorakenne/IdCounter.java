package tietorakenne;

public class IdCounter {

	// Seuraava annettava id-numero.
	private int id = 1;
	
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public void increment() {
		this.id++;
	}
	
	public int popId() {
		int currentId = getId();
		increment();
		return currentId;
	}
	
}
