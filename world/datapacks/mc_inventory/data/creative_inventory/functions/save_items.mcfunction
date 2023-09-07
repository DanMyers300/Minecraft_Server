# summon marker
summon marker ~ ~ ~ {Tags:["inv_store","inv_new"]}
# link marker to player    
scoreboard players operation @e[tag=inv_new] id = @s id
# copy Inventory of player to marker data.Inventory
data modify entity @e[tag=inv_new,limit=1] data.Inventory set from entity @s Inventory
# remove the new tag to get ready for the next player
tag @e[tag=inv_new] remove inv_new
