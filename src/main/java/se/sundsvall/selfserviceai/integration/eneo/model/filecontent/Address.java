package se.sundsvall.selfserviceai.integration.eneo.model.filecontent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(setterPrefix = "with")
@Schema(description = "Model for address data sent to Eneo")
public class Address {

	private String careOf;

	private String street;

	private String postalCode;

	private String city;
}
