package cart.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import cart.dto.ProductRequest;
import cart.dto.ProductStockResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class ProductIntegrationTest extends IntegrationTest {

    @Test
    void getProducts() {
        final var result = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/products")
                .then()
                .extract();

        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void createProduct() {
        final var product = new ProductRequest("치킨", 10_000, "http://example.com/chicken.jpg", 10);

        final var response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(product)
                .when()
                .post("/products")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void getCreatedProduct() {
        final var product = new ProductRequest("피자", 15_000, "http://example.com/pizza.jpg", 10);

        // create product
        final var location =
                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(product)
                        .when()
                        .post("/products")
                        .then()
                        .statusCode(HttpStatus.CREATED.value())
                        .extract().header("Location");

        // get product
        final var responseProduct = given().log().all()
                .when()
                .get(location)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getObject(".", ProductStockResponse.class);

        assertThat(responseProduct.getId()).isNotNull();
        assertThat(responseProduct.getName()).isEqualTo("피자");
        assertThat(responseProduct.getPrice()).isEqualTo(15_000);
        assertThat(responseProduct.getStock()).isEqualTo(10);
    }
}
