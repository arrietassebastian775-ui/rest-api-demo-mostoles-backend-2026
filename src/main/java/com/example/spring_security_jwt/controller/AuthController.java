package com.example.spring_security_jwt.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_security_jwt.model.ERole;
import com.example.spring_security_jwt.model.Role;
import com.example.spring_security_jwt.model.User;
import com.example.spring_security_jwt.payload.request.LogginRequest;
import com.example.spring_security_jwt.payload.request.SignupRequest;
import com.example.spring_security_jwt.payload.response.JwtResponse;
import com.example.spring_security_jwt.payload.response.MessageResponse;
import com.example.spring_security_jwt.repository.RoleRepository;
import com.example.spring_security_jwt.repository.UserRepository;
import com.example.spring_security_jwt.security.jwt.JwtUtils;
import com.example.spring_security_jwt.security.service.UserDetailsImpl;
import com.example.spring_security_jwt.security.service.UserDetailsServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

		private final AuthenticationManager authenticationManager;
		private final UserRepository userRepository;
		private final RoleRepository roleRepository;
		private final PasswordEncoder encoder;
		private final JwtUtils jwtUtils;
		
		private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);


		@PostMapping("/signup")
		public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest,
				BindingResult validationResults) {
			
			if (userRepository.existsByUsername(signupRequest.getUsername())) {
				return ResponseEntity.badRequest()
				    .body(new MessageResponse("Error: Username is already taken"));
			}
			
			if (userRepository.existsByEmail(signupRequest.getEmail())) {
				return ResponseEntity.badRequest()
					.body(new MessageResponse("Error: Email is already in use!!!"));
			}
			
			/**
			 * Crear el usuario, es decir, el User para persistirlo en la tabla User,
			 * con las propiedades del JSON recibido en la peticion (signupRequest)
			 */
			
			User user = User.builder()
					.username(signupRequest.getUsername())
					.email(signupRequest.getEmail())
					.password(encoder.encode(signupRequest.getPassword()))
					.build();
			
			
			
			Set<String> strRoles = signupRequest.getRole();
			Set<Role> roles = new HashSet<>();
			
					
			if (strRoles == null) {
				Role userRole = roleRepository.findByName(ERole.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Error: Role not found"));
				
				roles.add(userRole);
			} else {
				
				strRoles.forEach(role -> {
			
					if ("admin".equals(role)) {
						 Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
									.orElseThrow(() -> new RuntimeException("Error: Role is not found "));
						 roles.add(adminRole);
					} else {
						
						Role userRole = roleRepository.findByName(ERole.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role not found"));
						  roles.add(userRole);
						
					}
//					switch (role) {
//						case "admin" : Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
//								.orElseThrow(() -> new RuntimeException("Error: Role is not found "));
//						      roles.add(adminRole);
//						      break;
//						default: Role userRole = roleRepository.findByName(ERole.ROLE_USER)
//									.orElseThrow(() -> new RuntimeException("Error: Role not found"));
//							  roles.add(userRole);
//							  break;
//						      
//					}
				});
				
				
				
				
			}
			
			user.setRoles(roles);
			userRepository.save(user);
			
			
			return ResponseEntity.ok(new MessageResponse("User registered successfully"));
			
		}
				

		/**
		 * Metodo que permite logearse a un usuario que se ha registrado previamente
		 */
		@SuppressWarnings("null")
		@PostMapping("/signin")
		public ResponseEntity<?> authenticateUser(@Valid @RequestBody LogginRequest logginRequest,
				BindingResult result) {
			
			Authentication authentication = authenticationManager 
					.authenticate(new UsernamePasswordAuthenticationToken(logginRequest.getUsername(), 
							logginRequest.getPassword()));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			String jwt = jwtUtils.generateJwtToken(authentication);
			
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			
			Set<String> roles = userDetails.getAuthorities().stream()
					.map(item -> item.getAuthority())
					.collect(Collectors.toSet());
			
			// Mostrar por la consola los roles del usuario
			LOGGER.info("Roles del usuario: {}", roles);
			
			return ResponseEntity.ok(new JwtResponse(
						jwt,
						userDetails.getId(),
						userDetails.getUsername(),
						userDetails.getEmail(),
						roles
						
					));
		}
}











