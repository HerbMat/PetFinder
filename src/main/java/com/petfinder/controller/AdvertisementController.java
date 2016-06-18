package com.petfinder.controller;

import com.petfinder.dao.AdvertisementRepository;
import com.petfinder.domain.Advertisement;
import com.petfinder.domain.Attachment;
import com.petfinder.domain.Tag;
import com.petfinder.service.AdvertisementService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.petfinder.domain.Location;
import com.petfinder.domain.Pet;
import com.petfinder.domain.PetCategory;
import com.petfinder.exception.NoUsersToNotifyException;
import com.petfinder.exception.UserDoesNotHavePermissionToAdvertisemntException;
import com.petfinder.rest.domain.SearchResults;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.RandomStringUtils;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdvertisementController {

    private final static Logger LOGGER = Logger.getLogger(AdvertisementController.class.getName());
    private final static int INITIAL_PAGE = 0;
    private final static String adminPath = "/admin";

    @Autowired
    AdvertisementService advertisementService;

    @RequestMapping(value = {"/", "/latest"})
    public String latestAdvertisements(Model model) {
        this.preparePagination(model, INITIAL_PAGE + 1);
        model.addAttribute("advertisements",
                advertisementService.getLatestAdvertisements(INITIAL_PAGE)
        );
        return "adlist";
    }

    @RequestMapping(value = "/latest/{page}")
    public String latestAdvertisements(@PathVariable int page, Model model) {
        this.preparePagination(model, page);
        model.addAttribute("advertisements",
                advertisementService.getLatestAdvertisements(page - 1)
        );
        return "adlist";
    }

    @RequestMapping(value = "/advertisement/{id}")
    public String advertisement(@PathVariable long id, Model model) {
        Advertisement ad = advertisementService.getAdvertisement(id);
        model.addAttribute("notfound", 0);
        if (ad == null) {
            model.addAttribute("notfound", id);
        }
        model.addAttribute("ad", ad);
        return "advertisement";
    }

    @RequestMapping(value = "/newAdd")
    public String newAdvertisement(@RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String race,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String voivodership,
            @RequestParam(required = false) String commune,
            @RequestParam(required = false) String place,
            @RequestParam(required = false) String tagsString,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) MultipartFile video,
            Model model) {
    
    	Advertisement advertisement = null;
        
    	if (title != null && content != null && petName != null && race != null && categoryName != null && voivodership != null && commune != null && place != null) {
            if (!title.equals("") && !content.equals("") && !petName.equals("") && !race.equals("") && !categoryName.equals("") && !voivodership.equals("") && !commune.equals("") && !place.equals("")) {

                List<Tag> tags = tagStringToList(tagsString);
                String imageName = uploadFile(image);
                String videoName = uploadFile(video);

                List<Attachment> attachments = setAttachment(imageName, videoName);
                advertisement = advertisementService.newAdvertisement(title, content, petName, age, race, categoryName, voivodership, commune, place, tags, attachments);
                model.addAttribute("statusOK", "Advertisement has been added successfully.");
            } else {
                PetCategory category = new PetCategory(categoryName);
                Pet pet = new Pet(petName, race, age, null, category);
                Location location = new Location(voivodership, place, commune);
                advertisement = new Advertisement(title, content, null, pet, location, null, null);
                model.addAttribute("advertisement", advertisement);
                model.addAttribute("tags", tagsString);
                model.addAttribute("statusEmpty", "Fields cannot remain empty.");
            }
        }
        //new advertisment information
        try {
			advertisementService.sendEmailNotification(advertisement);
		} catch (NoUsersToNotifyException e) {
            LOGGER.log(Level.SEVERE, "NoUsersToNotifyException is returned");
            model.addAttribute("status", e.getMessage());
		}
        
        model.addAttribute("categories", advertisementService.getAllCategories());
        return "addAdvertisement";
    }

    private List<Attachment> setAttachment(String imageName, String videoName) {
        List<Attachment> attachments = new ArrayList<>();
        if (imageName != null) {
            attachments.add(new Attachment(imageName, Attachment.Type.IMAGE, null));
        }
        if (videoName != null) {
            attachments.add(new Attachment(videoName, Attachment.Type.VIDEO, null));
        }
        return attachments;
    }

    private String uploadFile(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String[] partsiFileName = fileName.split("\\.");
                String newFileName = partsiFileName[0] + RandomStringUtils.randomAlphanumeric(6) + "." + partsiFileName[1];
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(new File(newFileName)));
                FileCopyUtils.copy(file.getInputStream(), stream);
                stream.close();
                LOGGER.log(Level.SEVERE, "You successfully uploaded {0}!", file.getOriginalFilename());
                return newFileName;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "You failed to upload {0} => {1}", new Object[]{file.getOriginalFilename(), e.getMessage()});
            }
        } else {
            LOGGER.log(Level.SEVERE, "You failed to upload {0} because the file was empty", file.getOriginalFilename());
        }
        return null;
    }

    @RequestMapping(value = "/getNewAdd")
    public String getToNewAdvertisement(Model model) {
        model.addAttribute("categories", advertisementService.getAllCategories());
        return "addAdvertisement";
    }

    private List<Tag> tagStringToList(String tagsString) {
        List<String> listTags = new ArrayList<>(Arrays.asList(tagsString.split(",")));
        listTags.removeAll(Arrays.asList("", null));
        List<Tag> tags = new ArrayList<>();
        for (String listTag : listTags) {
            tags.add(new Tag(listTag.trim()));
        }
        return tags;
    }

    @RequestMapping(value = "/editAdd")
    public String editAdvertisement(@RequestParam(required = false) Long id, Model model,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String race,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String voivodership,
            @RequestParam(required = false) String commune,
            @RequestParam(required = false) String place,
            @RequestParam(required = false) String tagsString,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) MultipartFile video) {

        if (title != null && content != null && petName != null && race != null && categoryName != null && voivodership != null && commune != null && place != null) {
            if (!title.equals("") && !content.equals("") && !petName.equals("") && !race.equals("") && !categoryName.equals("") && !voivodership.equals("") && !commune.equals("") && !place.equals("")) {

                List<Tag> tags = tagStringToList(tagsString);

                String imageName = uploadFile(image);
                String videoName = uploadFile(video);

                List<Attachment> attachments = setAttachment(imageName, videoName);
                try {
                    advertisementService.editAdvertisement(id, title, content, petName, age, race, categoryName, voivodership, commune, place, tags, attachments);
                    model.addAttribute("statusOK", "Advertisement has been edited successfully.");
                } catch (UserDoesNotHavePermissionToAdvertisemntException e) {
                    LOGGER.log(Level.SEVERE, "UserDoesNotHavePermissionToAdvertisemntException is returned");
                    model.addAttribute("status", e.getMessage());
                }
            } else {
                PetCategory category = new PetCategory(categoryName);
                Pet pet = new Pet(petName, race, age, null, category);
                Location location = new Location(voivodership, place, commune);
                Advertisement advertisement = new Advertisement(title, content, null, pet, location, null, null);
                model.addAttribute("advertisement", advertisement);
                model.addAttribute("tags", tagsString);
                model.addAttribute("files", advertisementService.getAttachmentNameForAdvertisement(id));
                model.addAttribute("addId", id);
                model.addAttribute("statusEmpty", "Fields cannot remain empty.");
            }
        }
        model.addAttribute("categories", advertisementService.getAllCategories());
        return "editAdvertisement";
    }

    @RequestMapping(value = "/removeAttachment")
    public String removeAttachment(@RequestParam(required = false) Long idAdd, Long idAttachment, Model model,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String race,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String voivodership,
            @RequestParam(required = false) String commune,
            @RequestParam(required = false) String place,
            @RequestParam(required = false) String tagsString) {

        advertisementService.removeAttachment(idAttachment);

        PetCategory category = new PetCategory(categoryName);
        Pet pet = new Pet(petName, race, age, null, category);
        Location location = new Location(voivodership, place, commune);
        Advertisement advertisement = new Advertisement(title, content, null, pet, location, null, null);

        model.addAttribute("advertisement", advertisement);
        model.addAttribute("addId", idAdd);
        model.addAttribute("tags", tagsString);
        model.addAttribute("files", advertisementService.getAttachmentNameForAdvertisement(idAdd));
        model.addAttribute("categories", advertisementService.getAllCategories());
        return "editAdvertisement";
    }

    @RequestMapping(value = "/getEditAdd")
    public String getToEditAdvertisement(@RequestParam(required = false) Long id, Model model) {
        try {
            Advertisement advertisement = advertisementService.getToEditAdvertisement(id);
            List<Tag> tagsList = advertisement.getTags();
            String currentTagsString = "";
            for (Tag tag : tagsList) {
                if (tagsList.indexOf(tag) != 0) {
                    currentTagsString += ",";
                }
                currentTagsString += tag.getName();
            }
            model.addAttribute("advertisement", advertisement);
            model.addAttribute("tags", currentTagsString);
            model.addAttribute("categories", advertisementService.getAllCategories());
            model.addAttribute("files", advertisementService.getAttachmentNameForAdvertisement(id));
            model.addAttribute("addId", id);
        } catch (UserDoesNotHavePermissionToAdvertisemntException e) {
            LOGGER.log(Level.SEVERE, "UserDoesNotHavePermissionToAdvertisemntException is returned");
            model.addAttribute("status", e.getMessage());
        }
        return "editAdvertisement";
    }

    @ResponseBody
    @RequestMapping(value = "/searchResult")
    public ModelAndView getSearchResults(Model model,
            @RequestParam(required = false) String adInfo,
            @RequestParam(required = false) String petInfo,
            @RequestParam(required = false) String locationInfo,
            @RequestParam(required = false) String tagInfo,
            @RequestParam(required = false) int page
    ) {
    	SearchResults searchResults = advertisementService.getSearchedAdvertisements(
    			page - 1, 20, 
    			adInfo, petInfo, 
    			locationInfo, 
    			tagInfo
    	);
        model.addAttribute("advertisements",
                searchResults.getAdvertisements()
        );
        this.preparePagination(model, page);
        model.addAttribute("adInfo", adInfo);
        model.addAttribute("petInfo", petInfo);
        model.addAttribute("locationInfo", locationInfo);
        model.addAttribute("tagInfo", tagInfo);
        model.addAttribute("pages", (long) Math.ceil(searchResults.getAllResultsCount()/ 20)+1);
        return new ModelAndView("searchResults");
    }
    
    @RequestMapping(value = "/admin/deleteAdv/{advId}", method = RequestMethod.DELETE)
	public String removeAdd(
			Model model, 
			@PathVariable int advId
			
	) {
    	advertisementService.deleteAdvertisement(advId);
    	
    	return "deleteSuccess";
	}

    private void preparePagination(Model model, int page) {
        long pages = advertisementService.getNumberOfPages(20);
        if (pages < 1) {
            pages = 1;
        }
        long firstpage = page == 1 ? 1 : page - 2;
        if (firstpage < 1) {
            firstpage = 1;
        }
        long lastpage = page == 1 ? 3 : page + 2;
        if (lastpage > pages) {
            lastpage = pages;
        }
        List<Long> printPages = new ArrayList<>();
        for (long p = firstpage; p <= lastpage; p++) {
            printPages.add(p);
        }
        model.addAttribute("page", page);
        model.addAttribute("pages", pages);
        model.addAttribute("firstpage", firstpage);
        model.addAttribute("lastpage", lastpage);
        model.addAttribute("printPages", printPages);
    }
}
