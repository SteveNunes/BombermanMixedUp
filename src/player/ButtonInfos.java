package player;

public class ButtonInfos {
	
	private int id;
	private String name;
	
	ButtonInfos(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ButtonInfos [id=" + id + ", name=" + name + "]";
	}

}
