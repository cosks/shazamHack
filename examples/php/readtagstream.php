<?php
require 'vendor/autoload.php';

$client = new Predis\Async\Client('tcp://colin.dev.shazamteam.net:6370');

$client->connect(function ($client) {
    echo "Connected to Redis, now listening for incoming messages...\n";

    $logger = new Predis\Async\Client('tcp://127.0.0.1:6379', $client->getEventLoop());

    $client->pubsub('tags', function ($event) use ($logger) {
		$tag = json_decode($event->payload);
		$title = $tag->{'match'}->{'track'}->{'metadata'}->{'trackTitle'};
		$artist = $tag->{'match'}->{'track'}->{'metadata'}->{'artistName'};
        echo "Tagged `$title` by $artist\n";
    });
});

$client->getEventLoop()->run();
