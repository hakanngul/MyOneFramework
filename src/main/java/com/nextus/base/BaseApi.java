package com.nextus.api;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.nextus.config.FrameworkConfig;
import org.aeonbits.owner.ConfigFactory;
import org.apache.http.params.CoreConnectionPNames;

import java.util.Map;

public abstract class BaseApi {

    protected FrameworkConfig config;

    // Constructor - Configuration yüklemesi
    public BaseApi() {
        this.config = ConfigFactory.create(FrameworkConfig.class);
        RestAssured.baseURI = config.apiBaseUrl();
    }

    // Merkezi HTTP isteği gönderme fonksiyonu
    protected Response sendRequest(Method method, String endpoint, Map<String, Object> body) {
        RequestSpecification requestSpec = requestSpecification();
        if (body != null && (method == Method.POST || method == Method.PUT || method == Method.PATCH)) {
            requestSpec.body(body);
        }

        return requestSpec.request(method, endpoint).then().extract().response();
    }

    // Merkezi GET isteği
    protected Response get(String endpoint) {
        return sendRequest(Method.GET, endpoint, null);
    }

    // Merkezi POST isteği
    protected Response post(String endpoint, Map<String, Object> body) {
        return sendRequest(Method.POST, endpoint, body);
    }

    // Merkezi PUT isteği
    protected Response put(String endpoint, Map<String, Object> body) {
        return sendRequest(Method.PUT, endpoint, body);
    }

    // Merkezi DELETE isteği
    protected Response delete(String endpoint) {
        return sendRequest(Method.DELETE, endpoint, null);
    }

    // Merkezi PATCH isteği
    protected Response patch(String endpoint, Map<String, Object> body) {
        return sendRequest(Method.PATCH, endpoint, body);
    }

    // Ortak request specification yapılandırması
    protected RequestSpecification requestSpecification() {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    // Authorization Token Ekleyici
    protected RequestSpecification requestWithAuth(String token) {
        return requestSpecification()
                .header("Authorization", "Bearer " + token);
    }

    // Özel Header Ekleme
    protected RequestSpecification requestWithHeaders(Map<String, String> headers) {
        RequestSpecification spec = requestSpecification();
        headers.forEach(spec::header);
        return spec;
    }

    // Genel API Response doğrulama
    protected void validateResponse(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    // JSON Path üzerinden değer çekme
    protected <T> T getJsonPathValue(Response response, String jsonPath, Class<T> type) {
        return response.jsonPath().getObject(jsonPath, type);
    }

    // Özel Timeout Ayarları
    protected void setTimeouts(int connectionTimeout, int socketTimeout) {
        RestAssured.config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout)
                .setParam(CoreConnectionPNames.SO_TIMEOUT, socketTimeout));
    }

}