package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.JsonResponse;
import com.prs.business.LineItem;
import com.prs.db.LineItemRepository;


@RestController
@RequestMapping("/line-items")
public class LineItemController {

	@Autowired
	private LineItemRepository lineItemRepo; 

	@GetMapping("/")
	public JsonResponse list() {
		JsonResponse jr = null;
		List<LineItem> lineItems = lineItemRepo.findAll();
		if (lineItems.size()>0) {
			jr = JsonResponse.getInstance(lineItems);			
		}
		else {
			jr = JsonResponse.getErrorInstance("No line items found.");
		}
		return jr;
	}

	@GetMapping("/{id}")
	public JsonResponse get(@PathVariable int id) {
		JsonResponse jr = null;
		Optional<LineItem> lineItem = lineItemRepo.findById(id); 
		if (lineItem.isPresent()) {
			jr = JsonResponse.getInstance(lineItem.get());
		}
		else {
			jr = JsonResponse.getErrorInstance("No line item found for id: "+id);
		}
		return jr;
	}
	
	@PostMapping("/")
	public JsonResponse createLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		try {
			li = lineItemRepo.save(li);
			jr = JsonResponse.getInstance(li);
		}
		catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getErrorInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		}
		catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error creating line item: "+e.getMessage());
			e.printStackTrace();
		}

		
		return jr;
	}
	
	@PutMapping("/")
	public JsonResponse updateLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		try {
			li = lineItemRepo.save(li);
			jr = JsonResponse.getInstance(li);
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error updating line item: "+e.getMessage());
			e.printStackTrace();
		}

		
		return jr;
	}
	
	@DeleteMapping("/{id}")
	public JsonResponse deleteLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			lineItemRepo.deleteById(id);
			jr = JsonResponse.getInstance("Line Item ID: "+id+ " deleted successfully");
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error deleting line item: "+e.getMessage());
			e.printStackTrace();
		}

		
		return jr;
	}
	
}
