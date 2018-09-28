const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


// Listens for new messages added to messages/:pushId
/* exports.pushNotification = functions.database.ref('/groups').onWrite( event => {

    console.log('Bildirim olayı tetiklendi.');

  // Create a notification
    const payload = {
        notification: {
            title: "Bildirim başlığı",
            body: "Bildirim açıklaması",
            sound: "default"
        },
    };

  //Create an options object that contains the time to live for the notification and the priority
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };

    return admin.messaging().sendToTopic("msgNotification",payload, options);
    //return admin.messaging().sendToTopic("msgNotification", payload, options);
}); */
