package com.bornfire.xbrl.services;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;



@Service
@Component
public class EmailServices {

	@Autowired
	Environment env;
	
	private static final Logger logger = LoggerFactory.getLogger(EmailServices.class);


public void sendEmail(String filename) {
	String alertcode = "LIST_SCHED";


		logger.info("EMAIL STARTS");
		String nextPage = "";
		String status;

		String host = env.getProperty("mail.host");
		String user = env.getProperty("mail.username");// change accordingly
		String password = env.getProperty("mail.password");// change accordingly
		String port = env.getProperty("mail.port");

					Properties props = new Properties();
					props.put("mail.smtp.auth", "true");
					// props   .put("mail.smtp.starttls.enable", "true");
					props.put("mail.smtp.host", host);
					props.put("mail.smtp.port", port);

					Session session = Session.getInstance(props, new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(user, password);
						}
					});
					System.out.println("SEND MAIL...");
					try {
						MimeMessage msg = new MimeMessage(session);
						msg.setFrom(new InternetAddress(user));

						msg.addRecipient(Message.RecipientType.TO, new InternetAddress("brbs@bornfire"));
					//	msg.addRecipient(Message.RecipientType.CC, new InternetAddress("tr.ramkumar@bornfire.in"));
						
						//	msg.addRecipient(Message.RecipientType.CC, new InternetAddress("kalidass.k@bornfire.in"));
						

						msg.setSentDate(new Date());
						msg.setSubject("Account Statement Testing");

						   BodyPart messageBodyPart1 = new MimeBodyPart();  
						    messageBodyPart1.setText("Please do find the attached files for Requested Statement");  
						      
//						  MimeBodyPart messageBodyPart2 = new MimeBodyPart();  
//						  String path_xlsx =  env.getProperty("output.exportpath");
//						  path_xlsx+=filename;
//						    DataSource source = new FileDataSource(path_xlsx);  
//						    messageBodyPart2.setDataHandler(new DataHandler(source));  
//						    messageBodyPart2.setFileName(filename);  
						    
						    MimeBodyPart messageBodyPart3 = new MimeBodyPart();  
							  String path_xlsx_new =  env.getProperty("output.exportpath");
							  path_xlsx_new ="C:\\Users\\91902\\Downloads\\";
							  path_xlsx_new+=filename;
							    DataSource source2 = new FileDataSource(path_xlsx_new);  
							    messageBodyPart3.setDataHandler(new DataHandler(source2));  
							    messageBodyPart3.setFileName(filename);  
							    
//							    
//							    MimeBodyPart messageBodyPart4 = new MimeBodyPart();  
//								  String path_xlsx_cust =  env.getProperty("output.exportpath");
//								  path_xlsx_cust+=filenamecust;
//								    DataSource source3 = new FileDataSource(path_xlsx_cust);  
//								    messageBodyPart4.setDataHandler(new DataHandler(source3));  
//								    messageBodyPart4.setFileName(filenamecust);  
						    
//							  MimeBodyPart messageBodyPart3 = new MimeBodyPart();  
//							String path_pdf =  env.getProperty("output.exportpath");
//							path_pdf+=filename;
//							 DataSource source2 = new FileDataSource(path_pdf);  
//							 messageBodyPart3.setDataHandler(new DataHandler(source2));  
//							 messageBodyPart3.setFileName(path_pdf); 
						     
						     
						    Multipart multipart = new MimeMultipart();  
						    multipart.addBodyPart(messageBodyPart1);  
						    //commented the first attachment for consolidated file .
//						    multipart.addBodyPart(messageBodyPart2);  
						    multipart.addBodyPart(messageBodyPart3);  
						   // multipart.addBodyPart(messageBodyPart4);  
						    msg.setContent(multipart);  

						
						Transport.send(msg);

						nextPage = "success";

					} catch (Exception E) {
						nextPage = "error";
					}
					if (nextPage.equals("success")) {
//						BigDecimal srl = cmnVal.getEmail_srl_no();
//						EmailAlert cv = emailRep.getEmailbySRl(srl);
//
//						cv.setSend_flg("Y");
//						cv.setMsg_status("Delivered Successfully");
//
//						emailRep.save(cv);
					}
		
	}
}


