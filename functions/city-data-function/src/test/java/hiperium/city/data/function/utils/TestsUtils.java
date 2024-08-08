package hiperium.city.data.function.utils;

import hiperium.city.data.function.dto.CityDataRequest;
import hiperium.city.data.function.entities.City;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

import java.time.Duration;
import java.util.Map;

public final class TestsUtils {

    private TestsUtils() {
    }

    public static void waitForDynamoDbToBeReady(final DynamoDbClient dynamoDbClient) {
        Awaitility.await()
            .atMost(Duration.ofSeconds(30))         // maximum wait time
            .pollInterval(Duration.ofSeconds(3))    // check every 3 seconds
            .until(() -> {
                DescribeTableRequest request = DescribeTableRequest.builder()
                    .tableName(City.TABLE_NAME)
                    .build();
                try {
                    TableStatus tableStatus = dynamoDbClient.describeTable(request).table().tableStatus();
                    return TableStatus.ACTIVE.equals(tableStatus);
                } catch (ResourceNotFoundException e) {
                    return false;
                }
            });
    }

    public static Message<CityDataRequest> createMessage(CityDataRequest cityDataRequest) {
        return new Message<>() {
            @NonNull
            @Override
            public CityDataRequest getPayload() {
                return cityDataRequest;
            }

            @NonNull
            @Override
            public MessageHeaders getHeaders() {
                return new MessageHeaders(Map.of());
            }
        };
    }
}
