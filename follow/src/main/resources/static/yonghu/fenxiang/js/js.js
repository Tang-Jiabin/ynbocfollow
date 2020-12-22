// JavaScript Document
$(document).ready(function(){
	$("#wenanlist li a").hover(function(){
		$("#wenanlist li a").removeClass("sel");
		$(this).addClass("sel");
	});
});