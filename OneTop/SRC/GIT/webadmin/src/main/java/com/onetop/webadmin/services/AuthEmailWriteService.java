package com.onetop.webadmin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import static com.onetop.webadmin.common.CommonUtils.createUserPassword;

import java.util.Date;
import java.util.Random;

import javax.annotation.PostConstruct;

import com.onetop.webadmin.mappers.MemberDao;
import com.onetop.webadmin.models.member.AuthEmailMember;
import com.onetop.webadmin.models.member.WebMember;
import com.onetop.webadmin.util.PasswordUtil;

@Service
public class AuthEmailWriteService {


    @Autowired
    MemberDao memberDao;

    @Autowired
    WebMemberListService webMemberListService;

    @Value("${aws-email.accessKey}")
	private String AWS_EMAIL_ACCESS;
	
	@Value("${aws-email.secretKey}")
	private String AWS_EMAIL_SECRET;

	@Value("${aws-email.region}")
	private String AWS_EMAIL_REGION;

    @Value("${aws-email.master}")
	private String EMAIL_MASTER;

    BasicAWSCredentials awsEmailCred;	
	AmazonSimpleEmailService emailClient;

    @PostConstruct
	public void setCredentials() {
		awsEmailCred = new BasicAWSCredentials(AWS_EMAIL_ACCESS, AWS_EMAIL_SECRET);
		emailClient = AmazonSimpleEmailServiceClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsEmailCred))
				.withRegion(AWS_EMAIL_REGION)
				.build();		
	}

    public void authEmailWrite(String mb_id, int autu_type) throws Exception {

        WebMember webMember = webMemberListService.GetWebMember(mb_id);

        AuthEmailMember authEmailMember = new AuthEmailMember();

        String auth_key = createUserPassword(mb_id, new Date().toString());
        String auth_key2 = createUserPassword(auth_key, new Date().toString());

        authEmailMember.setMa_idx_user(webMember.getMb_no());
        authEmailMember.setMb_id(mb_id);
        authEmailMember.setAuthType(Integer.toString(autu_type));
        authEmailMember.setEmailState("0");
        authEmailMember.setEAddress(webMember.getMb_email());
        authEmailMember.setAuthKey(auth_key+auth_key2);
        memberDao.insertAuthEmail(authEmailMember);
    }

    private Content createContent(String text) {
        return new Content()
                .withCharset("UTF-8")
                .withData(text);
    }

    public boolean sendPasswordReset(String mb_id) throws Exception {
        WebMember member = webMemberListService.GetWebMember(mb_id);
        // 변경된 비번 EMAIL로 보내주기		
		String pass = String.format("%06d", new Random().nextInt(999999)); 		
		String mbKey = PasswordUtil.createUserMbKey(member.getMb_id(), pass);
        String newInsertPwd = PasswordUtil.createUserPassword(member.getMb_id(), pass);

        int result = memberDao.updatePassword(newInsertPwd, mbKey, member.getMb_no());

        if( result < 1) {
            // 패스워드 업데이트 실패!!
            return false;
		}
		
		String emailSubject = "[OneTop] Password Reset";
        String emailContent = "Hello, " + member.getMb_email() + "!<br/>" +						
						"OneTop new temporary password : <br/>" +
						"<strong>" + pass +"</strong><br/>" +
						"Please change your password after log in.";

		Destination destination = new Destination()
                .withToAddresses(member.getMb_email());

        Message message = new Message()
                .withSubject(createContent(emailSubject))
                .withBody(new Body()
                        .withHtml(createContent(emailContent))); // content body는 HTML 형식으로 보내기 때문에 withHtml을 사용합니다.

		SendEmailRequest sendRequest = new SendEmailRequest()
							.withSource(EMAIL_MASTER)
							.withDestination(destination)
							.withMessage(message);
		
		// Send the email.
		SendEmailResult emailResult = emailClient.sendEmail(sendRequest);	
		if (emailResult.getMessageId() == null )		{
			return false;
        }
        return true;
    }
}