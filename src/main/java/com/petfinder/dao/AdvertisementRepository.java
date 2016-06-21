package com.petfinder.dao;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.petfinder.domain.Advertisement;
import com.petfinder.domain.Location;
import com.petfinder.domain.Pet;
import com.petfinder.domain.Tag;
import com.petfinder.domain.User;

public interface AdvertisementRepository
        extends CrudRepository<Advertisement, Long> {

    @Transactional
    List<Advertisement> findByIsDeletedOrderByCreatedDate(Boolean isDeleted);

    @Transactional
    Page<Advertisement> findByIsDeletedOrderByCreatedDate(Boolean isDeleted,
                                                          Pageable pageable);

    @Transactional
    List<Advertisement> findByUser(User user);

    @Transactional
    Page<Advertisement> findByUser(User user, Pageable pageable);

    @Transactional
    List<Advertisement> findByPet(Pet pet);

    @Transactional
    Page<Advertisement> findByPet(Pet pet, Pageable pageable);
    
    @Transactional
    Page<Advertisement> findDistinctByPetInAndTitleContainingAndLocationInAndTagsIn(
    	Collection<Pet> pets, 
    	String title,
    	Collection<Location> locations,
    	Collection<Tag> tags,
    	Pageable pageable
    );
    
    @Transactional
    List<Advertisement> findDistinctByPetInAndTitleContainingAndLocationInAndTagsIn(
    	Collection<Pet> pets, 
    	String title,
    	Collection<Location> locations,
    	Collection<Tag> tags
    );
    
    @Transactional
    List<Advertisement> findByLocation(Location location);

    @Transactional
    Page<Advertisement> findByLocation(Location location, Pageable pageable);

    @Transactional
    List<Advertisement> findByTitleContaining(String title);

    @Transactional
    Page<Advertisement> findByTitleContaining(String title, Pageable pageable);
    
}
