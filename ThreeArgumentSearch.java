import java.util.Objects;

public class ThreeArgumentSearch {
    private String itemID;
    private String itemName;
    private String score;
    private String dist;
    private String price;

    public ThreeArgumentSearch(String itemID, String itemName, String score, String dist, String price) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.score = score;
        this.dist = dist;
        this.price = price;
    }

    public String getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getScore() {
        return score;
    }

    public String getDist() {
        return dist;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return itemID +
                ", " + itemName +
                ", score: " + score +
                ", dist: " + dist +
                ", price: " + price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreeArgumentSearch that = (ThreeArgumentSearch) o;
        return itemID.equals(that.itemID) &&
                itemName.equals(that.itemName) &&
                dist.equals(that.dist) &&
                price.equals(that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID, itemName, dist, price);
    }
}
