package com.adrian.ecommerceproject.controllers;

import java.util.List;
import java.util.Optional;

import com.adrian.ecommerceproject.models.Category;
import com.adrian.ecommerceproject.models.Product;
import com.adrian.ecommerceproject.models.Role;
import com.adrian.ecommerceproject.models.User;
import com.adrian.ecommerceproject.payload.response.MessageResponse;
import com.adrian.ecommerceproject.repository.CategoryRepository;
import com.adrian.ecommerceproject.repository.ProductRepository;
import com.adrian.ecommerceproject.repository.RoleRepository;
import com.adrian.ecommerceproject.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/organic_shop")
public class AppResponseController {

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	ProductRepository productReposiotry;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	UserRepository userRepository;
	
    @GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('ROLE_USER')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/super")
	@PreAuthorize("hasRole('ROLE_SUPER')")
	public String superAccess() {
		return "Super Admin Board.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String adminAccess() {
		return " Admin Board.";
	}

	/** DELETING THINGS ON THE DATABASE */
	@DeleteMapping("/admin/delete/user/{id}")
	@PreAuthorize("hasRole('ROLE_SUPER')")
	public ResponseEntity<?> deleteUserById(@PathVariable("id") long id){
		try {
			userRepository.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("User Successfully Deleted"));
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/admin/delete/role/{id}")
	@PreAuthorize("hasAnyRole('ROLE_SUPER', 'ROLE_ADMIN')")
	public ResponseEntity<?> deleteRoleById(@PathVariable("id") long id){
		try {
			roleRepository.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("Role Successfully Deleted"));
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/admin/delete/category/{id}")
	@PreAuthorize("hasAnyRole('ROLE_SUPER', 'ROLE_ADMIN')")
	public ResponseEntity<?> deleteCategoryById(@PathVariable("id") long id){
		try {
			categoryRepository.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("Category Successfully Deleted"));
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/admin/delete/product/{id}")
	@PreAuthorize("hasAnyRole('ROLE_SUPER', 'ROLE_ADMIN')")
	public ResponseEntity<?> deleteProductById(@PathVariable("id") long id){
		try {
			productReposiotry.deleteById(id);
			return ResponseEntity.ok(new MessageResponse("Product deleted succesfully.."));
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// productReposiotry.deleteById(id);
		// return ResponseEntity.ok(new MessageResponse("Product deleted succesfully.."));
	}

	@DeleteMapping("/admin/delete/all/products")
	@PreAuthorize("hasRole('ROLE_SUPER')")
	public ResponseEntity<?> deleteAllProducts(){
		try {
			productReposiotry.deleteAll();
			return ResponseEntity.ok(new MessageResponse("All Products Deleted Successfully"));
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/admin/delete/all/categories")
	@PreAuthorize("hasRole('ROLE_SUPER')")
	public ResponseEntity<?> deleteAllCategories(){
		try {
			categoryRepository.deleteAll();
			return ResponseEntity.ok(new MessageResponse("All Categories Deleted Successfully"));
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/admin/delete/all/roles")
	@PreAuthorize("hasAnyRole('ROLE_SUPER')")
	public ResponseEntity<?> deleteAllRoles(){
		try {
			roleRepository.deleteAll();
			return ResponseEntity.ok(new MessageResponse("All Roles Deleted Successfully"));
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/admin/delete/all/users")
	@PreAuthorize("hasAnyRole('ROLE_SUPER')")
	public ResponseEntity<?> deleteAllUsers(){
		try {
			userRepository.deleteAll();
			return ResponseEntity.ok(new MessageResponse("All Users Deleted Successfully"));
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	/*BROWSING THE DATABASE*/
	@GetMapping("/find/admin/roles")
	@PreAuthorize("hasAnyRole('ROLE_SUPER', 'ROLE_ADMIN')")
	public List<Role> getAdminRoles(){
		return roleRepository.findAll();
	}

	@GetMapping("/admin/find/user/{id}")
	@PreAuthorize("hasAnyRole('ROLE_SUPER', 'ROLE_ADMIN')")
	public ResponseEntity<?> getUserById(@PathVariable("id") long id){
		Optional<User> getUser = userRepository.findById(id);

		if(!getUser.isPresent()){
			return ResponseEntity.ok(new MessageResponse("User not found"));
		}
		return new ResponseEntity<>(getUser.get(), HttpStatus.OK);
	}

	@GetMapping("/find/all/products")
	public List<Product> getProducts(){
		return productReposiotry.findAll();
	}

	@GetMapping("/find/all/categories")
	public List<Category> getCategories() {
		return categoryRepository.findAll();
	}

	@GetMapping("/find/product/{id}")
	public ResponseEntity<?> getProduct(@PathVariable("id") long id) {
		Optional<Product> getProduct = productReposiotry.findById(id);

		if(!getProduct.isPresent()){
			return ResponseEntity.ok(new MessageResponse("Product not found."));
		}
		return new ResponseEntity<>(getProduct.get(), HttpStatus.OK);

	}

	@GetMapping("/find/all/products?category={id}")
	public Optional<Product> getProductCategoriesId(@PathVariable(value = "id") Long id){
		return productReposiotry.getAllProductsByCategoryId(id);
	}

}
