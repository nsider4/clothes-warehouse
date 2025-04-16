package com.nicolas.distribution_centre_api.repository;

import com.nicolas.distribution_centre_api.dto.DistributionCentre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionCentreRepository extends JpaRepository<DistributionCentre, Long> {
}
