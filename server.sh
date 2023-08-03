#!/bin/bash
cd /home/admin/minecraft
sudo -u admin screen -S minecraft -dm java -Xmx3G -Xms2G -jar server.jar nogui

