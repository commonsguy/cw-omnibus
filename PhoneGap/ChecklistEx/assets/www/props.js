// PhoneGap's APIs are not immediately ready, so set up an
// event handler to find out when they are ready

function onLoad() {
	document.addEventListener("deviceready", onDeviceReady, false);
}

// Now PhoneGap's APIs are ready

function onDeviceReady() {
	var element=document.getElementById('props');

	element.innerHTML='<li>Model: '+device.name+'</li>' + 
										'<li>OS and Version: '+device.platform +' '+device.version+'</li>' +
										'<li>PhoneGap Version: '+device.phonegap+'</li>';
}