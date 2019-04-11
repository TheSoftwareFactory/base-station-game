// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

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


exports.winningteam_update = functions.database.ref('stations/{stationId}/Teams/{teamId}/teamScore')
  .onUpdate((change, context) => {
    var winningteam="";
    var winningscore=0;
    return admin.database().ref("stations").child(context.params.stationId).child("Teams").once('value').then(function(teams){
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
        admin.database().ref("stations").child(context.params.stationId).child("winnerTeam").set(winningteam);
        return "geil"     
      }).catch(function(error){
        console.log(error)
        res.send(error)
      });
   
  })

  exports.winningteam_create = functions.database.ref('stations/{stationId}/Teams/{teamId}/teamScore')
  .onCreate((change, context) => {
    var winningteam="";
    var winningscore=0;
    return admin.database().ref("stations").child(context.params.stationId).child("Teams").once('value').then(function(teams){
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
        admin.database().ref("stations").child(context.params.stationId).child("winnerTeam").set(winningteam);
        return "geil"     
      }).catch(function(error){
        console.log(error)
        res.send(error)
      });
  })


exports.updateTeamScores = functions.database.ref('stations/{stationId}/Teams/{teamId}/Players')
  .onWrite((added, context) => {
    if (added.after.exists()){

    
    // Grab the current value of what was written to the Realtime Database.
    const newValue = added.after;
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
    return admin.database().ref('stations').child(station_id).child("Teams").child(team_id).child("teamScore").set(teamscore);    
  }
  });

// exports.levelUp = functions.database.ref('Users/{userId}/exp')
//     .onUpdate((change, context) => {
//       var exp = change.after.val();
//       const user_id = context.params.userId;
//       admin.database().ref('Users').child(user_id).child("level").once(
//         'value', (snapshot) => {
//           var level = snapshot.val();
//           const res = levelUp(level, exp);
//           if (res[2]) {
//             admin.database().ref('Users').child(user_id).child("level").set(res[0]);
//             admin.database().ref('Users').child(user_id).child("exp").set(res[1]);
//           }
//         }
//       );
//       return "nice";
//     }
// );

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
const LEVEL_UP_LIMIT = () => 100;

const levelUp = (level, exp) => {
  var changed = false;
  while (exp >= LEVEL_UP_LIMIT()) {
    changed = true;
    exp -= LEVEL_UP_LIMIT();
    level += 1;
  }
  return [level, exp, changed];
};

const scoreToExp = (change, context) => {
  const userId = context.params.userId;
  var score = 0;
  if (change.before.exists()) {
    // Not a new station
    if (change.after.exists()) {
      // The station was modified
      score = change.after.val() - change.before.val();
    }
    // We don't care if the station was deleted.
  } else {
    //New station
    score = change.after.val();
  }
  if (score > 0) {
    return admin.database().ref('Users').child(userId).once(
      'value', (snapshot) => {
        level = snapshot.child("level").val();
        exp = snapshot.child("exp").val();
        exp += score;
        const res = levelUp(level, exp);
        if (res[2]) {
          admin.database().ref('Users').child(userId).child("level").set(res[0]);
        }
        admin.database().ref('Users').child(userId).child("exp").set(res[1]);
      }
    );
  } else {
    console.log("Didn't add experience to " + userId + " because the score was " + score + ".");
    return true;
  }
};

exports.playedStationsScoreToExp = functions.database.ref('Users/{userId}/PlayedStations/{stationId}')
  .onWrite(scoreToExp);

// exports.playedStationsScoreToExp = functions.database.ref('Users/{userId}/ConqueredStations')
//   .onCreate(scoreToExp);


exports.stationDiesChrono = functions.https.onRequest((req, res) => {
  var deadStations=[];
  const promises=[];
  
  var starCountRef = admin.database().ref('stations');
  starCountRef.once('value').then( snapshot => {
    //get all stations as snapshot
    snapshot.forEach(function (childSnapshot) {
      console.log("im foreach")
      var winningteam="";
      var winningscore=0;
      var stationkey=childSnapshot.key;
      //console.log("station key: ",childSnapshot.key);

      p = admin.database().ref("stations").child(childSnapshot.key).child("winnerTeam").once('value').then(snap=>{
        winningteam=snap.val(); 

        //console.log("this is station: ",stationkey,"and my winner team is",winningteam)       
        //Check for right condition
        //console.log(childSnapshot.val()['name'],"<------- station");  
        //console.log("timetolive",childSnapshot.child('timeToLive').val())
        //console.log("new date",new Date());

        if (new Date(childSnapshot.child("timeToLive").val()) < new Date()) {
          //console.log("station dies",stationkey);
          deadStations.push(stationkey)

          const inner_promises=[];
          childSnapshot.child("Teams").child(winningteam).child("Players").forEach(function (userScore) {
            //console.log("player score",userScore.val())
            //console.log("player id",userScore.key)

            var key = userScore.key;
            var val = userScore.val();
            
            const final_promises=[];
            inside=admin.database().ref('Users').child(key).once("value", user => {
              if (user.exists()) {
                //var userData = user.val();
                //console.log("user iteration", userData);

                a=admin.database().ref('Users').child(key).child("exp").once('value').then(conquerer=> {
                  //console.log("previousexp",conquerer.val(),"of user ",key);
                 
                  return admin.database().ref('Users').child(key).child("exp").set(conquerer.val() + 500);
                });
               
                b=admin.database().ref('Users').child(key).child("ConqueredStations").once('value').then(conquerer => {
                  return admin.database().ref('Users').child(key).child("ConqueredStations").child(childSnapshot.val()['name']).set(val);
                });
                final_promises.push(a)
                final_promises.push(b)
                
              }
              //console.log("final promises",final_promises)
              return Promise.all(final_promises)
            })
            inner_promises.push(inside)           
                      
          })    
          return Promise.all(inner_promises);             
        }
          else {
            //console.log("station is alive")
            return true}         
      })
      promises.push(p);      
    })
    return Promise.all(promises)
  }).then(kek =>{
    return res.send("stations "+deadStations.toString()+"  died")
  })
  .catch(error=>{
    console.log("error: ",error)
    res.send(error)
  });
})

