package com.axelor.apps.talent.service;

import java.io.IOException;
import java.util.List;

import com.axelor.apps.base.db.AppRecruitment;
import com.axelor.apps.base.db.repo.AppRecruitmentRepository;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.talent.db.JobApplication;
import com.axelor.apps.talent.db.JobPosition;
import com.axelor.apps.talent.db.repo.JobApplicationRepository;
import com.axelor.apps.talent.db.repo.JobPositionRepository;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaAttachment;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaAttachmentRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class JobPositionServiceImpl implements JobPositionService {
	
	@Inject
	private JobPositionRepository jobPositionRepo;
	
	@Inject
	private JobApplicationRepository jobApplicationRepo;
	
	@Inject
	private AppRecruitmentRepository appRecruitmentRepo;
	
	@Inject
	private MetaFiles metaFiles;
	
	@Inject
	private MetaAttachmentRepository metaAttachmentRepo;
	
	@Override
	public void createJobApplication(Message message) {
		
		List<JobPosition> jobPositions = jobPositionRepo.all().filter("self.mailAccount = ?1", message.getMailAccount()).fetch();
		
		if (jobPositions.isEmpty()) {
			return;
		}
		
		if (jobPositions.size() > 1) {
			boolean positionFound = false;
			for (JobPosition position : jobPositions) {
				if (position.getJobReference() != null && message.getSubject().contains(position.getJobReference())) {
					createApplication(position, message);
					positionFound = true;
				}
			}
			if (!positionFound) {
				createApplication(null, message);
			}
		}
		else {
			createApplication(jobPositions.get(0), message);
		}
		
		updateLastEmailId(message);
	}
	
	private void createApplication(JobPosition position, Message message) {
		
		JobApplication application = new JobApplication();
		
		if (position != null) {
			application.setJobPosition(position);
			application.setResponsible(position.getEmployee());
		}
		
		application.setEmailAddress(message.getFromEmailAddress());
		application.setDescription(message.getContent());
		
		application = saveApplication(application);
		
		try {
			copyAttachments(application, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public JobApplication saveApplication(JobApplication application) {
		if (application != null) {
			application = jobApplicationRepo.save(application);
		}
		
		return application;
	}
	
	@Transactional
	public void updateLastEmailId(Message message) {
		
		AppRecruitment appRecruitment = appRecruitmentRepo.all().fetchOne();
		appRecruitment.setLastEmailId(message.getId().toString());
		
		appRecruitmentRepo.save(appRecruitment);
	}
	
	private void copyAttachments(JobApplication application, Message message) throws IOException {
		
		List<MetaAttachment> attachments = metaAttachmentRepo.all()
				.filter("self.objectId = ?1 and self.objectName = ?2",
						message.getId(),
						Message.class.getName())
				.fetch();
		
		for (MetaAttachment attachment : attachments) {
			MetaFile file = attachment.getMetaFile();
			metaFiles.attach(file, file.getFileName(), application);
		}
	}
}
