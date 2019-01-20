import java.lang.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.nio.file.Paths;
import java.sql.*;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import java.nio.file.Path;

public class Searcher {
    public Searcher() {}

	private static SimpleAnalyzer analyzer = new SimpleAnalyzer();
	private static HashMap<String, Double> itemAndDistance = new HashMap<>();
	private static String searchQuery;
	private static String longitude;
	private static String latitude;
	private static String width;
	private static boolean hasLatitudeAndLongitude = false;
	private static Set<OneArgumentSearch> oneArgumentSearchSet = new HashSet<>();
	private static Set<ThreeArgumentSearch> threeArgumentSearchSet = new HashSet<>();

	public static void main(String[] args) throws Exception {
		String usage = "java Searcher";
		searchQuery = args[0];
		if (args.length > 2) {
			longitude = args[2];
			latitude = args[4];
			width = args[6];
			hasLatitudeAndLongitude = true;
			getDistanceFromGeoTable(DbManager.getConnection(true));
		}
		searchIndex();
	}

	public static void getDistanceFromGeoTable(Connection connection) throws SQLException {
		Statement statement = null;
		String query = "SELECT \n" +
				"    itemID,\n" +
				"    (6371 * ACOS(COS(RADIANS(" + latitude + ")) * COS(RADIANS(X(location))) \n" +
				"    * COS(RADIANS(Y(location)) - RADIANS(" + longitude + ")) + SIN(RADIANS(" + latitude + "))\n" +
				"    * SIN(RADIANS(X(location))))) AS distance\n" +
				"FROM geo\n" +
				"WHERE MBRContains\n" +
				"    (\n" +
				"    LineString\n" +
				"        (\n" +
				"        Point (\n" +
				"            " + latitude + " + " + Double.parseDouble(width) * 1000 + " / (111.1 * COS(RADIANS(" + longitude + "))),\n" +
				"            " + longitude + " + " + Double.parseDouble(width) * 1000 + " / 111.1\n" +
				"        ),\n" +
				"        Point (\n" +
				"            " + latitude + " - " + Double.parseDouble(width) * 1000 + "/ (111.1 * COS(RADIANS(" + longitude + "))),\n" +
				"            " + longitude + "- " + Double.parseDouble(width) * 1000 + "/ 111.1\n" +
				"        )\n" +
				"    ),\n" +
				"    location\n" +
				"    )\n" +
				"HAVING distance < " + width + "\n" +
				"ORDER By distance;";
		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String geo_itemID = rs.getString("geo.itemID");
				String geo_distance = rs.getString("distance");
				itemAndDistance.put(geo_itemID, Double.parseDouble(geo_distance));
			}
		} catch (SQLException e) {
			e.getMessage();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static void searchIndex() throws Exception {
		Path path = Paths.get("indexes");
		Directory directory = FSDirectory.open(path);
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);

		// item_name query
		QueryParser item_nameQP = new QueryParser("item_name", analyzer);
		Query item_nameQuery = item_nameQP.parse(searchQuery);

		// has_category_category_name query
		QueryParser has_category_category_name = new QueryParser("category_name", analyzer);
		Query has_categoryQuery = has_category_category_name.parse(searchQuery);

		// description query
		QueryParser descriptionQP = new QueryParser("item_description", analyzer);
		Query descriptionQuery = descriptionQP.parse(searchQuery);

		BooleanQuery query = new BooleanQuery.Builder().
				add(item_nameQuery, BooleanClause.Occur.SHOULD).
				add(has_categoryQuery, BooleanClause.Occur.SHOULD).
				add(descriptionQuery, BooleanClause.Occur.SHOULD).
				build();


		TopDocs docs = searcher.search(query, 10000);


		// for three decimal places
		DecimalFormat df = new DecimalFormat("###.###");
		df.setRoundingMode(RoundingMode.DOWN);


		int i = 0;
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			Document d = searcher.doc(scoreDoc.doc);
			if (hasLatitudeAndLongitude) {
				if (itemAndDistance.containsKey(d.get("item_id"))) {
					threeArgumentSearchSet.add(
							new ThreeArgumentSearch(
									d.get("item_id"),
									d.get("item_name"),
									String.valueOf(scoreDoc.score),
									df.format(itemAndDistance.get(d.get("item_id"))),
									d.get("current_price")
							));
					i++;
				}
			} else {
				oneArgumentSearchSet.add(new OneArgumentSearch(
						d.get("item_id"),
						d.get("item_name"),
						String.valueOf(scoreDoc.score),
						d.get("current_price")
				));
				i++;
			}
		}
		System.out.println("Running search(" + searchQuery + ")");
		if (hasLatitudeAndLongitude) {
			System.out.println("totalHits " + threeArgumentSearchSet.size());
			// comparator to compare first score, and then distance, and at last price
			ArrayList<ThreeArgumentSearch> threeArgumentSearchArrayList = new ArrayList<>(threeArgumentSearchSet);
			Collections.sort(threeArgumentSearchArrayList, new Comparator<ThreeArgumentSearch>() {
				@Override
				public int compare(ThreeArgumentSearch o1, ThreeArgumentSearch o2) {
					int c = o2.getScore().compareTo(o1.getScore());
					if (c == 0) {
						c = o1.getDist().compareTo(o2.getDist());
					}
					if (c == 0) {
						c = o1.getPrice().compareTo(o2.getPrice());
					}
					return c;
				}
			});
			for (ThreeArgumentSearch threeArgumentSearch : threeArgumentSearchArrayList) {
				System.out.println(threeArgumentSearch);
			}
		} else {
			System.out.println("totalHits " + oneArgumentSearchSet.size());
			// comparator to compare first score, and then price
			ArrayList<OneArgumentSearch> oneArgumentSearchArrayList = new ArrayList<>(oneArgumentSearchSet);
			Collections.sort(oneArgumentSearchArrayList, new Comparator<OneArgumentSearch>() {
				@Override
				public int compare(OneArgumentSearch o1, OneArgumentSearch o2) {
					int c = o2.getScore().compareTo(o1.getScore());
					if (c != 0) {
						return c;
					} else {
						return o1.getPrice().compareTo(o2.getPrice());
					}
				}
			});
			for (OneArgumentSearch oneArgumentSearch : oneArgumentSearchArrayList) {
				System.out.println(oneArgumentSearch);
			}
		}


	}

}
