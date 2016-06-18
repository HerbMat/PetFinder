
	var map; 	
	var infoWindow; 	
	var geocoder = new google.maps.Geocoder();
	var messageLoc;
	function mapStart(localisation)  
	{  
		var pos = new google.maps.LatLng(53.429805, 14.537883);
		var mapOptions = {
			zoom: 10,
			center: pos,
			mapTypeId: google.maps.MapTypeId.ROADMAP,
			disableDefaultUI: true
		};
		map = new google.maps.Map(document.getElementById("map"), mapOptions); 			
		infoWindow = new google.maps.InfoWindow();
		messageLoc = localisation
		geocoder.geocode({address: localisation}, geocodeHandler);
	}  
	
	function geocodeHandler(results, status)
	{
		var size 			= new google.maps.Size(32,32);
		var sizeShadow		= new google.maps.Size(59,32);
		var startingPoint		= new google.maps.Point(0,0);
		var handlePoint	= new google.maps.Point(16,16);
		
		var icon	= new google.maps.MarkerImage("http://maps.google.com/mapfiles/kml/pal3/icon52.png", size, startingPoint, handlePoint);
		var shadow 	= new google.maps.MarkerImage("http://maps.google.com/mapfiles/kml/pal3/icon52s.png", sizeShadow, startingPoint, handlePoint);
		
		if(status == google.maps.GeocoderStatus.OK)
		{
			map.setCenter(results[0].geometry.location);
			var marker = new google.maps.Marker(
				{
					map: 		map, 
					position: 	results[0].geometry.location,
					icon: 		icon,
					shadow: 	shadow
				}
			);
			infoWindow.open(map, marker);
			infoWindow.setContent('<strong>Ad Localisation</strong><br />' + messageLoc);
		}
		else
		{
			console.log("Adres not found!");
		}
	}
