package com.linktic.catalogo.catalogo_service.service;

import com.linktic.catalogo.catalogo_service.model.Product;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final DynamoDbClient dynamoDbClient;

    public ProductService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public List<Product> getAllProducts() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Products")
                .build();

        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

        return scanResponse.items().stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
    }

    public Product getProductById(String productId) {
        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("ProductID", AttributeValue.builder().s(productId).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName("Products")
                .key(keyToGet)
                .build();

        Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();

        return mapToProduct(returnedItem);
    }

    // Método para mapear los atributos de DynamoDB a un objeto Product
    private Product mapToProduct(Map<String, AttributeValue> item) {
        if (item == null || item.isEmpty()) {
            return null;
        }

        // Extraer valores del mapa y crear un nuevo objeto Product
        String id = item.get("ProductID").s();
        String name = item.get("ProductName").s();
        double price = Double.parseDouble(item.get("ProductPrice").n());
        String description = item.get("Description").s();

        return new Product(id, name, price, description);
    }
}
