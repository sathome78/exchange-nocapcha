
$(document).ready(function() {

	function setHeiHeight() {
	    $('.full__height, .lk > .container, .sidebar, .main__content').css({
	        minHeight: $(window).height() + 'px'
	    });
	}
	setHeiHeight(); // устанавливаем высоту окна при первой загрузке страницы
	$(window).resize( setHeiHeight ); // обновляем при изменении размеров окна


	// Reset link whte attribute href="#"
	$('[href*="#"]').click(function(event) {
		event.preventDefault();
	});


	// Scroll to ID
    $('#main__menu a[href^="#"]').click( function(){ 
	    var scroll_el = $(this).attr('href'); 
        if ($(scroll_el).length != 0) {
	    $('html, body').animate({ scrollTop: $(scroll_el).offset().top }, 500);
        }
	    return false;
    });

    // tag select styller
    $('.select').chosen({disable_search_threshold: 10});

    // Mobile menu toggle
    $('.mobile__menu__toggle').click(function() {
    	$('body').toggleClass('open__offcanvas');
    	$(this).toggleClass('open');
    	$('.sidebar').toggleClass('open');
    });

    // Tabs
    //$('.nav-tabs a:first').tab('show');

	// Custom scrollbar
   // $(".custom__scrollbar").mCustomScrollbar();





});