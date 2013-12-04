$( document ).ready(function() {
 $('#settingsSection').hide();
 $('#invisible').hide();
 $('#invisible').click(function(){
	 $('#settingsSection').fadeOut();
	 $('#invisible').fadeOut();
 });
 $('#headSetting').click(function(){
	 $('#settingsSection').fadeToggle();
	 $('#invisible').fadeToggle();
 });
 $('#profile').click(function(){
	 Android.changeActivity("com.abbey.zephyr.ProfileActivity");
 });
});