var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var contactSchema = new Schema({
    name: String,
    number: String,
    //published_date: { type: Date, default: Date.now  }
});

module.exports = mongoose.model('contact', contactSchema);


//type validation can be added
//https://developer.mozilla.org/en-US/docs/Learn/Server-side/Express_Nodejs/mongoose
