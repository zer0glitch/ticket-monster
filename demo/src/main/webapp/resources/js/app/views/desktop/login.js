define([
    'utilities',
    'configuration',
    'text!../../../../templates/desktop/login.html'
], function (
    utilities,
    config,
    securityTemplate) {

    var LoginView = Backbone.View.extend({
        events:{
            "click input[name='signIn']":"signIn",
            "click input[name='signUp']":"signUp",
        },
    	render:function () {
    		var self = this;
    		utilities.applyTemplate($(this.el), securityTemplate, {});
            return this;
        },
        signIn:function (event) {
        	var username = $("#userName");
        	var password = $("#password");
        	
        	if (username.is(":valid") && password.is(":valid")) {
        		$.ajax({url: (config.baseUrl + "rest/login"),
        			data:JSON.stringify({ username: username.val(), password: password.val() }),
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
        },
        signUp:function (event) {
        	var firstName = $("#firstName");
        	var lastName = $("#lastName");
        	var email = $("#email");
        	var password = $("#userPassword");
        	var passwordConfirmation = $("#passwordConfirmation");
        	
        	if (firstName.is(":valid") && lastName.is(":valid") && email.is(":valid") && password.is(":valid") && passwordConfirmation.is(":valid")) {
        		$.ajax({url: (config.baseUrl + "rest/registration"),
        			data:JSON.stringify({ firstName: firstName.val(), lastName: lastName.val(), email: email.val(), password: password.val(), passwordConfirmation: passwordConfirmation.val() }),
        			type:"POST",
        			dataType:"json",
        			contentType:"application/json",
        			success: function(context) {
        				if (context.user) {
        					window.location = config.baseUrl; 
        				} else {
        					$("#error-signup").empty().append(context.message);
        				}
        			}}
        		);
        	}
        }
    });

    return LoginView;
});