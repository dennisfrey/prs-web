package com.prs.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prs.business.LineItem;

public interface LineItemRepository extends JpaRepository<LineItem, Integer> {

}
