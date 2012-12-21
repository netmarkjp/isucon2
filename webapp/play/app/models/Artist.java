package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Artist extends Model {
	@Id
	public Integer id;

	public String name;

	public static Finder<Integer, Artist> find = new Finder<Integer, Artist>(
			Integer.class, Artist.class);

}
