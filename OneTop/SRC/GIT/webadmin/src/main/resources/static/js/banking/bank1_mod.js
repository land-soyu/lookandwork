$(document).ready(function() {

	$('#frm_action').submit(function(e) {
		e.preventDefault();
		
	    if (confirm(BANK1_CONTENT_SAVE_CONFIRM)) {
	        $.ajax({
	            url: 'bank1_mod_proc',
	            type: 'POST',
	            data: $('#frm_action').serialize(),
	            async: false,
	            success: function (data) {
	                if (data == 'success') {
	                    location.reload();
	                } else {
						alert(IMPORT_REQUESTFAIL);
					}
	            },
	            error: function (error) {
					alert(IMPORT_REQUESTFAIL);
				}
	        });
    	}
	});

});