// JavaScript Document

			var phoneReg = /(^1[3|4|5|7|8]\d{9}$)|(^09\d{8}$)/;//手机号正则 
			var count = 60; //间隔函数，1秒执行
			var InterValObj1; //timer变量，控制时间
			var curCount1;//当前剩余秒数
			/*第一*/
			function sendMessage1() {
				curCount1 = count;		 		 
				var phone = $.trim($('#phone1').val());
				if (!phoneReg.test(phone)) {
					alert(" 请输入有效的手机号码"); 
					return false;
				}
				//设置button效果，开始计时
				$("#btnSendCode1").attr("disabled", "true");
				$("#btnSendCode1").val( + curCount1 + "秒再获取");
				InterValObj1 = window.setInterval(SetRemainTime1, 1000); //启动计时器，1秒执行一次
				//向后台发送处理数据
					 
			}
			function SetRemainTime1() {
				if (curCount1 == 0) {                
					window.clearInterval(InterValObj1);//停止计时器
					$("#btnSendCode1").removeAttr("disabled");//启用按钮
					$("#btnSendCode1").val("重新发送");
				}
				else {
					curCount1--;
					$("#btnSendCode1").val( + curCount1 + "秒再获取");
				}
			} 
			
			/*提交*/
			function binding(){
				alert(1)
			}
	
			var phoneReg = /(^1[3|4|5|7|8]\d{9}$)|(^09\d{8}$)/;//手机号正则 
			var count = 60; //间隔函数，1秒执行
			var InterValObj1; //timer变量，控制时间
			var curCount1;//当前剩余秒数
			/*第一*/
			function sendMessage2() {
				curCount1 = count;		 		 
				var phone = $.trim($('#phone2').val());
				if (!phoneReg.test(phone)) {
					alert(" 请输入有效的手机号码"); 
					return false;
				}
				//设置button效果，开始计时
				$("#btnSendCode2").attr("disabled", "true");
				$("#btnSendCode2").val( + curCount1 + "秒再获取");
				InterValObj1 = window.setInterval(SetRemainTime1, 1000); //启动计时器，1秒执行一次
				//向后台发送处理数据
					 
			}
			function SetRemainTime1() {
				if (curCount1 == 0) {                
					window.clearInterval(InterValObj1);//停止计时器
					$("#btnSendCode2").removeAttr("disabled");//启用按钮
					$("#btnSendCode2").val("重新发送");
				}
				else {
					curCount1--;
					$("#btnSendCode2").val( + curCount1 + "秒再获取");
				}
			} 
			
			/*提交*/
			function binding(){
				alert(1)
			}
	