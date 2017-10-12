package com.ncl.sindhu.repository;

import com.ncl.sindhu.domain.Mails;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Mails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MailsRepository extends JpaRepository<Mails,Long> {
    
}
