package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.db.ebean.Model;

import com.avaje.ebean.annotation.UpdatedTimestamp;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "variation_id",
		"seat_id" }) })
public class Stock extends Model {
	@Id
	public Long id;

	public Long variationId;

	public String seatId;

	public Long orderId;

	@UpdatedTimestamp
	public Date updatedAt;

	public static Finder<Long, Stock> find = new Finder<Long, Stock>(
			Long.class, Stock.class);
}
