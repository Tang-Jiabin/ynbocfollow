// JavaScript Document
$(document).ready(function(){
 $(".jidi_txt").hide();
 $("ul#jidi_list li").hover(function(){
  $(".jidi_txt",this).slideToggle(500);

 });
 

});