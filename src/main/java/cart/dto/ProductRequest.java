package cart.dto;

public class ProductRequest {

    private String name;
    private int price;
    private String imageUrl;
    private int stock;

    public ProductRequest() {
    }

    public ProductRequest(final String name, final int price, final String imageUrl, final int stock) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStock() {
        return stock;
    }
}
