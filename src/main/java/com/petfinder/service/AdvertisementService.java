package com.petfinder.service;

import com.petfinder.dao.AdvertisementRepository;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.cfg.EJB3DTDEntityResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.petfinder.dao.AttachmentRepository;
import com.petfinder.dao.LocationRepository;
import com.petfinder.dao.PetCategoryRepository;
import com.petfinder.dao.PetRepository;
import com.petfinder.dao.TagRepository;
import com.petfinder.dao.UserRepository;
import com.petfinder.domain.Advertisement;
import com.petfinder.domain.Attachment;
import com.petfinder.domain.Location;
import com.petfinder.domain.Pet;
import com.petfinder.domain.PetCategory;
import com.petfinder.domain.Tag;
import com.petfinder.domain.User;
import com.petfinder.exception.NoUsersToNotifyException;
import com.petfinder.exception.UserDoesNotHavePermissionToAdvertisemntException;
import com.petfinder.rest.domain.EmailSender;
import com.petfinder.rest.domain.SearchResults;
import com.petfinder.dao.ReportRepository;

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
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    private final static Logger LOGGER = Logger.getLogger(AdvertisementService.class.getName());

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
    public Advertisement newAdvertisement(String title, String content, String petName, Integer age, String race, String categoryName, String voivodership, String commune, String place, List<Tag> tags, List<Attachment> attachments) {
        User user = userRepository.findOneByLogin(userService.getLoggedUserName());
        PetCategory petCategory = petCategoryRepository.findOne(Long.parseLong(categoryName));
        Pet pet = setPetForAdvertisement(petName, race, petCategory, user, age);
        Location location = setLocationForAdvertisement(voivodership, place, commune);
        tagsSet(tags);
        Advertisement advertisement = new Advertisement(title, content, user, pet, location, tags, attachments);
        advertisement = setAtachment(attachments, title, content, user, pet, location, tags, advertisement);
        advertisementRepository.save(advertisement);

        return advertisement;
    }

    private Advertisement setAtachment(List<Attachment> attachments, String title, String content, User user, Pet pet, Location location, List<Tag> tags, Advertisement advertisement) {
        for (Attachment attachment : attachments) {
            attachmentRepository.save(attachment);
        }

        for (Attachment attachment : attachments) {
            attachment.setAdvertisement(advertisement);
        }
        advertisement.setAttachments(attachments);
        return advertisement;
    }

    private void tagsSet(List<Tag> tags) {
        for (int i = 0; i < tags.size(); i++) {
            if (tagRepository.findOneByName(tags.get(i).getName()) == null) {
                tagRepository.save(tags.get(i));
            } else {
                tags.set(i, tagRepository.findOneByName(tags.get(i).getName()));
            }
        }
    }

    @Transactional
    public SearchResults getSearchedAdvertisements(
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
                .findDistinctByPetInAndTitleContainingAndLocationInAndTagsIn(
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
        int allResultsCounts = advertisementRepository
                .findDistinctByPetInAndTitleContainingAndLocationInAndTagsIn(
                        pets,
                        adInfo,
                        locations,
                        tags
                ).size();

        return new SearchResults(advertisements, (int) allResultsCounts);
    }

    public Advertisement getToEditAdvertisement(Long id) throws UserDoesNotHavePermissionToAdvertisemntException {
        Advertisement advertisement = advertisementRepository.findOne(id);
        if (advertisement.getUser() == null || advertisement.getUser().equals(userService.getLoggedUserName())) {
            throw new UserDoesNotHavePermissionToAdvertisemntException("User does not have permission to advertisemnt");
        }
        advertisement.getAttachments().size();
        advertisement.getContent();
        advertisement.getLocation();
        advertisement.getPet();
        advertisement.getTags().size();
        return advertisement;
    }

    public Advertisement editAdvertisement(Long id, String title, String content, String petName, Integer age, String race, String categoryName, String voivodership, String commune, String place, List<Tag> tags, List<Attachment> attachments) throws UserDoesNotHavePermissionToAdvertisemntException {
        Advertisement advertisement = advertisementRepository.findOne(id);
        if (advertisement != null) {
            if (advertisement.getUser() == null || advertisement.getUser().equals(userService.getLoggedUserName())) {
                throw new UserDoesNotHavePermissionToAdvertisemntException("User does not have permission to advertisemnt");
            }
            advertisement.setTitle(title);
            advertisement.setContent(content);

            User user = userRepository.findOneByLogin(userService.getLoggedUserName());
            PetCategory petCategory = petCategoryRepository.findOne(Long.parseLong(categoryName));
            Pet pet = setPetForAdvertisement(petName, race, petCategory, user, age);
            advertisement.setPet(pet);
            Location location = setLocationForAdvertisement(voivodership, place, commune);
            advertisement.setLocation(location);

            tagsSet(tags);
            advertisement.setTags(tags);
            advertisement = setAtachment(attachments, title, content, user, pet, location, tags, advertisement);

            advertisementRepository.save(advertisement);
        }

        return advertisement;
    }

    private Location setLocationForAdvertisement(String voivodership, String place, String commune) {
        Location location;
        List<Location> locationList = locationRepository.findByVoivodershipAndPlaceAndCommune(voivodership, place, commune);
        if (locationList.isEmpty()) {
            location = new Location(voivodership, place, commune);
            locationRepository.save(location);
        } else {
            location = locationList.get(0);
        }
        return location;
    }

    private Pet setPetForAdvertisement(String petName, String race, PetCategory petCategory, User user, Integer age) {
        Pet pet;
        List<Pet> petList = petRepository.findByNameAndRaceAndCategoryAndOwner(petName, race, petCategory, user);
        if (petList.isEmpty()) {
            pet = new Pet(petName, race, age, user, petCategory);
            petRepository.save(pet);
        } else {
            pet = petList.get(0);
        }
        return pet;
    }

    @Transactional
    public List<User> getUsersToNotify() throws NoUsersToNotifyException {

        if (userRepository.getUsersToNotify() != null) {
            return userRepository.getUsersToNotify();
        }

        throw new NoUsersToNotifyException("Users to notify were not found.");
    }

    public void sendEmailNotification(Advertisement advertisement) throws NoUsersToNotifyException {
        List<User> usersToNotify = getUsersToNotify();

        EmailSender es = new EmailSender(usersToNotify, advertisement);
        Thread t = new Thread(es);
        t.start();
    }

    public void teAdvertisement(int advId) {
        Advertisement advertisement = this.getAdvertisement(advId);
        this.advertisementRepository.delete(advertisement);
    }

    @PostConstruct
    private void setDatabase() {
//        PetCategory dog = new PetCategory("Psy");
//        PetCategory cat = new PetCategory("Koty");
//        PetCategory mammal = new PetCategory("Inne saaki");
//        PetCategory bird = new PetCategory("Ptaki");
//        PetCategory reptile = new PetCategory("Gady");
//        PetCategory amphibian = new PetCategory("Płazy");
//        PetCategory fish = new PetCategory("Ryby");
//        PetCategory other = new PetCategory("Inne");
//        petCategoryRepository.save(dog);
//        petCategoryRepository.save(cat);
//        petCategoryRepository.save(mammal);
//        petCategoryRepository.save(bird);
//        petCategoryRepository.save(reptile);
//        petCategoryRepository.save(amphibian);
//        petCategoryRepository.save(fish);
//        petCategoryRepository.save(other);

    }

    public List<PetCategory> getAllCategories() {
        return petCategoryRepository.findAll();
    }

    public List<Attachment> getAttachmentNameForAdvertisement(Long addId) {
        Advertisement advertisement = advertisementRepository.findOne(addId);
        List<Attachment> advertisementList = attachmentRepository.findByAdvertisement(advertisement);
        // List<String> filesName = new ArrayList<>();
        for (Attachment attachment : advertisementList) {
            String[] partsiFileName = attachment.getUri().split("\\.");
            //filesName.add(partsiFileName[0].substring(0, partsiFileName[0].length() - 6) + "." + partsiFileName[1]);
            advertisementList.get(advertisementList.indexOf(attachment)).setUri(partsiFileName[0].substring(0, partsiFileName[0].length() - 6) + "." + partsiFileName[1]);
        }
        return advertisementList;
    }

    public void removeAttachment(Long id) {
        Attachment attachment = attachmentRepository.findOne(id);
        try {
            File file = new File(attachment.getUri());
            if (file.delete()) {
                LOGGER.log(Level.SEVERE, "{0} is deleted!", file.getName());
                attachmentRepository.delete(attachment);
            } else {
                LOGGER.log(Level.SEVERE, "Delete operation is failed.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
    }

    public void deleteAdvertisement(Long id) throws UserDoesNotHavePermissionToAdvertisemntException {
        Advertisement advertisement = advertisementRepository.findOne(id);
        if (advertisement != null) {
            if (advertisement.getUser() == null || advertisement.getUser().equals(userService.getLoggedUserName())) {
                throw new UserDoesNotHavePermissionToAdvertisemntException("User does not have permission to advertisemnt");
            }
            advertisement.setDeleted(true);
            advertisementRepository.save(advertisement);
        }
    }
}
