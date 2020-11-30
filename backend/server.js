const express = require("express");
const app = express();
const PORT = process.env.PORT || 3000
const fs = require("fs");

const admin = require("firebase-admin");
const serviceAccount = require("./key/key.json");
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://movier-bbc1b.firebaseio.com"
});

const routes  = require("./routes/routes.js")(app, fs, admin)

app.listen(PORT,() => {
  console.log('SERVER STARTED ON PORT %s', PORT);
});
