package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Artist extends Model {
	@Id
	public Long id;

	public String name;

	public static Finder<Long, Artist> find = new Finder<Long, Artist>(
			Long.class, Artist.class);

}
