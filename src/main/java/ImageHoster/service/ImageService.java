package ImageHoster.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ImageHoster.model.Comment;
import ImageHoster.model.Image;
import ImageHoster.repository.ImageRepository;

@Service
public class ImageService {
	@Autowired
	private ImageRepository imageRepository;

	// Call the getAllImages() method in the Repository and obtain a List of all the
	// images in the database
	public List<Image> getAllImages() {
		return imageRepository.getAllImages();
	}

	// The method calls the createImage() method in the Repository and passes the
	// image to be persisted in the database
	public void uploadImage(Image image) {
		imageRepository.uploadImage(image);
	}

	// The method calls the getImageByTitle() method in the Repository and passes
	// the title of the image to be fetched
	public Image getImageByIDorTitle(Integer id, String title) {
		return imageRepository.getImageByIDorTitle(id, title);
	}

	// The method calls the getImage() method in the Repository and passes the id of
	// the image to be fetched
	public Image getImage(Integer imageId) {
		return imageRepository.getImage(imageId);
	}

	// The method calls the updateImage() method in the Repository and passes the
	// Image to be updated in the database
	public void updateImage(Image updatedImage) {
		imageRepository.updateImage(updatedImage);
	}

	// The method calls the deleteImage() method in the Repository and passes the
	// Image id of the image to be deleted in the database
	public void deleteImage(Integer imageId) {
		imageRepository.deleteImage(imageId);
	}

	// Calling the method to confirm if the current user is the owner of the image or not
	public boolean confirmOwner(Integer id, String userName) {
		return this.imageRepository.confirmOwner(id, userName);
	}

	// Calling the method to create a comment and saving it in the repository
	public Comment createComment(Comment comment) {
		return this.imageRepository.createComment(comment);
	}
}
