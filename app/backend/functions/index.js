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
  var a = original.split(",");
  // Push the new message into the Realtime Database using the Firebase Admin SDK.
  //UTC Date
  var dt = new Date();
  dt.setHours(dt.getHours() + a[3]);
  dt = new Date(dt);
  return admin.database().ref('/stations').push({ "name": a[0], "latitude": parseFloat(a[1]), "longitude": parseFloat(a[2]), "timeToLive": dt.toISOString() })
    .then((snapshot) => {
      // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
      return res.redirect(302, snapshot.ref.toString());
    })
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

exports.stationDiesChrono = functions.https.onRequest((req, res) => {
  //StationId is going to die
  //get snapshot of stations key
  var starCountRef = admin.database().ref('stations');
  starCountRef.on('value', function (snapshot) {
    //get all stations as snapshot
    snapshot.forEach(function (childSnapshot) {
      //Check for right condition
      console.log(childSnapshot.val()['name'],"<------- station");
      //console.log(childSnapshot.child('timeToLive').val())
      //console.log(new Date());
      if (new Date(childSnapshot.child("timeToLive").val()) > new Date()) {
        console.log("station dies");
        //get the conquerer list of each station
        childSnapshot.child("Teams").forEach(function (teamSnapshot) {
          //var key = teamSnapshot.key;
          //var val = teamSnapshot.val();
          //console.log("First conquerer: ");
          //console.log(key);
          //console.log(val);
          teamSnapshot.forEach(function (userScore) {
            console.log(userScore.key);
            console.log(userScore.val());
            var key = userScore.key;
            var val = userScore.val();

            admin.database().ref('Users').child(key).once("value", xd => {
              if (xd.exists()) {
                var userData = xd.val();
                console.log("exists!", userData);

                //update exp based on old exp
                admin.database().ref('Users').child(key).child("exp").once('value', function (conquerer) {
                  admin.database().ref('Users').child(key).child("exp").set(conquerer.val() + val);
                });
                
                //update count of conquered base stations for user
                admin.database().ref('Users').child(key).child("ConqueredStations").once('value', function (conquerer) {
                  admin.database().ref('Users').child(key).child("ConqueredStations").child(childSnapshot.val()['name']).set(val);
                });
              }
            });
          })

        });
      }
    });
    //todo: delete station
    //admin.database().ref('stations').remove(id);

  });
  return res.write(200,"nice");
});


exports.updateTeamScores = functions.database.ref('stations/{stationId}/teams/{teamId}')
  .onUpdate((change, context) => {
    // Grab the current value of what was written to the Realtime Database.
    const newValue = change.after.val();
    console.log('current value', newValue);
    var station_id = context.params.stationId;
    var team_id = context.params.teamId;
    console.log("station id: ", station_id);
    console.log("team id: ", team_id);


    newValue.forEach(function (snapshot) {
      var teamscore = 0;
      console.log(snapshot.val());
      teamscore = teamscore + snapshot.val();
      /*snapshot.forEach(function(childSnapshot){
        console.log(childSnapshot);
        teamscore=teamscore+childSnapshot.val();
      });*/
    });
    admin.database().ref('stations').child(station_id).child("teams").child(team_id).child("teamScore").set(teamscore);
    return "nice";
  });

