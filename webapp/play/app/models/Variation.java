package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Variation extends Model {

	@Id
	public Integer id;

	public String name;

	public Integer ticketId;

	public static Finder<Integer, Variation> find = new Finder<Integer, Variation>(
			Integer.class, Variation.class);
}
