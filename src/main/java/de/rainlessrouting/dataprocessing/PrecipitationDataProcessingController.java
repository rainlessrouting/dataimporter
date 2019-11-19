package de.rainlessrouting.dataprocessing;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import de.rainlessrouting.dataprocessing.model.UpdateMessage;

@Controller
public class PrecipitationDataProcessingController {
     
	
	
    private Date lastPrecipiationUpdate; 
	
	
	
	@GetMapping("/PrecipitationDataUpdate")
    public @ResponseBody UpdateMessage  getPrecipitationDataUpdate() {
        
		UpdateMessage message = new UpdateMessage();
		
		String uuid = UUID.randomUUID().toString();
		message.setId(uuid);
		
	    if(this.lastPrecipiationUpdate != null) {
	    		message.setLastUpdateAt(lastPrecipiationUpdate.toString());
	    } else {
	       	//message.setLastUpdateAt("not available");
	       	message.setResponseBody("There was no incomming update registered until now.");
	       	
	    }
	    	
	    return message;
    }
	
	@PostMapping("/PrecipitationDataUpdate")
    public @ResponseBody String postPrecipitationDataUpdate() {
		this.lastPrecipiationUpdate = new Date();
        
    		return "Was succesfull posted.";
    }


}
