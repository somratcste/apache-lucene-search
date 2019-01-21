import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class Indexer {
	public static IndexWriter indexWriter;
    private static LinkedList<ItemInfo> itemInfoLinkedList = new LinkedList<>();
	public static void main(String args[]) throws Exception {
		queryToGetItemInfo(DbManager.getConnection(true));
		String usage = "java Indexer";
		rebuildIndexes("indexes");
	}

    public static void insertDoc(IndexWriter i, String id, String name, String categoryName, String currentPrice, String description) {
        Document doc = new Document();
        doc.add(new TextField("item_id", id, Store.YES));
        doc.add(new TextField("item_name", name, Store.YES));
        doc.add(new TextField("category_name", categoryName, Store.YES));
//        doc.add(new TextField("current_price", currentPrice, Store.YES));
        doc.add(new TextField("item_description", description, Store.YES));

        try {
            i.addDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static void queryToGetItemInfo(Connection connection) throws Exception {
        Statement statement = null;
        String query = "SELECT distinct item.item_id, item.item_name, has_category.category_name, auction.current_price, item.description\n" +
                "FROM item\n" +
                "INNER JOIN auction ON item.item_id = auction.item_id\n" +
                "INNER JOIN has_category ON item.item_id = has_category.item_id;";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String id = rs.getString("item.item_id");
                String name = rs.getString("item.item_name");
                String categoryName = rs.getString("has_category.category_name");
//                String currentPrice = rs.getString("auction.current_price");
                String description = rs.getString("item.description");
                itemInfoLinkedList.add(new ItemInfo(id, name, categoryName , description));
            }

        } catch (SQLException e) {
            e.getNextException();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
	}

    public static void rebuildIndexes(String indexPath) {
        try {
            Path path = Paths.get(indexPath);
            System.out.println("Indexing to directory '" + indexPath + "'...\n");
            Directory directory = FSDirectory.open(path);
            SimpleAnalyzer analyzer = new SimpleAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter i = new IndexWriter(directory, indexWriterConfig);
            i.deleteAll();
            for (ItemInfo itemInfo : itemInfoLinkedList) {
                insertDoc(i,
                        itemInfo.getId(),
                        itemInfo.getName(),
                        itemInfo.getCategoryName(),
                        itemInfo.getCurrentPrice(),
                        itemInfo.getDescription());
            }
            i.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
