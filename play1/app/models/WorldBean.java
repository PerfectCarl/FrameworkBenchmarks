package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.jpa.GenericModel;

/**
 * use a generic model as we want to explicitly define the id
 * 
 * @author tom
 * 
 */
@Entity
public class WorldBean extends GenericModel {

	public WorldBean(long i, long number) {
		id = i;
		randomNumber = number;
	}

	public WorldBean() {
	}

	@Id
	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long value) {
		this.id = value;
	}

	private Long randomNumber;

	public Long getRandomNumber() {
		return this.randomNumber;
	}

	public void setRandomNumber(Long value) {
		this.randomNumber = value;
	}
}