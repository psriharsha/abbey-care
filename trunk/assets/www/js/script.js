$( document ).ready(function() {
 $('#settingsSection').hide();
 $('#invisible').hide();
 $('#invisible').click(function(){
	 hideMenu();
 });
 $('#headSetting').click(function(){
	 $('#settingsSection').fadeToggle();
	 $('#invisible').fadeToggle();
	 changeSyncState();
 });
 $('#profile').click(function(){
	 Android.changeActivity("com.abbey.zephyr.ProfileActivity");
 });
 $('#accountSubmit').click(function(){
	 var username = $('#user').val();
	 var password = $('#pass').val();
 	var result = Android.onSubmit(username,password);
 	$('#errorMsg').html(result);
 });
 function alertMessage(){
	 alert("Hi Alert!!");
 }
 function setVitals(){
	 var vitals = new Array();
	 vitals = Android.getVitals();
 }
 function hideMenu(){
	 $('#settingsSection').fadeOut();
	 $('#invisible').fadeOut();
 }
 function changeSyncState(){
	  var syncState = Android.myServiceRun();
	 if(syncState == "false"){
		 $('#menuStopSync').html("Start Sync");
	 }
	 else {
		 $('#menuStopSync').html("Stop Sync");
	 }
 }
 function bioChanged(){
	 var value = $('#bioDecide').val();
	 Android.changeBio(value);
 }
 //window.setInterval(setVitals(),1000);
 $('#menuProf').click(function(){
	 hideMenu();
	 Android.myProfile();
 });
 $('#menuStopSync').click(function(){
	 if($(this).html() == "Stop Sync")
	 	$(this).html("Start Sync");
	 else
		 $(this).html("Stop Sync");
	 hideMenu();
	 Android.stopSync();
 });
 $('#menuLogout').click(function(){
	 hideMenu();
	 Android.logout();
 });
 $('#menuHome').click(function(){
	 hideMenu();
	 Android.goHome();
 });
{
	 var bioS = Android.selectBio();
	 var bioNames = new Array();
	 bioNames = bioS.split(",");
	 $('#bioDecide').html("<option value=\"def\">--</option>");
	 for(var i=0; i<bioNames.length; i++){
		 if(bioNames[i] != "null"){
			 var temp = $('#bioDecide').html();
			 $('#bioDecide').html(temp+"<option value=\""+bioNames[i]+"\">"+bioNames[i]+"</option>");
		 }
	 }
 }
 //// Profile Page
$('#bioDecide').change(function(){
	bioChanged();
});
 $('#profileName').html(Android.getMyName());
 $('#profAge').html(Android.getMyAge()+" Years");
 $('#profGen').html(Android.getGender());
});