

module.exports = function(app, Location)
{    // GET ALL LOCATION
    app.get('/api/locations', function(req,res){
        Location.find(function(err, locations){
            if(err) return res.status(500).send({error: 'database failure'});
            res.json(locations);
        })
    });

    // GET SINGLE LOCATION
    app.get('/api/locations/:index', function(req, res){
        Location.findOne({index: req.params.index}, function(err, location){
            if(err) return res.status(500).json({error: err});
            if(!location) return res.status(404).json({error: 'location not found'});
            res.json(location);
        })
    });
}