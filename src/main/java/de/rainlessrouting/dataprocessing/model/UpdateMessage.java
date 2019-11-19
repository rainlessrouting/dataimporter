package de.rainlessrouting.dataprocessing.model;

import javax.validation.constraints.NotNull;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UpdateMessage {
	
	String id;
	
	@JsonProperty("last_update_at")
	String lastUpdateAt;
	
	@JsonProperty("response_body")
	String responseBody;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLastUpdateAt() {
		return lastUpdateAt;
	}

	public void setLastUpdateAt(String lastUpdateAt) {
		this.lastUpdateAt = lastUpdateAt;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
	

	
}
