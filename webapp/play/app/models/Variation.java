package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Variation extends Model {

	@Id
	public Long id;

	public String name;

	public Long ticketId;

	public static Finder<Long, Variation> find = new Finder<Long, Variation>(
			Long.class, Variation.class);
}
