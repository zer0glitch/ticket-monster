$.ajaxSetup({
	error : function(xhr, textStatus, errorThrown) {
		if (xhr.status == 401) {
			window.location = "#login";
		}
	}
});

function performLogin() {
	var username = $("#userName");
	var password = $("#password");
	
	if (username.is(":valid") && password.is(":valid")) {
		$.ajax({url: ("rest/login"),
			data:JSON.stringify({ userId: username.val(), password: password.val() }),
			type:"POST",
			dataType:"json",
			contentType:"application/json",
			success: function(context) {
				if (context.user) {
					window.history.back();
				} else {
					$("#error-signin").empty().append("Invalid credentials. Please try again.");
				}
			}}
		);
	}
}

function performLogout() {
	var contextPath = "";
	
	if (baseUrl) {
		contextPath = baseUrl + "/";
	}
	
	$.ajax({url: contextPath + "rest/logout",
		type:"POST",
		dataType:"json",
		contentType:"application/json",
		success: function(context) {
			$("#userSection").hide();
			$("#logoutSection").hide();
			$("#adminSection").hide();
			$("#signInSection").show();
			window.location = ""; 
		}}
	);
}

function performSignUp() {
	var firstName = $("#firstName");
	var lastName = $("#lastName");
	var email = $("#email");
	var password = $("#userPassword");
	var passwordConfirmation = $("#passwordConfirmation");
	
	if (firstName.is(":valid") && lastName.is(":valid") && email.is(":valid") && password.is(":valid") && passwordConfirmation.is(":valid")) {
		$.ajax({url: ("rest/registration"),
			data:JSON.stringify({ firstName: firstName.val(), lastName: lastName.val(), email: email.val(), password: password.val(), passwordConfirmation: passwordConfirmation.val() }),
			type:"POST",
			dataType:"json",
			contentType:"application/json",
			success: function(context) {
				if (context.user) {
					window.location = ""; 
				} else {
					$("#error-signup").empty().append(context.message);
				}
			}}
		);
	}
}