	
	
/* /////////////////////////////	First Menu*/	
	
$(document).ready(function(){

$(".menu1").click(function()
	{
	var X=$(this).attr('id');
		if(X==1){
			$(".submenu1").hide();
			$(this).attr('id', '0'); 
		}else{
			$(".submenu1").show();
			$(".submenu2").hide();
			$(".submenu3").hide();
			$(this).attr('id', '1');
		}
});

//Mouse click on sub menu
//$(".submenu").mouseup(function()
$(".submenu1").mouseleave(function(){
/*alert("left");
return false*/
$(".submenu1").hide();
$(".menu1").attr('id', '');
});

//Mouse click on my account link
$(".menu1").mouseup(function(){
return false
});


//Document Click
$(document).mouseup(function()
	{
		$(".submenu1").hide();
		$(".menu1").attr('id', '');
	});
});
	
	
	
		
	
/* /////////////////////////////	Menu 2 */	
	
$(document).ready(function(){

$(".menu2").click(function()
	{
	var X=$(this).attr('id');
		if(X==1){
			$(".submenu2").hide();
			$(this).attr('id', '0'); 
		}else{
			$(".submenu1").hide();
			$(".submenu2").show();
			$(".submenu3").hide();
			$(this).attr('id', '1');
		}
});

//Mouse click on sub menu
//$(".submenu").mouseup(function()
$(".submenu2").mouseleave(function(){
/*alert("left");
return false*/
$(".submenu2").hide();
$(".menu2").attr('id', '');
});

//Mouse click on my account link
$(".menu2").mouseup(function(){
return false
});


//Document Click
$(document).mouseup(function()
	{
		$(".submenu2").hide();
		$(".menu2").attr('id', '');
	});
});
	
	
	
	/* /////////////////////////////	Menu 3*/	

	
$(document).ready(function(){

$(".menu3").click(function()
	{
	var X=$(this).attr('id');
		if(X==1){
			$(".submenu3").hide();
			$(this).attr('id', '0'); 
		}else{
			$(".submenu1").hide();
			$(".submenu2").hide();
			$(".submenu3").show();
			$(this).attr('id', '1');
		}
});

//Mouse click on sub menu
//$(".submenu").mouseup(function()
$(".submenu3").mouseleave(function(){
/*alert("left");
return false*/
$(".submenu3").hide();
$(".menu3").attr('id', '');
});

//Mouse click on my account link
$(".menu3").mouseup(function()
{
return false
});


//Document Click
$(document).mouseup(function()
	{
		$(".submenu3").hide();
		$(".menu3").attr('id', '');
	});
});
	
	
	