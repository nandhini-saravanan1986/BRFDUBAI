package com.bornfire.xbrl.services;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.bornfire.xbrl.config.SequenceGenerator;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.BRBS.MANUAL_Service_Entity;
import com.bornfire.xbrl.entities.BRBS.MANUAL_Service_Rep;

@Service
public class AuditService {

	private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpSession session;

	@Autowired
	private MANUAL_Service_Rep mANUAL_Service_Rep;

	@Autowired
	private UserProfileRep userProfileRep;

	@Autowired
	private SequenceGenerator sequence;

	@Autowired
	private RepositoryResolver repositoryResolver;

	@PersistenceContext
	private EntityManager entityManager;

	// ---------- FILE DOWNLOAD AUDIT ----------
	public void saveCommonAudit(String reportId, String fileType) {
		try {
			String userId = (String) request.getSession().getAttribute("USERID");
			String username = (String) request.getSession().getAttribute("USERNAME");

			if (userId == null || username == null) {
				logger.warn("Session user not found. Skipping audit.");
				return;
			}

			String auditID = sequence.generateRequestUUId();

			MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(userId);
			audit.setEvent_id(userId);
			audit.setEvent_name(username);

			// Set table name based on file type
			if ("detailexcel".equalsIgnoreCase(fileType)) {
				audit.setAudit_table(reportId + "_DETAILTABLE");
			} else {
				audit.setAudit_table(reportId + "_SUMMARYTABLE");
			}

			// Set audit function code and remarks
			switch (fileType.toLowerCase()) {
			case "excel":
				audit.setFunc_code("DOWNLOAD_EXCEL");
				audit.setAudit_screen("Download Excel");
				audit.setRemarks(reportId + " Excel downloaded successfully.");
				break;
			case "detailexcel":
				audit.setFunc_code("DOWNLOAD_DETAILEXCEL");
				audit.setAudit_screen("Download Detail Excel");
				audit.setRemarks(reportId + " Detail Excel downloaded successfully.");
				break;
			case "pdf":
				audit.setFunc_code("DOWNLOAD_PDF");
				audit.setAudit_screen("Download PDF");
				audit.setRemarks(reportId + " PDF downloaded successfully.");
				break;
			default:
				audit.setFunc_code("DOWNLOAD_FILE");
				audit.setAudit_screen("Download");
				audit.setRemarks(reportId + " File downloaded successfully.");
				break;
			}

			// Add user profile info if available
			UserProfile userProfile = userProfileRep.getRole(userId);
			if (userProfile != null) {
				audit.setAuth_user(userProfile.getAuth_user());
				audit.setAuth_time(userProfile.getAuth_time());
			}

			audit.setAudit_ref_no(auditID);

			mANUAL_Service_Rep.save(audit);
			logger.info("Saved file download audit for {} by {}", reportId, userId);

		} catch (Exception e) {
			logger.error("Error in saveCommonAudit()", e);
		}
	}

	// ---------- ENTITY FIELD CHANGE AUDIT ----------
	@Transactional
	public void auditChanges(Object newEntity,String reason) {
		System.out.println("Reaon in service : "+reason);
	    if (newEntity == null) return;

	    Class<?> entityClass = newEntity.getClass();

	    // Resolve repository for this entity class
	    JpaRepository<Object, Object> repository = repositoryResolver.resolveRepository(entityClass);
	    if (repository == null) {
	        logger.warn("Repository not found for entity class: {}", entityClass.getName());
	        return;
	    }

	    // Get primary key value
	    Object primaryKey = entityManager.getEntityManagerFactory()
	        .getPersistenceUnitUtil().getIdentifier(newEntity);
	    //-----------------------------
	    System.out.println("Primary Key : "+primaryKey);
	    
	    EntityType<?> entityType = entityManager.getMetamodel().entity(newEntity.getClass());
	    List<String> idNames = new ArrayList<>();

	    if (entityType.hasSingleIdAttribute()) {
	        // Single @Id
	        for (SingularAttribute<?, ?> attribute : entityType.getSingularAttributes()) {
	            if (attribute.isId()) {
	                idNames.add(attribute.getName());
	                break;
	            }
	        }
	    } else {
	    	// composite @Idclass
	        for (Object attrObj : entityType.getIdClassAttributes()) {
	            SingularAttribute<?, ?> attribute = (SingularAttribute<?, ?>) attrObj;
	            idNames.add(attribute.getName());
	        }
	    }

	    // Get the ID value
	    Object idValue = entityManager.getEntityManagerFactory()
	    	    .getPersistenceUnitUtil().getIdentifier(newEntity);

	    	System.out.println("--- Extracting Composite Key Values ---");

	    	String formattedIdString = "";
	    	
	    	if (idValue != null) {
		    	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	    	    if (entityType.hasSingleIdAttribute()) {
	    	        // Single Primary Key
	    	        String singleIdName = idNames.get(0);
	    	        String displayName=singleIdName.replace('_', ' ');
                	displayName=displayName.substring(0,1).toUpperCase()+displayName.substring(1);
	    	        String displayValue = (idValue instanceof Date) ? sdf.format((Date) idValue) : idValue.toString();
	    	        formattedIdString = displayName + " : " + displayValue;
	    	        
	    	    } else {
	    	        //Composite Primary Key
	    	        StringJoiner joiner = new StringJoiner(";");
	    	        Field[] fields = idValue.getClass().getDeclaredFields();
	    	        
	    	        for (Field field : fields) {
	    	            field.setAccessible(true);
	    	            try {
	    	                String fieldName = field.getName();
	    	                Object fieldValue = field.get(idValue);	    	                
	    	                if (!fieldName.equals("serialVersionUID")) {
	    	                	String displayName=fieldName.replace('_', ' ');
	    	                	displayName=displayName.substring(0,1).toUpperCase()+displayName.substring(1);
								Object displayValue = (fieldValue instanceof Date) ? sdf.format((Date) fieldValue) : fieldValue;
	    	                    joiner.add(displayName + " : " + displayValue);
	    	                }
	    	            } catch (IllegalAccessException e) {
	    	                System.err.println("Could not read field: " + field.getName());
	    	            }
	    	        }
	    	        formattedIdString = joiner.toString();
	    	    }
	    	}

	    	System.out.println("ID values : " + formattedIdString);
	    	
	 //-------------------------------

	    if (primaryKey == null) {
	        logger.warn("Primary key not found for entity: {}", entityClass.getSimpleName());
	        return;
	    }

	    // Load old entity from DB
	    Object oldEntity = repository.findById(primaryKey).orElse(null);
	    if (oldEntity == null) {
	        logger.warn("No existing record found for audit: {}", primaryKey);
	        return;
	    }

	    // Merge null fields in newEntity from oldEntity to prevent null changes
	    mergeNullFields(newEntity, oldEntity);

	    // Prepare lists to store changed fields info
	    List<String> fieldNames = new ArrayList<>();
	    List<String> oldValues = new ArrayList<>();
	    List<String> newValues = new ArrayList<>();
		Set<String> ignoreFields = new HashSet<>(Arrays.asList("report_date"));

	    // Compare field-by-field
	    for (Field field : entityClass.getDeclaredFields()) {
	        field.setAccessible(true);
	        if (ignoreFields.contains(field.getName())) {
	            continue;
	        }

	        try {
	            Object oldValue = field.get(oldEntity);
	            Object newValue = field.get(newEntity);
				//System.out.println("Before Field: " + field.getName() + " Old: " + oldValue + " New: " + newValue + " Equals: " + Objects.equals(oldValue, newValue));

	            String formattedOld = (oldValue == null || oldValue.toString().trim().isEmpty()) ? null : oldValue.toString();
	            String formattedNew = (newValue == null || newValue.toString().trim().isEmpty()) ? null : newValue.toString();
	            
				//System.out.println("After Field: " + field.getName() + " Old: " + formattedOld + " New: " + formattedNew+ " Equals: " + Objects.equals(formattedOld, formattedNew));
	            
	            if (!Objects.equals(formattedOld, formattedNew) && !formattedOld.equals(formattedNew)) {
	                fieldNames.add(field.getName());
	                oldValues.add(formattedOld != null  ? formattedOld.toString() : "null");
	                newValues.add(formattedNew != null ? formattedNew.toString() : "null");
	            }
	        } catch (IllegalAccessException e) {
	            logger.error("Unable to access field: " + field.getName(), e);
	        }
	    }

	    // If no changes, skip audit
	    if (fieldNames.isEmpty()) {
	        logger.debug("No changes detected for {}", entityClass.getSimpleName());
	        return;
	    }

	    // Get session user info
	    String userId = (String) request.getSession().getAttribute("USERID");
	    String username = (String) request.getSession().getAttribute("USERNAME");
	    if (userId == null || username == null) {
	        logger.warn("Session user not found. Skipping audit.");
	        return;
	    }

	    String auditID = sequence.generateRequestUUId();
	    UserProfile userProfile = userProfileRep.getRole(userId);

	    // Get actual DB table name from @Table or fallback to entity name
	    String tableName = entityClass.isAnnotationPresent(Table.class)
	        ? entityClass.getAnnotation(Table.class).name()
	        : entityClass.getSimpleName();

	    // Build and save audit entity
	    MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
	    audit.setAudit_ref_no(auditID);
	    audit.setAudit_date(new Date());
	    audit.setEntry_time(new Date());
	    audit.setEntry_user(userId);
	    audit.setFunc_code("EDIT");
	    audit.setAudit_table(tableName);
	    audit.setAudit_screen("Edit");
	    audit.setEvent_id(primaryKey.toString());
	    audit.setEvent_name(username);
	    audit.setRemarks("Edit Successfully");
	    
	    audit.setID_VALUES(formattedIdString);
	    audit.setREASON(reason);

	    audit.setField_name(String.join("; ", fieldNames));
	    audit.setOld_value(String.join("; ", oldValues));
	    audit.setNew_value(String.join("; ", newValues));

	    if (userProfile != null) {
	        audit.setAuth_user(userProfile.getAuth_user());
	        audit.setAuth_time(userProfile.getAuth_time());
	    }

	    mANUAL_Service_Rep.save(audit);
	    logger.info("✅ Audit saved for table {} with changes in fields: {}", tableName, fieldNames);
	}

	// Utility to copy non-null fields from oldEntity to newEntity if newEntity fields are null
	private void mergeNullFields(Object target, Object source) {
	    Class<?> clazz = target.getClass();
	    for (Field field : clazz.getDeclaredFields()) {
	        field.setAccessible(true);
	        try {
	            Object targetValue = field.get(target);
	            Object sourceValue = field.get(source);
	            if (targetValue == null && sourceValue != null) {
	                field.set(target, sourceValue);
	            }
	        } catch (IllegalAccessException e) {
	            logger.error("Unable to merge field: " + field.getName(), e);
	        }
	    }
	}	    

}
