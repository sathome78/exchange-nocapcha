$( ".reveal" ).click(function() {
  $('.reveal ul').slideToggle();
});

$( "#other_pairs" ).click(function() {
  $('#other_pairs ul').slideToggle();
  $("#other_pairs").toggleClass("whiter");	
});



//Enable REGISTER button if pass == repass when entering repass
$(document).ready(function(){
	document.getElementById("register_button").disabled = true;
    $("#repass").keyup(function(){
    	console.log("keyup")
    	var pass = $('#pass').val();
    	var repass = $('#repass').val();
    	var email = $('#email').val();
    	var login = $('#login').val();
    	
        if ((pass.length !=0) && (pass === repass)){
        	$('.repass').css("display","block");
        	if ((email.length != 0) && (login.length != 0)){
        		$("#register_button").prop('disabled', false);
        	} else {
        		$("#register_button").prop('disabled', true);
        	}
        }
        else {
        	$('.repass').css("display","none");
        	$("#register_button").prop('disabled', true);
        }
    });
});


