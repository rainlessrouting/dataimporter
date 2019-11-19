package de.rainlessrouting.dataprocessing.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
*
* @author Julius Wulk
*
*/

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PrecipitationDataUpdate {
	
	@NotNull @Getter @Setter
	String id;

}
