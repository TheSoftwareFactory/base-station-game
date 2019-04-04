// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

/*
// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.addStation = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  var original = req.query.text;
  var a=original.split(",");
  console.log(a);
  console.log(a[0]);
  // Push the new message into the Realtime Database using the Firebase Admin SDK.

  return admin.database().ref('/stations').push({"name":a[0],"latitude":a[1],"longitude":a[2],"timeToLive":a[3]}).then((snapshot) => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    return res.redirect(302, snapshot.ref.toString());
  });
});
*/

exports.deleteStation = functions.https.onRequest((req, res) => {
  const original = req.query.text;
  return admin.database().ref('/stations').child(original).remove().then((snapshot) => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    return res.redirect(302, snapshot.ref.toString());
  });
});

exports.deleteAllStations = functions.https.onRequest((req, res) => {

  return admin.database().ref('/stations').remove().then((snapshot) => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    return res.redirect(302, snapshot.ref.toString());
  });
});

exports.addStation = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  var original = req.query.text;
  var a=original.split(",");
  console.log(a);
  console.log(a[0]);
  // Push the new message into the Realtime Database using the Firebase Admin SDK.

  return admin.database().ref('/stations').push({"name":a[0],"latitude":parseFloat(a[1]),"longitude":parseFloat(a[2]),"timeToLive":parseInt(a[3]),"RedConquerer":{"init":0},"BlueConquerer":{"init":0}}).then((snapshot) => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    return res.redirect(302, snapshot.ref.toString());
  });
});

exports.killStation = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  var original = req.query.text;
  console.log(original);
  // Push the new message into the Realtime Database using the Firebase Admin SDK.
	console.log(admin.database());
  return admin.database().ref('/stations/-LbJCKbLcIC-2BIbG1KJ/timeToLive').set(0).then((snapshot) => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    return res.redirect(302, snapshot.ref.toString());
  });
});

exports.stationDies = functions.database.ref('/stations/{stationId}/timeToLive')
    .onUpdate((change,context) => {
      const stationExp = 500;
      // Grab the current value of what was written to the Realtime Database.
      const newValue = change.after.val();
      var id=context.params.stationId;
      console.log('Time to live of station', id, newValue);

      //if station dies
      if (newValue==="0"){       
        console.log("station dies");

        //get snapshot of stations key
        var starCountRef = admin.database().ref('stations');
        starCountRef.on('value',function(snapshot) {

          //get all stations as snapshot
          snapshot.forEach(function(childSnapshot){
            console.log(childSnapshot.val());

            //get the conquerer list of each station
            childSnapshot.child("BlueConquerer").forEach(function(babySnapshot){
              var key = babySnapshot.key;
              var val = babySnapshot.val();
              console.log("erster conquerer: ")
              console.log(key);
              console.log(val);
              
              
              admin.database().ref('Users').child(key).once("value",xd => {
                if (xd.exists()){
                  //const userData = xd.val();
                  //console.log("exists!", userData);
                            
                  //update exp based on old exp
                  admin.database().ref('Users').child(key).child("exp").once('value',function(conquerer){
                    admin.database().ref('Users').child(key).child("exp").set(conquerer.val()+stationExp);
                  });
                  //update count of conquered base stations for user
                  admin.database().ref('Users').child(key).child("ConqueredStations").once('value',function(conquerer){
                    admin.database().ref('Users').child(key).child("ConqueredStations").set(conquerer.val()+1);
                  });
                }
              });
            });

          });
        //todo: delete station
        //admin.database().ref('stations').remove(id);

        });
      }
        
    	return "nice";
});


exports.updateTeamScores = functions.database.ref('/stations/')
    .onUpdate((change,context) => {
      const stationExp = 500;
      // Grab the current value of what was written to the Realtime Database.
      const newValue = change.after.val();
      var id=context.params.stationId;
      console.log('Time to live of station', id, newValue);

    	return "nice";
});

