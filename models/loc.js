var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var locationSchema = new Schema({
    index: Number,
    name: String,
    address: String,
    latitude: Number,
    longtitud: Number,
    //published_date: { type: Date, default: Date.now  }
});

module.exports = mongoose.model('location', locationSchema);