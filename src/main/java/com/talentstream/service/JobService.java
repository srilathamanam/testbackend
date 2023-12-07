package com.talentstream.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.talentstream.dto.JobDTO;
import com.talentstream.dto.RecuriterSkillsDTO;
import com.talentstream.entity.Job;
import com.talentstream.entity.JobRecruiter;
import com.talentstream.entity.JobSearchCriteria;
import com.talentstream.entity.JobSpecifications;
import com.talentstream.entity.RecuriterSkills;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.JobRecruiterRepository;
import com.talentstream.repository.JobRepository;
import com.talentstream.repository.RecuriterSkillsRepository;

@Service
public class JobService {
	JobRepository jobRepository;	   
	    private RecuriterSkillsRepository skillsRepository;
	    
	    @Autowired
	    JobRecruiterRepository jobRecruiterRepository;
	    
    @Autowired
    public JobService(JobRepository jobRepository, RecuriterSkillsRepository skillsRepository) {
        this.jobRepository = jobRepository;
        this.skillsRepository = skillsRepository;
    }

    public List<Job> searchJobs(JobSearchCriteria searchCriteria) {
    	 try {
             Page<Job> jobPage = jobRepository.findAll(
                     JobSpecifications.searchJobs(
                             searchCriteria.getSkillName(),
                             searchCriteria.getJobTitle(),
                             searchCriteria.getLocation(),
                             searchCriteria.getIndustryType(),
                             searchCriteria.getEmployeeType(),
                             searchCriteria.getMinimumQualification(),
                             searchCriteria.getSpecialization()
                     ),
                     Pageable.unpaged()
             );

             return jobPage.getContent();
         } catch (Exception e) {
             throw new CustomException("Error while searching jobs", HttpStatus.INTERNAL_SERVER_ERROR);
         }
    }


    public ResponseEntity<String> saveJob(JobDTO jobDTO, Long jobRecruiterId) {
    	 try {
             JobRecruiter jobRecruiter = jobRecruiterRepository.findByRecruiterId(jobRecruiterId);
             if (jobRecruiter != null) {
                 Job job = convertDTOToEntity(jobDTO);
                 job.setJobRecruiter(jobRecruiter);
                 jobRepository.save(job);
                 return ResponseEntity.status(HttpStatus.OK).body("Job saved successfully.");
             } else {
                 throw new CustomException("JobRecruiter with ID " + jobRecruiterId + " not found.", HttpStatus.NOT_FOUND);
             }
         } catch (CustomException ce) {
             throw ce;
         } catch (Exception e) {
             throw new CustomException("Error while saving job", HttpStatus.INTERNAL_SERVER_ERROR);
         }
}

    public Job getJobById(Long jobId) {
    	  try {
              return jobRepository.findById(jobId).orElse(null);
          } catch (Exception e) {
              throw new CustomException("Error while retrieving job by ID", HttpStatus.INTERNAL_SERVER_ERROR);
          }
    }
   
    public List<Job> getAllJobs() {
    	 try {
             return jobRepository.findAll();
         } catch (Exception e) {
             throw new CustomException("Error while retrieving all jobs", HttpStatus.INTERNAL_SERVER_ERROR);
         }
    }
    public List<Job> getJobsByRecruiter(Long jobRecruiterId) {
    	try {
            return jobRepository.findByJobRecruiterId(jobRecruiterId);
        } catch (Exception e) {
            throw new CustomException("Error while retrieving jobs by recruiter ID", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

	public long countJobsByRecruiterId(Long recruiterId) {
		 try {
	            return jobRepository.countJobsByRecruiterId(recruiterId);
	        } catch (Exception e) {
	            throw new CustomException("Error while counting jobs by recruiter ID", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	private Job convertDTOToEntity(JobDTO jobDTO) {
        Job job = new Job();       
        job.setJobTitle(jobDTO.getJobTitle());
        job.setMinimumExperience(jobDTO.getMinimumExperience());
        job.setMaximumExperience(jobDTO.getMaximumExperience());
        job.setMinSalary(jobDTO.getMinSalary());
        job.setMaxSalary(jobDTO.getMaxSalary());
        job.setLocation(jobDTO.getLocation());
        job.setEmployeeType(jobDTO.getEmployeeType());
        job.setIndustryType(jobDTO.getIndustryType());
        job.setMinimumQualification(jobDTO.getMinimumQualification());
        job.setSpecialization(jobDTO.getSpecialization());
        job.setSkillsRequired(convertSkillsDTOToEntity(jobDTO.getSkillsRequired()));
        job.setJobHighlights(jobDTO.getJobHighlights());
        job.setDescription(jobDTO.getDescription());
      //  job.setUploadDocument(Base64.getDecoder().decode(jobDTO.getUploadDocument())); // Decode base64 string

        return job;
    }
	
	private Set<RecuriterSkills> convertSkillsDTOToEntity(Set<RecuriterSkillsDTO> skillsRequired) {
		 return skillsRequired.stream()
		            .map(skillDTO -> {
		                RecuriterSkills skill = new RecuriterSkills();
		                skill.setSkillName(skillDTO.getSkillName());
		                skill.setMinimumExperience(skillDTO.getMinimumExperience());
		                return skill;
		            })
		            .collect(Collectors.toSet());
		}
	
}