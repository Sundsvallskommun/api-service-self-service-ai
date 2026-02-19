package se.sundsvall.selfserviceai.integration.installedbase;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseResponse;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Component
public class InstalledbaseIntegration {
	private static final String ERROR_MULTIPLE_MATCHES = "Installed base response can not be interpreted as it contains more than one match (size is %s)";

	private final InstalledbaseClient installedbaseClient;

	InstalledbaseIntegration(InstalledbaseClient installedbaseClient) {
		this.installedbaseClient = installedbaseClient;
	}

	/**
	 * The method retrieves all installed bases that the customer matching the provided party id has registered for each
	 * provided customer engagement org id in the specified municipality
	 *
	 * @param  municipalityId           id for the municipality to filter response on
	 * @param  partyId                  party id for customer to fetch installed bases for
	 * @param  customerEngagementOrgIds list of counterparts to filter installed bases on
	 * @return                          a map where key is the customerEngagementOrgId and value is the response from
	 *                                  underlying service
	 */
	public Map<String, InstalledBaseCustomer> getInstalledbases(String municipalityId, String partyId, Set<String> customerEngagementOrgIds) {
		return ofNullable(customerEngagementOrgIds).orElse(emptySet())
			.stream()
			.map(counterPart -> getInstalledbase(municipalityId, partyId, counterPart))
			.filter(Objects::nonNull)
			.collect(toMap(Entry::getKey, Entry::getValue));
	}

	private Entry<String, InstalledBaseCustomer> getInstalledbase(String municipalityId, String partyId, String customerEngagementOrgId) {
		final var response = ofNullable(installedbaseClient.getInstalledbase(municipalityId, customerEngagementOrgId, partyId))
			.map(InstalledBaseResponse::getInstalledBaseCustomers)
			.orElse(emptyList());

		if (response.size() > 1) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, ERROR_MULTIPLE_MATCHES.formatted(response.size()));
		}

		return response.isEmpty() ? null : Map.entry(customerEngagementOrgId, response.getFirst());
	}
}
