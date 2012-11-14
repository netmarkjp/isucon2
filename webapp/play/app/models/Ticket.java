package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Ticket extends Model {

	@Id
	public Long id;

	public String name;

	public Long artistId;

	public static Finder<Long, Ticket> find = new Finder<Long, Ticket>(
			Long.class, Ticket.class);
}
