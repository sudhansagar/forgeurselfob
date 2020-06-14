package com.forgeurself.ob.repos;

import com.forgeurself.ob.entities.HomeLoan;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author madhusudhan.gr
 */
public interface LoanRepository extends JpaRepository<HomeLoan, Long> {


}
