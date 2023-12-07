package com.talentstream.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.talentstream.exception.CustomException;
import com.talentstream.dto.JobRecruiterDTO;
import com.talentstream.entity.JobRecruiter;
import com.talentstream.repository.JobRecruiterRepository;
import com.talentstream.repository.RegisterRepository;

@Service
public class JobRecruiterService {
	 
	    private PasswordEncoder passwordEncoder;	    
	   @Autowired
        JobRecruiterRepository recruiterRepository;
	   @Autowired
	   RegisterRepository applicantRepository;

	   public JobRecruiterService(JobRecruiterRepository recruiterRepository, PasswordEncoder passwordEncoder) {
	        this.recruiterRepository = recruiterRepository;
	        this.passwordEncoder = passwordEncoder;
	    }

	    public ResponseEntity<String> saveRecruiter(JobRecruiter recruiter) {
	    	try {
	           
	            if (recruiterRepository.existsByEmail(recruiter.getEmail()) || applicantRepository.existsByEmail(recruiter.getEmail())) {
	                throw new CustomException("Failed to register/Email already exists", HttpStatus.BAD_REQUEST);
	            }
	            recruiter.setPassword(passwordEncoder.encode(recruiter.getPassword()));
	            recruiterRepository.save(recruiter);
	            return ResponseEntity.ok("Recruiter registered successfully");
	        } catch (CustomException e) {
	            throw e;
	        } catch (Exception e) {
	            throw new CustomException("Error registering recruiter", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
    public JobRecruiter login(String email, String password) {
    	 JobRecruiter recruiter = recruiterRepository.findByEmail(email);

         if (recruiter != null && passwordEncoder.matches(password, recruiter.getPassword())) {
             return recruiter; 
         } else {
             throw new CustomException("Login failed", HttpStatus.UNAUTHORIZED);
         }
    }
    public JobRecruiter findById(Long id) {
    	return recruiterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobRecruiter not found for ID: " + id));
        
    }
    public List<JobRecruiterDTO> getAllJobRecruiters() {
        try {
            List<JobRecruiter> jobRecruiters = recruiterRepository.findAll();
            return jobRecruiters.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("Error retrieving job recruiters", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    public void updatePassword(String userEmail, String newPassword) {
        JobRecruiter jobRecruiter = recruiterRepository.findByEmail(userEmail);
        if (jobRecruiter != null) {
            jobRecruiter.setPassword(newPassword);
            recruiterRepository.save(jobRecruiter);
        } else {
            throw new EntityNotFoundException("JobRecruiter not found for email: " + userEmail);
        }
    }

	public JobRecruiter findByEmail(String userEmail) {
			return recruiterRepository.findByEmail(userEmail);
	}

	public void addRecruiter(JobRecruiter jobRecruiter) {
		recruiterRepository.save(jobRecruiter);
			}
	
	
	private JobRecruiterDTO convertToDTO(JobRecruiter recruiter) {
        JobRecruiterDTO recruiterDTO = new JobRecruiterDTO();
        recruiterDTO.setRecruiterId(recruiter.getRecruiterId());
        recruiterDTO.setCompanyname(recruiter.getCompanyname());
        recruiterDTO.setMobilenumber(recruiter.getMobilenumber());
        recruiterDTO.setEmail(recruiter.getEmail());
        recruiterDTO.setPassword(recruiter.getPassword());
        recruiterDTO.setRoles(recruiter.getRoles());

        return recruiterDTO;
    }
	}
    

 

	
