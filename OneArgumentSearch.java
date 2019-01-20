import java.util.Objects;

public class OneArgumentSearch {
    private String itemID;
    private String itemName;
    private String score;
    private String price;

    public OneArgumentSearch(String itemID, String itemName, String score, String price) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.score = score;
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

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return itemID +
                ", " + itemName + '\'' +
                ", score: " + score +
                ", price: " + price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneArgumentSearch that = (OneArgumentSearch) o;
        return itemID.equals(that.itemID) &&
                itemName.equals(that.itemName) &&
                price.equals(that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID, itemName, price);
    }
}
