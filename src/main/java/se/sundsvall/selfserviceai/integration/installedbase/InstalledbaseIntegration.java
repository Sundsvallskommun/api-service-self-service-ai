package se.sundsvall.selfserviceai.integration.installedbase;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

@Component
public class InstalledbaseIntegration {
	private static final String ERROR_MULTIPLE_MATCHES = "Installed base response can not be interpreted as it contains more than one match (size is %s)";

	private final InstalledbaseClient installedbaseClient;

	InstalledbaseIntegration(InstalledbaseClient installedbaseClient) {
		this.installedbaseClient = installedbaseClient;
	}

	public InstalledBaseCustomer getInstalledbase(String municipalityId, String partyId, String customerEngagementOrgId) {
		final var response = ofNullable(installedbaseClient.getInstalledbase(municipalityId, customerEngagementOrgId, partyId).getInstalledBaseCustomers()).orElse(emptyList());

		if (response.size() > 1) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, ERROR_MULTIPLE_MATCHES.formatted(response.size()));
		}

		return response.isEmpty() ? null : response.getFirst();
	}
}
