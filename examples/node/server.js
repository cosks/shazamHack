var redis = require("redis");
var client = redis.createClient(6370, "colin.dev.shazamteam.net");

client.on("error", function(err) {
	console.error("ERROR: ", err);
});

client.on("message", function(channel, message) {
	var tag = JSON.parse(message);
	var trackTitle = tag.match.track.metadata.trackTitle;
	var artist = tag.match.track.metadata.artistName;
	console.log("Tagged '%s' by %s", trackTitle, artist);

});

client.subscribe("tags");
