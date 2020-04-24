package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.JsonResponse;
import com.prs.business.Request;
import com.prs.db.RequestRepository;


@RestController
@RequestMapping("/requests")
public class RequestController {

	@Autowired
	private RequestRepository requestRepo; 

	@GetMapping("/")
	public JsonResponse list() {
		JsonResponse jr = null;
		List<Request> requests = requestRepo.findAll();
		if (requests.size()>0) {
			jr = JsonResponse.getInstance(requests);			
		}
		else {
			jr = JsonResponse.getErrorInstance("No requests found.");
		}
		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		JsonResponse jr = null;
		Optional<Request> request = requestRepo.findById(id); 
		if (request.isPresent()) {
			jr = JsonResponse.getInstance(request.get());
		}
		else {
			jr = JsonResponse.getErrorInstance("No request found for id: "+id);
		}
		return jr;
	}
	
	@PostMapping("/")
	public JsonResponse createRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		try {
			r = requestRepo.save(r);
			jr = JsonResponse.getInstance(r);
		}
		catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getErrorInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		}
		catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error creating request: "+e.getMessage());
			e.printStackTrace();
		}

		
		return jr;
	}
	
	@PutMapping("/")
	public JsonResponse updateRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		try {
			r = requestRepo.save(r);
			jr = JsonResponse.getInstance(r);
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error updating request: "+e.getMessage());
			e.printStackTrace();
		}

		
		return jr;
	}
	
	@DeleteMapping("/{id}")
	public JsonResponse deleteRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			requestRepo.deleteById(id);
			jr = JsonResponse.getInstance("Request ID: "+id+ " deleted successfully");
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error deleting request: "+e.getMessage());
			e.printStackTrace();
		}

		
		return jr;
	}

}
