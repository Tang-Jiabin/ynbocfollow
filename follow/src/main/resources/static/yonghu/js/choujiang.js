


// 定义一个奖池
var jackpot = [
  ['奖品A1', '奖品A2', '奖品A3', '奖品A4'],
  ['奖品B1', '奖品B2', '奖品B3', '奖品B4'],
  ['奖品C1', '奖品C2', '奖品C3', '奖品C4'],
  ['奖品D1', '奖品D2', '奖品D3', '奖品D4']
];
/**
  * [table 创建表格]
  * @param {[Array]} arr  [奖品数组]
  * @param {[String]} selector [选择器]
  * @return {[String]} table [返回一个HTML标签]
  */
function table(arr, selector) {
  var table = '<table border="0">';
  for (var i = 0; i < arr.length; i++) {
   table += '<tr>';
   for (var j = 0; j < arr[i].length; j++) {
    table += '<td class="' + selector + '">' + arr[i][j] + '</td>';
   }
   table += '</tr>';
  }
  table += '</table>';
  return table;
}
// 输出奖池
document.getElementById('table').innerHTML = table(jackpot, 'p');
// 抽过的还能抽  可定义抽奖次数-->次数限制      num需要定义
//     不定义抽奖次数-->次数无限      num不需定义
// 抽过的不能抽  可定义抽奖次数-->次数限制(次数不超过选择器长度) num需要定义
//     不定义抽奖次数-->次数等于选择器长度    num需要定义
/**
  * [start 开始抽奖]
  * @param {[String]} selector [选择器]
  * @param {[String]} addselector [给选中的添加样式]
  * @param {[String]} newaddselector [中奖奖品样式]
  * @param {[Number]} speed  [时间越小，速度越快]
  * @return {[type]}    [description]
  */


/**
  * [start 开始抽奖]
  * @param {[String]} selector [选择器]
  * @param {[String]} addselector [给选中的添加样式]
  * @param {[String]} newaddselector [中奖奖品样式]
  * @param {[Number]} speed  [时间越小，速度越快]
  * @return {[type]}    [description]
  */
function startRanDom(selector, addselector, newaddselector, speed) {
  
    // 如果写成var timer会每次执行时重新定义一个timer，那么clearInterval(timer)只能清除后面定义的那个timer，前面定义的已经没有变量指向了 无法清除
    timer = setInterval(function() {
     var count = Math.floor(Math.random() * $('.' + selector).length);
     $('.' + selector).eq(count).addClass(addselector);
     $('.' + selector).eq(count).siblings().removeClass(addselector);
     $('.' + selector).eq(count).parent().siblings().children().removeClass(addselector);
    }, speed);
    setTimeout(function(){  //使用  setTimeout（）方法设定定时2000毫秒
      clearInterval(timer);
      $('.' + addselector).addClass(newaddselector).removeClass(selector);
      //alert($('.' + addselector).html());
	  $(".zhifu_bg").show(100);
      $('.' + addselector).addClass('clearactive');
      console.log("抽奖结束");
    },2000);
    
 
}
