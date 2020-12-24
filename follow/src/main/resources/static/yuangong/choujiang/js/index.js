$(function () {
    $(".rules_footer").click(function () {
        $(".rules_cover,.rules_footer").hide();
        $(".rules_con").css("height", "95vw");
    });
    $('.pointer').click(function () {
        Rotate();
    });
    $(".covers_quit").click(function () {
        $(".cover").fadeOut(500);
        $(this).parent().fadeOut(500);
    });

    function Init() {
        var H = $(window).height(),
            W = $(window).width();
        $(".cover").css({"width": W, "height": H});

        $(".covers_btn").click(function () {
            $(".cover").hide();
            $(this).parent().hide();
            Rotate();
        });

        $(".covers_btn2").click(function () {
            $(".cover").hide();
            $(this).parent().parent().hide();
            $(".rules_cover,.rules_footer").hide();
            $(".rules_con").css("height", "95vw");
        });
    }

    Init();
    // 抽奖
    var rotateTimeOut = function () {
        $('#rotate').rotate({
            angle: 0,
            animateTo: 2160,
            duration: 8000,
            callback: function () {
                alert('网络超时，请检查您的网络设置！');
            }
        });
    };
    var bRotate = false;

    var rotateFn = function (awards, angles, txt, path) {
        bRotate = !bRotate;
        $('#rotate').stopRotate();
        $('#rotate').rotate({
            angle: 0,
            animateTo: angles + 2825,
            duration: 8000,
            callback: function () {
                $(".cover,.covers2").show();
                // $('.cover_fuck,.cover_quit').css({"display": "block","animation": "action_translateY 2s linear", "animation-fill-mode":"forwards"});
                // $(".cover_fuck").text("+"+parseInt(txt));
                $(".covers2 .covers_font span").text(txt);
                $("#imgId").attr('src', path);
                bRotate = !bRotate;
            }
        })
    };

    var time = 2;

    function Rotate() {
        // 防止多次点击
        if (bRotate) {
        	alert("暂无抽奖次数")
            return;
        }
        // var Url3 = testname+"/yfax-htt-api/api/htt/doHolidayActivityLuckyDraw";
        // $.post(Url3,{"phoneNum": uid1,"access_token": token1},function(res){
        // var times = res.data.remainlotteryTimes,
        // 	item = res.data.resultCode;
        // $(".cover_num span").text(times);
        // var item = 1;
        // console.log(res);
        // if(item == -1) {
        // alert("抽奖次数已用完");
        // }else {
        // $('.cover_fuck').hide();
        time--;
        if (time <= 0) {
            console.log("covers3");
            $(".cover,.covers3").show();
        } else {
            var item = Math.floor((Math.random() * 3) + 1);

            $.ajax({
                url: '/ynbocfollow/staff/luckDraw',
                type: 'get',
                dataType: 'json',
                beforeSend: function (request) {
                    request.setRequestHeader("X-Auth-Token", localStorage.getItem("yg_token"));
                },
                data: {},
                success: function (data) {
                    if (data.status !== 200) {
                        alert(data.msg)
                    } else {
                        item = Number(data.data.prizeId)

                        switch (item) {
                            case 1:
                                rotateFn(1, 90, '谢谢参与', 'images/jiangpin1.png');
                                break;
                            case 2:
                                rotateFn(2, 180, '10元话费', 'images/jiangpin2.png');
                                break;
                            case 3:
                                rotateFn(3, 270, '红包50元', 'images/jiangpin1.png');
                                break;
                            case 4:
                                rotateFn(4, 360, '20元话费', 'images/jiangpin2.png');
                                break;
                            default:
                                rotateFn(1, 90, '谢谢参与', 'images/jiangpin1.png');
                                break;
                        }

                    }
                },
                error: function (data) {
                    console.log("e:" + data.msg)
                    console.log("e:" + data.data)
                }
            })


        }

        // }
        // });
    }
})