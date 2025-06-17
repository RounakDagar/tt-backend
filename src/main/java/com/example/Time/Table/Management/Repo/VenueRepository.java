package com.example.Time.Table.Management.Repo;

import com.example.Time.Table.Management.Model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue,Long> {
    Optional<Venue> findByVenue(String venue);
}
