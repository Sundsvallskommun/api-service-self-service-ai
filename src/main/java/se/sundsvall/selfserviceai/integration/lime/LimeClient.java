package se.sundsvall.selfserviceai.integration.lime;

import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest;
import generated.se.sundsvall.lime.ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.selfserviceai.integration.lime.configuration.LimeConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.selfserviceai.integration.lime.configuration.LimeConfiguration.CLIENT_ID;

@CircuitBreaker(name = CLIENT_ID)
@FeignClient(name = CLIENT_ID, url = "${integration.lime.url}", configuration = LimeConfiguration.class)
public interface LimeClient {

	@PostMapping(path = "/api/chathistorik", consumes = APPLICATION_JSON_VALUE)
	Void saveChatHistory(
		@RequestBody final ServanetItOpsApiGatewayAdapterHttpContractsModelsRequestsChathistorikSkapaChathistorikRequest request);

	@GetMapping(path = "/api/chathistorik/{sessionId}", produces = APPLICATION_JSON_VALUE)
	ServanetItOpsApiGatewayAdapterHttpContractsModelsResponsesChathistorikChathistorikResponse getChatHistory(
		@RequestParam("sessionId") final String sessionId);

}
