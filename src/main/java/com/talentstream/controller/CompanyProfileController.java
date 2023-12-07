package com.talentstream.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; 
import com.talentstream.dto.CompanyProfileDTO;
import com.talentstream.service.CompanyProfileService;
import java.util.Optional;
import com.talentstream.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/companyprofile")
public class CompanyProfileController {
	private static final Logger logger = LoggerFactory.getLogger(ApplicantProfileController.class);
	 @Autowired
	    private final CompanyProfileService companyProfileService;

	 
	    @Autowired
	    public CompanyProfileController(CompanyProfileService companyProfileService) {
	        this.companyProfileService = companyProfileService;
	    }
	 
	    
	    @PostMapping(value="/recruiters/company-profiles/{jobRecruiterId}")
	    public ResponseEntity<String> createCompanyProfile(@RequestBody CompanyProfileDTO companyProfileDTO,@PathVariable Long jobRecruiterId) {
	       	try {
	           companyProfileService.saveCompanyProfile(companyProfileDTO,jobRecruiterId);
	           return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"CompanyProfile saved successfully.\"}");
	        } catch (CustomException ce) {
	            return ResponseEntity.status(ce.getStatus()).body(ce.getMessage());
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred.");
	        }
	    }
	 
	 
	    @GetMapping("/recruiters/getCompanyProfile/{id}")
	    public ResponseEntity<CompanyProfileDTO> getCompanyProfileById(@PathVariable Long id) {
	    	try {
	            Optional<CompanyProfileDTO> companyProfileDTO = companyProfileService.getCompanyProfileById(id);
	            return companyProfileDTO.map(profile -> new ResponseEntity<>(profile, HttpStatus.OK))
	                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	        } catch (CustomException ce) {
	            return ResponseEntity.status(ce.getStatus()).body(null);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }

    
}

