package com.ncl.sindhu.repository;

import com.ncl.sindhu.domain.Ownerdetails;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Ownerdetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OwnerdetailsRepository extends JpaRepository<Ownerdetails,Long> {
    
}
