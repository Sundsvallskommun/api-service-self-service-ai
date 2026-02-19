package se.sundsvall.selfserviceai.integration.agreement;

import generated.se.sundsvall.agreement.Agreement;
import generated.se.sundsvall.agreement.AgreementParty;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.zalando.problem.ThrowableProblem;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.NOT_FOUND;

@Component
public class AgreementIntegration {

	private final AgreementClient agreementClient;

	AgreementIntegration(AgreementClient agreementClient) {
		this.agreementClient = agreementClient;
	}

	/**
	 * Fetch active agreements for customer matching sent in parameters.
	 *
	 * @param  municipalityId the municipality to filter on
	 * @param  partyId        the party id to filter on
	 * @return                all active customer agreements matching the provided parameters
	 */
	public List<Agreement> getAgreements(String municipalityId, String partyId) {
		try {
			return ofNullable(agreementClient.getAgreements(municipalityId, partyId, true).getAgreementParties()).orElse(emptyList())
				.stream()
				.map(AgreementParty::getAgreements)
				.flatMap(List::stream)
				.toList();
		} catch (final ThrowableProblem e) {
			if (Objects.equals(NOT_FOUND, e.getStatus())) {
				return emptyList();
			}
			throw e;
		}
	}
}
