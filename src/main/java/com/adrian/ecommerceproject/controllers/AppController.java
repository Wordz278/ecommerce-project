package com.adrian.ecommerceproject.controllers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.print.attribute.standard.Media;
import javax.validation.Valid;

import com.adrian.ecommerceproject.models.Category;
import com.adrian.ecommerceproject.models.ECategories;
import com.adrian.ecommerceproject.models.ERole;
import com.adrian.ecommerceproject.models.ImageUpload;
import com.adrian.ecommerceproject.models.Product;
import com.adrian.ecommerceproject.models.Role;
import com.adrian.ecommerceproject.models.User;
import com.adrian.ecommerceproject.payload.request.LoginRequest;
import com.adrian.ecommerceproject.payload.request.ProductRequest;
import com.adrian.ecommerceproject.payload.request.SignupRequest;
import com.adrian.ecommerceproject.payload.request.UploadImage;
import com.adrian.ecommerceproject.payload.response.MessageResponse;
import com.adrian.ecommerceproject.payload.response.JwtResponse;
import com.adrian.ecommerceproject.repository.CategoryRepository;
import com.adrian.ecommerceproject.repository.ImageUploadRepository;
import com.adrian.ecommerceproject.repository.ProductRepository;
import com.adrian.ecommerceproject.repository.RoleRepository;
import com.adrian.ecommerceproject.repository.UserRepository;
import com.adrian.ecommerceproject.security.jwt.JwtUtils;
import com.adrian.ecommerceproject.security.services.UserDetailsImpl;
import com.adrian.ecommerceproject.service.FileUploadService;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/organic_shop")
public class AppController implements Serializable {
	@Value("${adrian.upload-dir}")
	String FILE_DIRECTORY;	

	@Autowired
	ImageUploadRepository imageUploadRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productReposiotry;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

	@Autowired
	FileUploadService fileUploadService;

	@PostMapping("/user/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(
							 signUpRequest.getUsername(),
							 encoder.encode(signUpRequest.getPassword()),
							 signUpRequest.getFirstName(),
							 signUpRequest.getLastName(),
							 signUpRequest.getPhone(),
							 signUpRequest.getEmail(),
							 signUpRequest.getAddress(),
							 signUpRequest.getCity(),
							 signUpRequest.getPostalCode()
							 );

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "super":
					Role superRole = roleRepository.findByName(ERole.ROLE_SUPER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(superRole);

					break;
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("Record registered successfully!"));
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/admin/add/role")
	@PreAuthorize("hasAnyRole('ROLE_SUPER', 'ROLE_ADMIN')")
	public ResponseEntity<?> addRole(@Valid @RequestBody Role role){		
		roleRepository.save(role);
		return ResponseEntity.ok(new MessageResponse(role.getName()+" Added Successfully"));
	}

	@PostMapping("/admin/add/category")
	@PreAuthorize("hasAnyRole('ROLE_SUPER', 'ROLE_ADMIN')")
	public ResponseEntity<?> addCategory(@Valid @RequestBody Category category){
		categoryRepository.save(category);
		return ResponseEntity.ok(new MessageResponse(category.getName() + " Category Added Succesfully"));
	}

	@PostMapping(value = "/admin/add/product", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	@PreAuthorize("hasAnyRole('ROLE_SUPER', 'ROLE_ADMIN')")
	public ResponseEntity<?> addProduct(@Valid @RequestPart("image") MultipartFile image, @RequestPart("product") ProductRequest product){
		
		//String pathDirectory = "C:\\Users\\A238545\\ecommerce\\project\\ecommerce-project\\ecommerce-product-images\\product_";
		String pathDirectory = "C:\\Users\\A238545\\ecommerce\\project\\ecommerce-project-ui\\src\\assets\\images\\product_";

		//Create New Product
		Product newProduct = new Product(
			product.getName(),
			product.getDescription(),
			product.getPrice()
		);
		
		try {
			byte[] data = image.getBytes();
			Path path = Paths.get(pathDirectory + image.getOriginalFilename());
			Files.write(path, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String imageProductName = image.getOriginalFilename();

		Set<Category> newCategory = new HashSet<>();
		Set<String> categoryName = product.getCategory();

		categoryName.forEach( category -> {
			if(category.equalsIgnoreCase(ECategories.VEGETABLES.name())){
				Category vegetables = categoryRepository.findByName(ECategories.VEGETABLES)
				.orElseThrow( ()-> new RuntimeException("Error finding Category") );
				newCategory.add(vegetables);
			}
			if(category.equalsIgnoreCase(ECategories.FRUITS.name())){
				Category fruits = categoryRepository.findByName(ECategories.FRUITS)
				.orElseThrow( ()-> new RuntimeException("Error finding Category") );
				newCategory.add(fruits);
			}
			if(category.equalsIgnoreCase(ECategories.JUICE.name())){
				Category juice = categoryRepository.findByName(ECategories.JUICE)
				.orElseThrow( ()-> new RuntimeException("Error finding Category") );
				newCategory.add(juice);
			}
			if(category.equalsIgnoreCase(ECategories.HERBS.name())){
				Category herbs = categoryRepository.findByName(ECategories.HERBS)
				.orElseThrow( ()-> new RuntimeException("Error finding Category") );
				newCategory.add(herbs);
			}			
		});
		newProduct.setCategory(newCategory);
		newProduct.setImageUrl(imageProductName);
		productReposiotry.save(newProduct);
		return ResponseEntity.ok(new MessageResponse("Product Details successfully added"));
	}


	/** Editing */
	@PutMapping("/admin/edit/product/{id}")
	@PreAuthorize("hasAnyRole('ROLE_SUPER', 'ROLE_ADMIN')")
	public ResponseEntity<?> editProduct(@Valid @PathVariable("id") long id, @RequestBody ProductRequest productRequest){
		Optional<Product> productData = productReposiotry.findById(id);
		if(productData.isPresent()){
			Product _product = productData.get();
			_product.setName(productRequest.getName());
			_product.setDescription(productRequest.getDescription());
			_product.setPrice(productRequest.getPrice());
			return new ResponseEntity<>(productReposiotry.save(_product), HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/admin/edit/product/image/{id}")
	public ResponseEntity<?> ediptProductImage(@PathVariable("id") long id, @RequestParam("image") MultipartFile multipartFile){
		//String pathDirectory = "C:\\Users\\A238545\\ecommerce\\project\\ecommerce-project\\ecommerce-product-images\\product_";
		String pathDirectory = "C:\\Users\\A238545\\ecommerce\\project\\ecommerce-project-ui\\src\\assets\\images\\product_";
		Optional<Product> productDataImage = productReposiotry.findById(id);

		String imageName = multipartFile.getOriginalFilename();

		try {
			byte[] data = multipartFile.getBytes();
			Path path = Paths.get(pathDirectory + multipartFile.getOriginalFilename());
			Files.write(path, data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(productDataImage.isPresent()){
			Product _product = productDataImage.get();
			_product.setImageUrl(imageName);
			return new ResponseEntity<>(productReposiotry.save(_product), HttpStatus.OK);
		}else{
			return ResponseEntity.ok(new MessageResponse("Product not found"));
		}	
	}












	//Save to local file system
	@PostMapping("/admin/add/product/image")
	public void uploadLocal(@RequestParam("file") MultipartFile multipartFile){
		fileUploadService.uploadToLocal(multipartFile);
	}

	@PostMapping("admin/move/image")
	public void moveImage(@RequestParam("image") MultipartFile multipartFile){
		String directoryPath = "C:\\Users\\A238545\\Desktop\\pathimages\\newImage_";
	try{
		byte[] data = multipartFile.getBytes();
		Path path = Paths.get(directoryPath + multipartFile.getOriginalFilename());
		Files.write(path, data);
	} catch (IOException e) {
		e.printStackTrace();
	}
	}

	@PostMapping("move/image")
	public ResponseEntity<?> fileName(@RequestParam("image") MultipartFile multipartFile, @RequestPart("product") ProductRequest product){
		String directoryPath = "C:\\Users\\A238545\\Desktop\\pathimages\\";

		//Create New Product
		Product newProduct = new Product(
			product.getName(),
			product.getImageUrl()
		);

		try{
			byte[] data = multipartFile.getBytes();
			Path path = Paths.get(directoryPath + multipartFile.getOriginalFilename());
			Files.write(path, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileName = multipartFile.getOriginalFilename();
		newProduct.setImageUrl(fileName);
		productReposiotry.save(newProduct);
		return ResponseEntity.ok(new MessageResponse("Product Successfully Saved"));
	}

	//Save to DB
	public void uploadDb(@RequestParam("file") MultipartFile multipartFile){
		//
	}
	
}
