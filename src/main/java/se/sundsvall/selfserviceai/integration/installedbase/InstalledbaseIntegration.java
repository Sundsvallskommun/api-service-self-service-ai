package se.sundsvall.selfserviceai.integration.installedbase;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.installedbase.InstalledBaseCustomer;
import generated.se.sundsvall.installedbase.InstalledBaseItem;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InstalledbaseIntegration {
	private final InstalledbaseClient installedbaseClient;

	InstalledbaseIntegration(InstalledbaseClient installedbaseClient) {
		this.installedbaseClient = installedbaseClient;
	}

	public List<InstalledBaseItem> getInstalledbase(String municipalityId, String partyId, String customerEngagementOrgId) {
		return ofNullable(installedbaseClient.getInstalledbase(municipalityId, customerEngagementOrgId, partyId).getInstalledBaseCustomers()).orElse(emptyList())
			.stream()
			.map(InstalledBaseCustomer::getItems)
			.flatMap(List::stream)
			.toList();
	}
}
