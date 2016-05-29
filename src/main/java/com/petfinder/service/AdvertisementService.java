package com.petfinder.service;

import com.petfinder.dao.*;
import com.petfinder.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AdvertisementService {

    @Autowired
    AdvertisementRepository advertisementRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    PetCategoryRepository petCategoryRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    AttachmentRepository attachmentRepository;

    @Transactional
    public List<Advertisement> getLatestAdvertisements(int page, int perPage) {
        List<Advertisement> advertisements
                = advertisementRepository.findByIsDeletedOrderByCreatedDate(
                        false,
                        new PageRequest(page, perPage)
                ).getContent();
        // Call relations to make sure those are delivered with advertisement.
        // It's required because of lazy loading of related entities, pretty
        // performance unfriendly, but still better than eager loading.
        for (Advertisement advertisement : advertisements) {
            advertisement.getAttachments().size();
            advertisement.getLocation();
            advertisement.getPet();
            advertisement.getTags().size();
            advertisement.getUser();
        }
        return advertisements;
    }

    @Transactional
    public List<Advertisement> getLatestAdvertisements(int page) {
        return getLatestAdvertisements(page, 20);
    }

    public Advertisement getAdvertisement(long id) {
        return advertisementRepository.findOne(id);
    }

    @Transactional
    public long getNumberOfPages(long perPage) {
        long pages = advertisementRepository.count();
        return (long) Math.ceil(pages / perPage);
    }

    @Transactional
    public void newAdvertisement(String title, String content, String petName, Integer age, String race, String categoryName, String voivodership, String commune, String place, List<Tag> tags, List<Attachment> attachments) {
        //TODO user, który jest teraz zalogowany
        User user = null;
        PetCategory petCategory = null;
        if (petCategoryRepository.findByName(categoryName).isEmpty()) {
            petCategory = new PetCategory(categoryName);
            petCategoryRepository.save(petCategory);
        } else {
            petCategory = petCategoryRepository.findByName(categoryName).get(0);
        }
        Pet pet = new Pet(petName, race, age, user, petCategory);
        petRepository.save(pet);
        Location location = new Location(voivodership, place, commune);
        locationRepository.save(location);
        for (int i = 0; i < tags.size(); i++) {
            if (tagRepository.findOneByName(tags.get(i).getName()) == null) {
                tagRepository.save(tags.get(i));
            } else {
                tags.set(i, tagRepository.findOneByName(tags.get(i).getName()));
            }
        }
        for (Attachment attachment : attachments) {
            attachmentRepository.save(attachment);
        }

        Advertisement advertisement = new Advertisement(title, content, user, pet, location, tags, attachments);
        for (Attachment attachment : attachments) {
            attachment.setAdvertisement(advertisement);
        }
        advertisement.setAttachments(attachments);
        advertisementRepository.save(advertisement);
    }
    
    @Transactional
    public List<Advertisement> getSearchedAdvertisements(
    	int page, 
    	int perPage,
    	String adInfo,
    	String petInfo,
    	String locationInfo,
    	String tagInfo
    ) {

    	List<PetCategory> categories = petCategoryRepository.findByNameContaining(petInfo);
    	List<Pet> pets = petRepository.findByNameContainingOrRaceContainingOrCategoryIn(
			petInfo, 
			petInfo, 
			categories
    	);
		List<Location> locations = locationRepository.findByVoivodershipContainingOrPlaceContainingOrCommuneContaining(
	    		locationInfo, 
	    		locationInfo, 
	    		locationInfo
	    	);
    	List<Tag> tags = tagRepository.findByNameContaining(tagInfo);
        List<Advertisement> advertisements = advertisementRepository
		.findByPetInAndTitleContainingAndLocationInAndTagsIn(
			pets, 
			adInfo, 
			locations,
			tags,
		    new PageRequest(page, perPage)
        ).getContent();

        for (Advertisement advertisement : advertisements) {
            advertisement.getAttachments().size();
            advertisement.getLocation();
            advertisement.getPet();
            advertisement.getTags().size();
            advertisement.getUser();
        }
        return advertisements;
    }
}
