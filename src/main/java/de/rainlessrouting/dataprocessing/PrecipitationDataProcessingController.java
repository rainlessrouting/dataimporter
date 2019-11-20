package de.rainlessrouting.dataprocessing;

import java.util.Date;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import de.rainlessrouting.dataprocessing.model.UpdateMessage;

@Controller
public class PrecipitationDataProcessingController {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(PrecipitationDataProcessingApplication.class);

	private Date lastPrecipitationUpdate;

	@GetMapping("/PrecipitationDataUpdate")
    public @ResponseBody UpdateMessage  getPrecipitationDataUpdate() {
        
		UpdateMessage message = new UpdateMessage();
		
		String uuid = UUID.randomUUID().toString();
		message.setId(uuid);
		
	    if(this.lastPrecipitationUpdate != null) {
	    		message.setLastUpdateAt(lastPrecipitationUpdate.toString());
	    } else {
	       	//message.setLastUpdateAt("not available");
	       	message.setResponseBody("There was no incomming update registered until now.");
	       	
	    }
	    	
	    return message;
    }
	
	@PostMapping("/PrecipitationDataUpdate")
    public @ResponseBody String postPrecipitationDataUpdate() {
		this.lastPrecipitationUpdate = new Date();
        
    		return "Was succesfull posted.";
    }
}
