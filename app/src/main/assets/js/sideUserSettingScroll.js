// JavaScript Document


/****
***右侧系统菜单效果
***/
	$(document).ready(function(){
							   
		var time = {
			mouseInTimeOutID : 0, 
			mouseOutTimeOutID : 0
		};					   
		$('.side_menu').hover(
			function(){
				time.dom = $(this);
				clearTimeout(time.mouseOutTimeOutID);
				if(time.mouseInTimeOutID == 0){
					time.mouseInTimeOutID = setTimeout($(this).animate({right:"0"},500,
						function(){time.mouseInTimeOutID = 0;}),300);
				}
			},
			function(){
				clearTimeout(time.mouseInTimeOutID);
				if(time.mouseOutTimeOutID == 0){
					time.mouseOutTimeOutID = setTimeout($(this).animate({right:"-150px"},500,
						function(){time.mouseOutTimeOutID = 0;}),300);
				}
			}
		)
	})
	
	


