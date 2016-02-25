package me.exrates.model;

public class Currency {

	private int id;
	private String name;
	private String description;

	public Currency() {
		
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Currency currency = (Currency) o;

		if (id != currency.id) return false;
		if (name != null ? !name.equals(currency.name) : currency.name != null) return false;
		return description != null ? description.equals(currency.description) : currency.description == null;

	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Currency{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
