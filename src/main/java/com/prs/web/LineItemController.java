package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.*;
import com.prs.db.LineItemRepository;
import com.prs.db.RequestRepository;

@RestController
@RequestMapping("/line-items")
public class LineItemController {

	@Autowired
	private LineItemRepository lineItemRepo;

	@Autowired
	private RequestRepository requestRepo;

	@GetMapping("/")
	public JsonResponse list() {
		JsonResponse jr = null;
		List<LineItem> lineItems = lineItemRepo.findAll();
		if (lineItems.size() > 0) {
			jr = JsonResponse.getInstance(lineItems);
		} else {
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
		} else {
			jr = JsonResponse.getErrorInstance("No line item found for id: " + id);
		}
		return jr;
	}

	@GetMapping("/lines-for-pr/{id}")
	public JsonResponse listLineItemsForRequest(@PathVariable int id) {
		JsonResponse jr = null;
		List<LineItem> lineItems = lineItemRepo.findByRequestId(id);
		if (lineItems.size() > 0) {
			jr = JsonResponse.getInstance(lineItems);
		} else {
			jr = JsonResponse.getErrorInstance("No line items Found");
		}

		return jr;
	}

	@PostMapping("/")
	public JsonResponse createLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		try {
			li = lineItemRepo.save(li);
			Request request = li.getRequest();
			recalculateTotal(request);
			jr = JsonResponse.getInstance(li);
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getErrorInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error creating line item: " + e.getMessage());
			e.printStackTrace();
		}

		return jr;
	}

	@PutMapping("/")
	public JsonResponse updateLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		try {
			li = lineItemRepo.save(li);
			Request request = li.getRequest();
			recalculateTotal(request);
			jr = JsonResponse.getInstance(li);
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error updating line item: " + e.getMessage());
			e.printStackTrace();
		}

		return jr;
	}

	@DeleteMapping("/{id}")
	public JsonResponse deleteLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			LineItem li = lineItemRepo.getOne(id);
			lineItemRepo.deleteById(id);
			Request request = li.getRequest();
			recalculateTotal(request);
			jr = JsonResponse.getInstance("Line Item ID: " + id + " deleted successfully");
		} catch (Exception e) {
			jr = JsonResponse.getErrorInstance("Error deleting line item: " + e.getMessage());
			e.printStackTrace();
		}

		return jr;
	}

	public void recalculateTotal(Request r) {
		List<LineItem> lineItems = lineItemRepo.findByRequestId(r.getId());
		double total = 0.0;
		for (LineItem lineItem : lineItems) {
			Product product = lineItem.getProduct();
			total += product.getPrice() * lineItem.getQuantity();
		}
		try {
			r.setTotal(total);
			requestRepo.save(r);
		} catch (Exception e) {
			throw e;
		}
	}

}
