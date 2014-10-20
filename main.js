var count = 0;

var devices = {
   iPhone : 0,
   android: 0
};

function calculatePercentage(iPhone, android) { 
    var iPhonePercentage  = (iPhone / total * 100).toFixed();
    var androidPercentage = (android / total * 100).toFixed();
    return {
        iPhone : iPhonePercentage,
        android: androidPercentage 
    };
}      

var socket = new WebSocket("ws://colin.dev.shazamteam.net:7380");

socket.onmessage = function(event) {
    var data = JSON.parse(event.data);
    if (data.platform === 'android') {
        devices.android++;
    } else if (data.platform === 'iphone') {
        devices.iPhone++;
    }
    
    count++;
    
    // Close socket once 100 devices have been logged
    if (count === 100) {
        socket.close();
    }
};

socket.onclose = function(event) {
    var iPhone = devices.iPhone;
    var android = devices.android;
    var percentages = calculatePercentage(iPhone, android);
    
    console.log('iPhone:', percentages.iPhone + '%');
    console.log('Android:', percentages.android + '%');
};

setInterval(function() {
    if (count === 100) {
        // Reset counters
        count = 0;
        devices.iPhone = 0;
        devices.android = 0;          
        // Reopen socket
        socket.open();
    }
}, 5000);