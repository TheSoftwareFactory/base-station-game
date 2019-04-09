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
  dt = new Date(dt)
  admin.database().ref('/stations').push({ "name": a[0], "latitude": parseFloat(a[1]), "longitude": parseFloat(a[2]), "timeToLive": dt.toISOString() })
  .then(snapshot => {
    return res.redirect(snapshot.ref.toString());
  }).catch(error => {
    console.log("error",error)
    res.status(500).send(error)
  })
});

exports.stationDies = functions.database.ref('/stations/{stationId}/timeToLive')
  //StationId is going to die
  .onUpdate((change, context) => {
    const stationExp = 500;
    // Grab the current value of what was written to the Realtime Database.
    const newValue = change.after.val();
    var id = context.params.stationId;
    console.log('Time to live of station', id, newValue);

    //if station dies
    if (newValue > new Date()) {
      console.log("station dies");

      //get snapshot of stations key
      var starCountRef = admin.database().ref('stations');
      starCountRef.once('value').then(function (snapshot) {
        //get all stations as snapshot
        snapshot.forEach((childSnapshot) => {
          console.log(childSnapshot.val());

          //get the conquerer list of each station
          childSnapshot.child("BlueConquerer").forEach(function (babySnapshot) {
            var key = babySnapshot.key;
            var val = babySnapshot.val();
            console.log("erster conquerer: ")
            console.log(key);
            console.log(val);


            admin.database().ref('Users').child(key).once("value", xd => {
              if (xd.exists()) {
                //const userData = xd.val();
                //console.log("exists!", userData);

                //update exp based on old exp
                admin.database().ref('Users').child(key).child("exp").once('value', function (conquerer) {
                  admin.database().ref('Users').child(key).child("exp").set(conquerer.val() + stationExp);
                });
                //update count of conquered base stations for user
                admin.database().ref('Users').child(key).child("ConqueredStations").once('value', function (conquerer) {
                  admin.database().ref('Users').child(key).child("ConqueredStations").set(conquerer.val() + 1);
                });
              }
            });
          });

        });
        //todo: delete station
        //admin.database().ref('stations').remove(id);
      return res.send("durchgelaufen")
      }).catch(function(error){
        console.log(error)
        res.send(error)
      });
    }
  });

exports.winningteam = functions.database.ref('stations/{stationId}/Teams/{teamId}/teamScore')
  .onWrite((change, context) => {
    var winningteam="";
    var winningscore=0;
    admin.database().ref("stations").child(context.params.stationId).child("Teams").once('value').then(function(teams){
        teams.forEach(function(team){
          //console.log("team: ",team.val());
          console.log("teamscore",team.child("teamScore").val());
          if (team.child("teamScore").val()>winningscore){
            winningscore=team.child("teamScore").val();
            winningteam=team.key;
          }
        });
        console.log("winningscore: ",winningscore);
        console.log("winningteam: ",winningteam);
        admin.database().ref("stations").child(context.params.stationId).child("Teams").child("winnerTeam").set(winningteam);
        return "geil"     
      }).catch(function(error){
        console.log(error)
        res.send(error)
      });
    return "nice"
  })

//var List = require("collections/list");

exports.stationDiesChrono = functions.https.onRequest((req, res) => {
  //StationId is going to die
  //get snapshot of stations key
  var deadStations="";
  var starCountRef = admin.database().ref('stations');
  starCountRef.once('value').then(function (snapshot) {
    //get all stations as snapshot
    snapshot.forEach(function (childSnapshot) {

      
      var winningteam="";
      var winningscore=0;
      var stationkey=childSnapshot.key;
      console.log("station key: ",childSnapshot.key);
      console.log("station val: ",childSnapshot.val());


      // when Teams empty -> stop doing anything
      admin.database().ref("stations").child(childSnapshot.key).child("Teams").child("winnerTeam").once('value', function(snap){
        console.log("winningref:",snap.ref);
        //console.log("winningteam:",snap.val());
        winningteam=snap.val();     
        console.log("winningteam start: ",winningteam)    
      })
      
      //Check for right condition
      //console.log(childSnapshot.val()['name'],"<------- station");
      //console.log(childSnapshot.val(),"<------- station");
      //console.log(childSnapshot.child('timeToLive').val())
      //console.log(new Date());
      if (new Date(childSnapshot.child("timeToLive").val()) > new Date()) {
        console.log("station dies");
        //get the conquerer list of each station
        //console.log("teamübersicht",childSnapshot.child("Teams").val());
      
        childSnapshot.child("Teams").forEach(function (teamSnapshot) {
          //var key = teamSnapshot.key;
          var teamname=teamSnapshot.key;
          /*console.log("team snapshot, team:",teamSnapshot.val());
            
          //console.log("teamscore: ",lol.child("teamScore").val()); 
          console.log("interessante infos:")    */
          //console.log("winningteam: ",winningteam)
          console.log("currentteam: ",teamname) 
          if(teamname===winningteam){

            //console.log(val);
            teamSnapshot.child("Players").forEach(function (userScore) {
              //console.log(userScore.key);
              //console.log(userScore.val());
              var key = userScore.key;
              var val = userScore.val();

              admin.database().ref('Users').child(key).once("value", xd => {
                if (xd.exists()) {
                  //var userData = xd.val();
                  //console.log("user iteration", userData);

                  //console.log("score",val);
                  //update exp based on old exp
                  admin.database().ref('Users').child(key).child("exp").once('value', function (conquerer) {
                    //console.log("previousexp",conquerer.val());
                    admin.database().ref('Users').child(key).child("exp").set(conquerer.val() + val);
                      /* admin.database().ref('Users').child(key).child("exp").once('value', function (lol) {
                        console.log("exp after",lol.val());
                      }); */
                      //return "done";
                  });
                
                  //update count of conquered base stations for user
                  admin.database().ref('Users').child(key).child("ConqueredStations").once('value', function (conquerer) {
                    admin.database().ref('Users').child(key).child("ConqueredStations").child(childSnapshot.val()['name']).set(val);
                  });
                }
              });
            })
          }
        });
        deadStations=deadStations+stationkey.toString();
      }
      //todo: delete station
      //admin.database().ref('stations').remove(id);
    });
    
  return res.send(deadStations)
  }).catch(function(error){
    console.log("error: ",error)
    res.send(error)
  });
  return "nice"
});


exports.updateTeamScores = functions.database.ref('stations/{stationId}/Teams/{teamId}/Players')
  .onWrite((change, context) => {
    // Grab the current value of what was written to the Realtime Database.
    const newValue = change.after;
    console.log('current value', newValue.val());
    var station_id = context.params.stationId;
    var team_id = context.params.teamId;
    console.log("station id: ", station_id);
    console.log("team id: ", team_id);
    console.log("erste conquerer: ",newValue.val());
    var teamscore = 0;
    newValue.forEach(function(snapshot){
      
      console.log(snapshot.val());
      teamscore = teamscore + snapshot.val();
    });
    console.log("teamscore: ",teamscore)
    admin.database().ref('stations').child(station_id).child("Teams").child(team_id).child("teamScore").set(teamscore);
    return "nice";
  });

// exports.levelUp = functions.database.ref('Users/{userId}/exp')
//     .onUpdate((change, context) => {
//       var exp = change.after.val();
//       console.log("" + typeof exp + " " + exp);
//       const user_id = context.params.userId;
//       admin.database().ref('Users').child(user_id).child("level").once(
//         'value', (snapshot) => {
//           var level = snapshot.val()
//           console.log("" + typeof level + " " + level);
//           var change = false;
//           while (exp >= 4000) {
//             change = true;
//             exp -= 4000;
//             level += 1;
//           }
//           if (change) {
//             admin.database().ref('Users').child(user_id).child("level").set(level);
//             admin.database().ref('Users').child(user_id).child("exp").set(exp);
//           }
//         }
//       );
//       res.send("successfull");
//     }
//   );

exports.dummy = functions.https.onRequest((req, res) => {
  return res.send("Hello");
})