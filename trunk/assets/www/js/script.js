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
 	Android.onSubmit(username,password);
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
	  var syncState = Android.isSyncActive();
	 if(syncState != "false"){
		 $('#menuStopSync').html("Stop Sync");
	 }
	 else {
		 $('#menuStopSync').html("Start Sync");
	 }
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
 $('#bioDecide').click(function(){
	 Android.selectBio();
 });
});