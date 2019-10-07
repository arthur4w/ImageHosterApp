package ImageHoster.repository;

import ImageHoster.model.Comment;
import ImageHoster.model.Image;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

//The annotation is a special type of @Component annotation which describes that the class defines a data repository
@Repository
public class ImageRepository {

	// Get an instance of EntityManagerFactory from persistence unit with name as
	// 'imageHoster'
	@PersistenceUnit(unitName = "imageHoster")
	private EntityManagerFactory entMngrFctry;

	// The method receives the Image object to be persisted in the database
	// Creates an instance of EntityManager
	// Starts a transaction
	// The transaction is committed if it is successful
	// The transaction is rolled back in case of unsuccessful transaction
	public Image uploadImage(Image newImage) {

		EntityManager entMngr = entMngrFctry.createEntityManager();
		EntityTransaction transaction = entMngr.getTransaction();

		try {
			transaction.begin();
			entMngr.persist(newImage);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		}
		return newImage;
	}

	// The method creates an instance of EntityManager
	// Executes JPQL query to fetch all the images from the database
	// Returns the list of all the images fetched from the database
	public List<Image> getAllImages() {
		EntityManager entMngr = entMngrFctry.createEntityManager();
		TypedQuery<Image> query = entMngr.createQuery("SELECT i from Image i", Image.class);
		List<Image> resultList = query.getResultList();

		return resultList;
	}

	// The method creates an instance of EntityManager
	// Executes JPQL query to fetch the image from the database with corresponding
	// title
	// Returns the image in case the image is found in the database
	// Returns null if no image is found in the database

	// Edited to select image by title AND ID
	public Image getImageByIDorTitle(Integer id, String title) {
		EntityManager em = entMngrFctry.createEntityManager();
		try {
			TypedQuery<Image> typedQuery = em
					.createQuery("SELECT img from Image img where img.title =:title AND  img.id=:id", Image.class);
			typedQuery.setParameter("title", title);
			typedQuery.setParameter("id", id);

			return typedQuery.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	// The method creates an instance of EntityManager
	// Executes JPQL query to fetch the image from the database with corresponding
	// id
	// Returns the image fetched from the database
	public Image getImage(Integer imageId) {
		EntityManager entMngr = entMngrFctry.createEntityManager();
		TypedQuery<Image> typedQuery = entMngr.createQuery("SELECT i from Image i where i.id =:imageId", Image.class)
				.setParameter("imageId", imageId);
		Image image = typedQuery.getSingleResult();
		return image;
	}

	// The method receives the Image object to be updated in the database
	// Creates an instance of EntityManager
	// Starts a transaction
	// The transaction is committed if it is successful
	// The transaction is rolled back in case of unsuccessful transaction
	public void updateImage(Image updatedImage) {
		EntityManager entMngr = entMngrFctry.createEntityManager();
		EntityTransaction transaction = entMngr.getTransaction();

		try {
			transaction.begin();
			entMngr.merge(updatedImage);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		}
	}

	// The method receives the Image id of the image to be deleted in the database
	// Creates an instance of EntityManager
	// Starts a transaction
	// Get the image with corresponding image id from the database
	// This changes the state of the image model from detached state to persistent
	// state, which is very essential to use the remove() method
	// If you use remove() method on the object which is not in persistent state, an
	// exception is thrown
	// The transaction is committed if it is successful
	// The transaction is rolled back in case of unsuccessful transaction
	public void deleteImage(Integer imageId) {
		EntityManager entMngr = entMngrFctry.createEntityManager();
		EntityTransaction transaction = entMngr.getTransaction();

		try {
			transaction.begin();
			Image image = entMngr.find(Image.class, imageId);
			entMngr.remove(image);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		}
	}

	// To confirm if the current user is the owner of the image
	public boolean confirmOwner(Integer id, String userName) {
		EntityManager entMngr = entMngrFctry.createEntityManager();

		try {
			TypedQuery<Image> typedQuery = entMngr
					.createQuery("SELECT img from Image img where img.id =:id ", Image.class).setParameter("id", id);
			if (typedQuery.getSingleResult().getUser().getUsername().equalsIgnoreCase(userName)) {
				return true;
			} else {
				return false;
			}
		} catch (NoResultException e) {
			return false;
		}
	}

	// To create a comment to an image
	public Comment createComment(Comment comment) {
		EntityManager entMngr = entMngrFctry.createEntityManager();
		EntityTransaction transaction = entMngr.getTransaction();

		try {
			transaction.begin();
			entMngr.merge(comment);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
		return comment;
	}

	//To get all the comments for a particular image
	public List<Comment> getAllComments() {
		EntityManager entMngr = entMngrFctry.createEntityManager();
		TypedQuery<Comment> query = entMngr.createQuery("SELECT cmnt from Comment cmnt", Comment.class);
		List<Comment> resultList = query.getResultList();
		return resultList;
	}
}