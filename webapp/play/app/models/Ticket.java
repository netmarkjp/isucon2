package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Ticket extends Model {

	@Id
	public Integer id;

	public String name;

	public Integer artistId;

	public static Finder<Integer, Ticket> find = new Finder<Integer, Ticket>(
			Integer.class, Ticket.class);
}
