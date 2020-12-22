// JavaScript Document
  function pros_hover(g) {
                    document.getElementById("pros01").style.display = (g == 1 ? "block" : "none");
                    document.getElementById("pros02").style.display = (g == 2 ? "block" : "none");
					document.getElementById("pros03").style.display = (g == 3 ? "block" : "none");
					document.getElementById("pros04").style.display = (g == 4 ? "block" : "none");
					document.getElementById("pros05").style.display = (g == 5 ? "block" : "none");

                  

                    document.getElementById("ps01").className = (g == 1 ? "a_h" : "");
                    document.getElementById("ps02").className = (g == 2 ? "a_h" : "");
					document.getElementById("ps03").className = (g == 3 ? "a_h" : "");
					document.getElementById("ps04").className = (g == 4 ? "a_h" : "");
					document.getElementById("ps05").className = (g == 5 ? "a_h" : "");
					

                   


                }
				
				$(document).ready(function(){
	$(".login_tab a").click(function(){
		$(".login_tab a").removeClass("sel");
		$(this).addClass("sel");
	});
});				$(document).ready(function(){
	$(".jifen_tab a").click(function(){
		$(".jifen_tab a").removeClass("sel3");
		$(this).addClass("sel3");
	});
});