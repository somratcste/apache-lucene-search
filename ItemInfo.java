public class ItemInfo {
    private String id;
    private String name;
    private String categoryName;
    private String currentPrice;
    private String description;

    public ItemInfo(String id, String name, String categoryName, String currentPrice, String description) {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.currentPrice = currentPrice;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public String getDescription() {
        return description;
    }
}
