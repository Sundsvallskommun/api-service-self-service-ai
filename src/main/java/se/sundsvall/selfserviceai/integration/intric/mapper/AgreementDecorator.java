package se.sundsvall.selfserviceai.integration.intric.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.selfserviceai.integration.intric.util.ConversionUtil.toBoolean;

import generated.se.sundsvall.agreement.Agreement;
import java.util.List;
import java.util.Objects;
import se.sundsvall.selfserviceai.integration.intric.model.filecontent.Facility;

public class AgreementDecorator {
	private AgreementDecorator() {}

	public static void addAgreements(List<Facility> facilities, List<Agreement> agreements) {
		ofNullable(agreements).orElse(emptyList())
			.stream()
			.filter(Objects::nonNull)
			.forEach(agreement -> attachToFacility(facilities, agreement));
	}

	private static void attachToFacility(List<Facility> facilities, Agreement agreement) {
		ofNullable(facilities).orElse(emptyList())
			.stream()
			.filter(f -> f.getFacilityId().equals(agreement.getFacilityId()))
			.findFirst()
			.ifPresent(f -> f.getAgreements().add(toAgreement(agreement)));
	}

	private static se.sundsvall.selfserviceai.integration.intric.model.filecontent.Agreement toAgreement(Agreement agreement) {
		return se.sundsvall.selfserviceai.integration.intric.model.filecontent.Agreement.builder()
			.withAgreementId(agreement.getAgreementId())
			.withBound(toBoolean(agreement.getBinding()))
			.withBindingRule(agreement.getBindingRule())
			.withCategory(agreement.getCategory().name())
			.withDescription(agreement.getDescription())
			.withFromDate(agreement.getFromDate())
			.build();
	}

}
