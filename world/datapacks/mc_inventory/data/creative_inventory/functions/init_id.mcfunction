# copy the score of the fake player to the player
scoreboard players operation @s id = $total id

# count up the score so the next player will get a different id
scoreboard players add $total id 1
