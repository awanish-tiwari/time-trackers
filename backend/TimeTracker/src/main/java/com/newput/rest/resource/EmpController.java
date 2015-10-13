package com.newput.rest.resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.newput.domain.Employee;
import com.newput.service.EmpService;
import com.newput.service.LoginService;
import com.newput.service.TSchedualService;
import com.newput.utility.JsonResService;
import com.newput.utility.ReqParseService;
import com.newput.utility.TTUtil;
import com.newput.utility.VerificationMailSend;

/**
 * Description : Use as a controller class to pass control on the services
 * 
 * @author Newput
 * 
 */
@Controller
@Path("/employee")
public class EmpController {

	@Autowired
	private TSchedualService timeSchedual;

	@Autowired
	private JsonResService jsonResService;

	@Autowired
	private EmpService empService;

	@Autowired
	private Employee emp;

	@Autowired
	private VerificationMailSend emailSend;

	@Autowired
	private ReqParseService reqParser;

	@Autowired
	private LoginService loginService;

	@Autowired
	private TTUtil util;

	/**
	 * @POST Description : Use to add new user into the system and send the
	 *       validation email to the registered mail id
	 *       {@link VerificationMailSend}
	 */
	@Path("/register")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject registerUser(@FormParam("firstName") String firstName, @FormParam("lastName") String lastName,
			@FormParam("email") String email, @FormParam("dob") String dob, @FormParam("doj") String doj,
			@FormParam("address") String address, @FormParam("contact") String contact,
			@FormParam("gender") String gender, @FormParam("password") String password) {

		String token = emailSend.generateRandomString();
		reqParser.setEmployeeValue(firstName, lastName, email, dob, doj, address, contact, gender, password, token);
		empService.addUser(emp);
		if (jsonResService.isSuccess()) {
			emailSend.sendMail();
		}
		return jsonResService.responseSender();
	}

	@Path("/verify")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject mailVerification(@FormParam("email") String emailId, @FormParam("token") String token) {
		if (emailId != null && !emailId.equalsIgnoreCase("")) {
			if (token != null && !token.equalsIgnoreCase("")) {
				reqParser.setValidationValue(emailId, token);
				empService.mailVerify(emp);
			} else {
				jsonResService.errorResponse("token can not be blank");
			}
		} else {
			jsonResService.errorResponse("Mail id can not be null");
		}
		return jsonResService.responseSender();
	}

	@Path("/login")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject login(@FormParam("email") String email, @FormParam("password") String password) {
		if (email != null && !email.equalsIgnoreCase("") && util.mailFormat(email)) {
			if (password != null && !password.equalsIgnoreCase("")) {
				reqParser.setSessionValue(email, password, "");
				loginService.createSession(emp);
			} else {
				jsonResService.errorResponse("password can not be blank");
			}
		} else {
			jsonResService.errorResponse("Mail id can not be null and in proper format");
		}
		return jsonResService.responseSender();
	}

	public void forgotPwd(@FormParam("email") String email) {

	}

	@Path("/timeEntry")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject timeEntry(@FormParam("lunchIn") String lunchIn, @FormParam("in") String in,
			@FormParam("out") String out, @FormParam("workDate") String workdate,
			@FormParam("lunchOut") String lunchOut, @FormParam("nightIn") String nightIn,
			@FormParam("nightOut") String nightOut, @FormParam("workDesc") String workDesc,
			@FormParam("empId") String emp_id) {
		int id = Integer.parseInt(emp_id);
		if (workdate != null && !workdate.equalsIgnoreCase("")) {
			if (emp_id != null && !emp_id.equalsIgnoreCase("")) {

				timeSchedual.timeSheetValue(lunchIn, in, out, workdate, lunchOut, nightIn, nightOut, id);
				if (workDesc != null && !workDesc.equalsIgnoreCase("")) {
					reqParser.setDateSheetValue(workDesc, workdate, id);
					timeSchedual.dateSheetValue();
				}
				timeSchedual.clearMap();
				// jsonResService.setTimeSheetValue(workdate,in,out,id,empId);
			} else {
				jsonResService.errorResponse("emp_id can not be null");
			}
		} else {
			jsonResService.errorResponse("Date can not be null");
		}
		return jsonResService.responseSender();
	}

	public void email() {
	}

	public void excelExport() {
	}

	public void monthlyExcel() {
	}

	public void editDetail() {
	}

	public void signOut() {
	}

	public void emailValidation() {
	}

}
