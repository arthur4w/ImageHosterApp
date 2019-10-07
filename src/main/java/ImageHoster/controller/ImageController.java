package ImageHoster.controller;

import ImageHoster.model.Comment;
import ImageHoster.model.Image;
import ImageHoster.model.Tag;
import ImageHoster.model.User;
import ImageHoster.service.ImageService;
import ImageHoster.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private TagService tagService;

    //This method displays all the images in the user home page after successful login
    @RequestMapping("images")
    public String getUserImages(Model model) {
        List<Image> images = imageService.getAllImages();
        model.addAttribute("images", images);
        return "images";
    }

    //To show the details of the image corresponding to the said ID or title
    @RequestMapping("/images/{id}/{title}")
    public String showImage(@PathVariable("id") Integer id,@PathVariable("title") String title, Model model) {
        Image image = imageService.getImageByIDorTitle(id,title);
        model.addAttribute("image", image);
        model.addAttribute("tags", image.getTags());
        model.addAttribute("comments",image.getComments());
        return "images/image";
    }

    //To upload a new image
    @RequestMapping("/images/upload")
    public String newImage() {
        return "images/upload";
    }

    //To upload a new image by POST request
    @RequestMapping(value = "/images/upload", method = RequestMethod.POST)
    public String createImage(@RequestParam("file") MultipartFile file, @RequestParam("tags") String tags, Image newImage, HttpSession session) throws IOException {

        User user = (User) session.getAttribute("loggeduser");
        newImage.setUser(user);
        String uploadedImageData = convertUploadedFileToBase64(file);
        newImage.setImageFile(uploadedImageData);

        List<Tag> imageTags = findOrCreateTags(tags);
        newImage.setTags(imageTags);
        newImage.setDate(new Date());
        imageService.uploadImage(newImage);
        return "redirect:/images";
    }

    //To have the image edited, we must ensure that the user is the admin of the image
    @RequestMapping(value = "/editImage")
    public String editImage(@RequestParam("imageId") Integer imageId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggeduser");

        //Checking if the "loggeduser" is the owner of the image or not
        Image image = imageService.getImage(imageId);

        //If "loggeduser" is the owner of the image
        if(this.imageService.confirmOwner(imageId, user.getUsername())){
            String tags = convertTagsToString(image.getTags());
            model.addAttribute("image", image);
            model.addAttribute("tags", tags);
            return "images/edit";
        }

        //If "loggeduser" is not the owner
        else {
            String error = "Only the owner of the image can edit the image";
            model.addAttribute("editError", error);
            model.addAttribute("image", image);
            model.addAttribute("comments",image.getComments());
            return "images/image";
        }

    }

    //To upload a new image by PUT request
    @RequestMapping(value = "/editImage", method = RequestMethod.PUT)
    public String editImageSubmit(@RequestParam("file") MultipartFile file, @RequestParam("imageId") Integer imageId, @RequestParam("tags") String tags, Image updatedImage, HttpSession session) throws IOException {

        Image image = imageService.getImage(imageId);
        String updatedImageData = convertUploadedFileToBase64(file);
        List<Tag> imageTags = findOrCreateTags(tags);

        if (updatedImageData.isEmpty())
            updatedImage.setImageFile(image.getImageFile());
        else {
            updatedImage.setImageFile(updatedImageData);
        }

        updatedImage.setId(imageId);
        User user = (User) session.getAttribute("loggeduser");
        updatedImage.setUser(user);
        updatedImage.setTags(imageTags);
        updatedImage.setDate(new Date());

        imageService.updateImage(updatedImage);
        return "redirect:/images/" + updatedImage.getTitle();
    }

    //To have an image deleted, we need to make sure if the user is an admin or not
    @RequestMapping(value = "/deleteImage", method = RequestMethod.DELETE)
    public String deleteImageSubmit(@RequestParam(name = "imageId") Integer imageId, HttpSession session, Model model) {

    	//Checking if the "loggeduser" is the owner of the image or not
        User user = (User) session.getAttribute("loggeduser");
        Image image = imageService.getImage(imageId);

        //If "loggeduser" is the admin, let him delete the image
        if(this.imageService.confirmOwner(imageId, user.getUsername())){
            this.imageService.deleteImage(imageId);
            return "redirect:/images";
        }
        
        //If "loggeduser" is NOT the admin, don't let him delete the image
        else{
            String error = "Only the owner of the image can delete the image";
            model.addAttribute("deleteError", error);
            model.addAttribute("image", image);
            model.addAttribute("comments",image.getComments());
            return "images/image";
        }
    }

    //For the comment section
    @RequestMapping(value = "/image/{imageId}/{imageTitle}/comments", method = RequestMethod.POST)
    public String createComment(@PathVariable("imageId") Integer imageId,
                              @PathVariable("imageTitle") String imageTitle,
                                @RequestParam("comment") String comment,
                              HttpSession session,
                              Comment newComment,
                              Image updatedImage, Model model){

        User user = (User) session.getAttribute("loggeduser");
        newComment.setUser(user);
        Image image = this.imageService.getImage(imageId);
        newComment.setCreatedDate(LocalDate.now());
        newComment.setId(imageId);
        newComment.setImage(image);
        newComment.setText(comment);

        //Creating a new comment in the database for the image by the "loggeduser"
        this.imageService.createComment(newComment);
        return this.showImage(imageId, imageTitle, model);
    }

    //This method converts the image to Base64 format
    private String convertUploadedFileToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    //Extracting the list of tags for the images
    private List<Tag> findOrCreateTags(String tagNames) {
        StringTokenizer st = new StringTokenizer(tagNames, ",");
        List<Tag> tags = new ArrayList<Tag>();

        while (st.hasMoreTokens()) {
            String tagName = st.nextToken().trim();
            Tag tag = tagService.getTagByName(tagName);

            if (tag == null) {
                Tag newTag = new Tag(tagName);
                tag = tagService.createTag(newTag);
            }
            tags.add(tag);
        }
        return tags;
    }

    //Displaying the list of the tags as String
    private String convertTagsToString(List<Tag> tags) {
        StringBuilder stringForTags = new StringBuilder();

        //Because we don't want to append a comma at the end of the list
        for (int i = 0; i <= tags.size() - 2; i++) {
            stringForTags.append(tags.get(i).getName()).append(", ");
        }
        Tag lastTag = tags.get(tags.size() - 1);
        stringForTags.append(lastTag.getName());
        return stringForTags.toString();
    }
}