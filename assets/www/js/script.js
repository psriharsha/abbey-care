$( document ).ready(function() {
 $('#settingsSection').hide();
 $('#invisible').hide();
 $('#invisible').click(function(){
	 hideMenu();
 });
 $('#headSetting').click(function(){
	 $('#settingsSection').fadeToggle();
	 $('#invisible').fadeToggle();
	 //changeSyncState();
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
 //// Profile Page
$('#bioDecide').change(function(){
	bioChanged();
});
 $('#profileName').html(Android.getMyName());
 $('.profName').html(Android.getMyName());
 $('#profAge').html(Android.getMyAge()+" Years");
 $('#profGen').html(Android.getGender());
 $('#profBlood').html(Android.getBloodGroup());
 $('#profEth').html(Android.getEthnicity());
 $('#menuStopSync').html(Android.myServiceRun());

});