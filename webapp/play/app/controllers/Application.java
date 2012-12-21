package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Artist;
import models.OrderRequest;
import models.Stock;
import models.Ticket;
import models.Variation;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;

public class Application extends Controller {

	public static List<HashMap<String, String>> getRecentSolds() {
		List<SqlRow> sqlRows = Ebean
				.createSqlQuery(
						"SELECT stock.seat_id, variation.name AS v_name, ticket.name AS t_name, artist.name AS a_name FROM stock"
								+ " JOIN variation ON stock.variation_id = variation.id"
								+ " JOIN ticket ON variation.ticket_id = ticket.id"
								+ " JOIN artist ON ticket.artist_id = artist.id"
								+ " WHERE order_id IS NOT NULL"
								+ " ORDER BY order_id DESC LIMIT 10")
				.findList();
		List<HashMap<String, String>> recent_solds = new ArrayList<HashMap<String, String>>(
				sqlRows.size());
		for (SqlRow sqlRow : sqlRows) {
			HashMap<String, String> recent_sold = new HashMap<String, String>();
			recent_sold.put("seatId", sqlRow.getString("seat_id"));
			recent_sold.put("variationName", sqlRow.getString("v_name"));
			recent_sold.put("ticketName", sqlRow.getString("t_name"));
			recent_sold.put("artistName", sqlRow.getString("a_name"));
			recent_solds.add(recent_sold);
		}
		return recent_solds;
	}

	public static Result index() {
		return ok(views.html.index.render(Artist.find.all(), getRecentSolds()));
	}

	public static Result artist(Integer artistId) {
		SqlQuery query = Ebean
				.createSqlQuery("SELECT COUNT(*) as cnt FROM variation"
						+ " INNER JOIN stock ON stock.variation_id = variation.id"
						+ " WHERE variation.ticket_id = :ticketId AND stock.order_id IS NULL");
		Artist artist = Artist.find.byId(artistId);
		List<Ticket> tickets = Ticket.find.where()
				.eq("artist_id", artistId.toString()).orderBy("id").findList();
		List<HashMap<String, String>> ticketMaps = new ArrayList<HashMap<String, String>>(
				tickets.size());
		for (Ticket ticket : tickets) {
			HashMap<String, String> ticketMap = new HashMap<String, String>();
			ticketMap.put("ticketId", ticket.id.toString());
			ticketMap.put("ticketName", ticket.name);
			List<SqlRow> count = query.setParameter("ticketId", ticket.id)
					.findList();
			ticketMap.put("count", count.get(0).getInteger("cnt").toString());
			ticketMaps.add(ticketMap);
		}
		return ok(views.html.artist
				.render(artist, ticketMaps, getRecentSolds()));
	}

	public static Result ticket(Integer ticketId) {
		Ticket ticket = Ticket.find.byId(ticketId);
		List<HashMap<String, Object>> variations = new ArrayList<HashMap<String, Object>>();
		for (Variation variation : Variation.find.where()
				.eq("ticket_id", ticketId.toString()).order("id").findList()) {
			HashMap element = new HashMap();
			element.put("variation", variation);

			int vacancy = Stock.find.where()
					.eq("variation_id", Integer.toString((variation.id)))
					.isNull("order_id").findList().size();
			element.put("vacancy", vacancy);

			List<Stock> stocks = Stock.find.where()
					.eq("variation_id", variation.id).orderBy("seat_id")
					.findList();
			element.put("stocks", stocks);
			variations.add(element);
		}

		return ok(views.html.ticket
				.render(ticket, variations, getRecentSolds()));
	}

	public static Result buy() {
		Map<String, String[]> requestBody = request().body().asFormUrlEncoded();

		String variationId = requestBody.containsKey("variation_id") ? requestBody
				.get("variation_id")[0] : "";
		String memberId = requestBody.containsKey("member_id") ? requestBody
				.get("member_id")[0] : "";

		Ebean.beginTransaction();
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.memberId = memberId;
		orderRequest.save();

		Stock stock = Stock.find.where().eq("variation_id", variationId)
				.isNull("order_id").orderBy("RAND()").findList().get(0);
		stock.orderId = orderRequest.id;
		stock.save();

		try {
			Ebean.commitTransaction();
			Ebean.endTransaction();
		} catch (Exception e) {
			Ebean.rollbackTransaction();
			Ebean.endTransaction();
			return ok(views.html.soldout.render());
		}
		String seatId = Stock.find.where()
				.eq("order_id", orderRequest.id.toString()).findList().get(0).seatId;
		return ok(views.html.buy.render(seatId, memberId));

	}

	public static Result adminIndex() {
		return ok(views.html.admin.render());
	}

	public static Result adminIndexPost() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"../config/database/initial_data.sql"));
			String line;
			Ebean.beginTransaction();
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				Ebean.execute(Ebean.createCallableSql(line));
			}
			Ebean.execute(Ebean.createCallableSql("commit;"));
			Ebean.commitTransaction();
		} catch (FileNotFoundException e) {
			Logger.error(e.getMessage());
			Logger.error(e.getStackTrace().toString());
		} catch (IOException e) {
			Logger.error(e.getMessage());
			Logger.error(e.getStackTrace().toString());
		} finally {
			Ebean.endTransaction();
		}
		return found("/admin");
	}

	public static Result adminOrderCSV() {
		response().setContentType("text/csv");
		List<SqlRow> sqlRows = Ebean
				.createSqlQuery(
						"SELECT order_request.*, stock.seat_id, stock.variation_id, stock.updated_at"
								+ " FROM order_request JOIN stock ON order_request.id = stock.order_id"
								+ " ORDER BY order_request.id ASC").findList();
		StringBuffer buffer = new StringBuffer();
		for (SqlRow sqlRow : sqlRows) {
			buffer.append(sqlRow.get("member_id"));
			buffer.append(",");
			buffer.append(sqlRow.get("seat_id"));
			buffer.append(",");
			buffer.append(sqlRow.get("variation_id"));
			buffer.append(",");
			buffer.append(sqlRow.get("updated_at"));
			buffer.append("\n");
		}
		return ok(buffer.toString());
	}

}