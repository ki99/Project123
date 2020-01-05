// app.js

// [LOAD PACKAGES]
var express     = require('express');
var app         = express();
var bodyParser  = require('body-parser');
var mongoose    = require('mongoose');

// [ CONFIGURE mongoose ]

// CONNECT TO MONGODB SERVER
var db = mongoose.connection;
db.on('error', console.error);
db.once('open', function(){
    // CONNECTED TO MONGODB SERVER
    console.log("Connected to mongod server");
});
var mongoDB = 'mongodb://127.0.0.1:27017/data'
mongoose.connect(mongoDB, {useNewUrlParser : true});

// DEFINE MODEL
var Contact = require('./models/cont');
var Location = require('./models/loc');

// [CONFIGURE APP TO USE bodyParser]
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.get('/', function(req, res) {
  res.send('heello');
});

// [CONFIGURE SERVER PORT]
//var port = process.env.PORT || 8080;

// [CONFIGURE ROUTER]
var router1 = require('./routes/index')(app, Contact);
var router2 = require('./routes/location')(app, Location);

// [RUN SERVER]
var server = app.listen(80, function(){
 console.log("Express server has started on port " + 80)
});