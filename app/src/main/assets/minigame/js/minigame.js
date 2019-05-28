
//AR.radar.container = document.getElementById("radarContainer");

/* Implementation of AR-Experience (aka "World"). */

var World = {
    /* True once data was fetched. */
    initiallyLoadedData: false,

    /* pOI-Marker asset. */
    markerDrawableIdle: null,

    /* Called to inject new POI data. */
    loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
        try {
            PoiRadar.show();
            $('#radarContainer').unbind('click');
            $("#radarContainer").click(PoiRadar.clickedRadar);

            /*
                The example Image Recognition already explained how images are loaded and displayed in the augmented
                reality view. This sample loads an AR.ImageResource when the World variable was defined. It will be
                reused for each marker that we will create afterwards.
            */
            World.markerDrawableIdle = new AR.ImageResource("assets/marker_idle.png", {
                onError: World.onError
            });
            /*          var markerLocation = new AR.GeoLocation(poiData.latitude, poiData.longitude, poiData.altitude);
                      var markerImageDrawableIdle = new AR.ImageDrawable(World.markerDrawableIdle, 2.5, {
                          zOrder: 0,
                          opacity: 1.0
                      });
          
          
                      
                      
          
                      var radarCircle = new AR.Circle(0.03, {
                          horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
                          opacity: 0.8,
                          style: {
                              fillColor: "#ffffff"
                          }
                      });
          
                      var radardrawables = [];
                      radardrawables.push(radarCircle);
          
                      var markerObject = new AR.GeoObject(markerLocation, {
                          drawables: {
                              cam: [markerImageDrawableIdle],
                              radar: radardrawables
                          }
                      });
          */
            var marker = new Marker(poiData);
            /* Updates status message as a user feedback that everything was loaded properly. */
            World.updateStatusMessage('1 place loaded');
            PoiRadar.updatePosition();
        }
        catch (e) {
            World.updateStatusMessage(e);
        }
    },

    /* Updates status message shown in small "i"-button aligned bottom center. */
    updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

        console.log("updateing with message " + message.toString())
        var themeToUse = isWarning ? "e" : "c";
        var iconToUse = isWarning ? "alert" : "info";

        $("#status-message").html(message);
        $("#popupInfoButton").buttonMarkup({
            theme: themeToUse,
            icon: iconToUse
        });
    },

    /* Location updates, fired every time you call architectView.setLocation() in native environment. */
    locationChanged: function locationChangedFn(lat, lon, alt, acc) {
        for (var i = 0; i < 6; i++) {
            AR.logger.debug("IN locationChanged");
            var poiDataRandom = {
                "id": i,
                "longitude": (lon + (Math.random() / 5 - 0.1)),
                "latitude": (lat + (Math.random() / 5 - 0.1)),
                "altitude": 100.0,
                "description": "This is the description of POI#"+i,
                "title": "POI#"+i
            };

            World.loadPoisFromJsonData(poiDataRandom);
        }
    },

    onError: function onErrorFn(error) {
        alert(error);
    }
};

/*
    Set a custom function where location changes are forwarded to. There is also a possibility to set
    AR.context.onLocationChanged to null. In this case the function will not be called anymore and no further
    location updates will be received.
*/

AR.context.onLocationChanged = World.locationChanged;



function setStationLocation(json) {
    AR.logger.debug("setStationLocation CALLED" + json.toString());
    AR.logger.debug("World.stationLocation received-> " + json.stationPositionLatitude + " " + json.stationPositionLongitude);
    console.log(json);

    var poiData = {
        "id": 1,
        "longitude": parseFloat(json.stationPositionLatitude),
        "latitude": parseFloat(json.stationPositionLongitude),
        "altitude": 100.0,
        "description": "Station POI",
        "title": "Station"
    };

    World.loadPoisFromJsonData(poiData);



}

setStationLocation({ "stationPositionLatitude": "60.201375", "stationPositionLongitude": "24.935734" });


