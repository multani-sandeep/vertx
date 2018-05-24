#/bin/bash

ps -ef | grep vertx | grep -v "grep " | cut -d' ' -f 5 | xargs kill -9

ps -ef | grep vertx 