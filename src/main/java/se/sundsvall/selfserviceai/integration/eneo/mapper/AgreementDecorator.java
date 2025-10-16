package se.sundsvall.selfserviceai.integration.eneo.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.selfserviceai.integration.eneo.util.ConversionUtil.toBoolean;

import generated.se.sundsvall.agreement.Agreement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Facility;

public class AgreementDecorator {

	private AgreementDecorator() {}

	public static void addAgreements(final List<Facility> facilities, final List<Agreement> agreements) {
		ofNullable(agreements).orElse(emptyList())
			.stream()
			.filter(Objects::nonNull)
			.forEach(agreement -> attachToFacility(facilities, agreement));
	}

	private static void attachToFacility(final List<Facility> facilities, final Agreement agreement) {
		ofNullable(facilities).orElse(emptyList())
			.stream()
			.filter(f -> f.getFacilityId().equals(agreement.getFacilityId()))
			.findFirst()
			.ifPresent(f -> f.getAgreements().add(toAgreement(agreement)));
	}

	private static se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Agreement toAgreement(final Agreement agreement) {
		return se.sundsvall.selfserviceai.integration.eneo.model.filecontent.Agreement.builder()
			.withAgreementId(agreement.getAgreementId())
			.withBound(toBoolean(agreement.getBinding()))
			.withBindingRule(agreement.getBindingRule())
			.withCategory(Optional.ofNullable(agreement.getCategory()).map(Object::toString).orElse(null))
			.withDescription(agreement.getDescription())
			.withFromDate(agreement.getFromDate())
			.build();
	}

}
