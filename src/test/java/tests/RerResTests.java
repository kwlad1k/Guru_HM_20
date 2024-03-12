package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RerResTests extends TestBaseAPI{

    @Test
    void containUserDataWithSchemaTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .when()
                .get("/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-id-response-schema.json"))
                .extract().response();

        assertThat(response.path("data.id"), is(2));
        assertThat(response.path("data.email"), is("janet.weaver@reqres.in"));
        assertThat(response.path("data.first_name"), is("Janet"));
        assertThat(response.path("data.last_name"), is("Weaver"));
        assertThat(response.path("data.avatar"), is("https://reqres.in/img/faces/2-image.jpg"));
    }

    @Test
    void notFoundSingleUserTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .when()
                .get("/users/23")
                .then()
                .log().status()
                .log().body()
                .statusCode(404)
                .extract().response();

        assertThat(response.getBody().asString(), is(equalTo("{}")));
    }

    @Test
    void createUserTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"name\": \"morpheus\", \"job\": \"leader\" }")
                .when()
                .post("/api/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/create-user-schema.json"))
                .extract().response();

        assertThat(response.path("name"), is("morpheus"));
        assertThat(response.path("job"), is("leader"));

    }

    @Test
    void updateUserTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"name\": \"morpheus\", \"job\": \"zion resident\" }")
                .when()
                .put("/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/update-user-schema.json"))
                .extract().response();

        assertThat(response.path("name"), is("morpheus"));
        assertThat(response.path("job"), is("zion resident"));

    }

    @Test
    void deleteUserTest() {
        given().
                log().uri()
                .log().method()
                .when()
                .delete("/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(204)
                .extract().response();
    }

    @Test
    void successfulRegisterUserTest() {
        Response response = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .body("{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }")
                .when()
                .post("/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/successful-register-user-schema.json"))
                .extract().response();

        assertThat(response.path("email"), is("eve.holt@reqres.in"));
        assertThat(response.path("password"), is("pistol"));
    }
}
